/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.device;


import raytracing.accelerator.RNormalBVH;
import bitmap.image.BitmapARGB;
import coordinate.model.OrientationModel;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import raytracing.display.BlendDisplay;
import raytracing.display.Overlay;
import raytracing.RaytraceAPI;
import raytracing.RaytraceEnvironment;
import static raytracing.abstracts.RayAPI.ImageType.RAYTRACE_IMAGE;
import static raytracing.abstracts.RayAPI.getGlobal;
import raytracing.abstracts.RayDeviceInterface;
import raytracing.geom.RPoint2;
import raytracing.geom.RPoint3;
import raytracing.geom.RVector3;
import static raytracing.device.RaytraceDevice.ShadeType.COLOR_SHADE;
import raytracing.mesh.RMesh;
import raytracing.structs.RBound;
import raytracing.structs.RBsdf;
import raytracing.structs.RCamera;
import raytracing.structs.RCameraModel;
import raytracing.structs.RIntersection;
import raytracing.structs.RMaterial;
import raytracing.structs.RRay;
import raytracing.structs.RTextureData;
import raytracing.texture.RTextureApplyPass;
import thread.model.LambdaThread;
import wrapper.core.CKernel;
import wrapper.core.CMemory;
import static wrapper.core.CMemory.READ_WRITE;
import wrapper.core.OpenCLConfiguration;
import wrapper.core.memory.values.FloatValue;
import wrapper.core.memory.values.IntValue;

/**
 *
 * @author user
 */
public class RaytraceDevice implements RayDeviceInterface<
        RaytraceAPI, 
        BlendDisplay, 
        RMaterial, 
        RMesh,
        RNormalBVH,
        RBound,
        RCameraModel, 
        RCamera>{
    
    OpenCLConfiguration configuration;
    BlendDisplay display;
    AtomicBoolean isRendering = new AtomicBoolean(false);
    
    public enum ShadeType{COLOR_SHADE, NORMAL_SHADE, TEXTURE_SHADE};    
    ShadeType shadeType = COLOR_SHADE;
    
    //API
    RaytraceAPI api;
    
    //render thread
    LambdaThread raytraceThread = new LambdaThread();
    
    RCameraModel cameraModel = new RCameraModel(new RPoint3(0, 0, -9), new RPoint3(), new RVector3(0, 1, 0), 45);
    RMesh mesh = null;
    RNormalBVH bvh = null;
    
    private final int width;
    private final int height;
    private final BitmapARGB raytraceBitmap;
    private final Overlay overlay;
    
    //global and local size
    int globalWorkSize, localWorkSize;
    
    //priority bound, that is the main focus for the ray trace (e.g. selected object)
    RBound priorityBound;
    
    //CL
    CMemory<IntValue> imageBuffer = null;      
    CMemory<RCamera> cameraBuffer = null;    
    CMemory<RRay> raysBuffer = null;
    CMemory<RIntersection> isectBuffer = null;
    CMemory<IntValue> count = null;
    CMemory<IntValue> groupBuffer = null;
    CMemory<RTextureData> texBuffer = null;
    CMemory<RBsdf> bsdfBuffer = null;
    
    CKernel initCameraRaysKernel = null;
    CKernel intersectPrimitivesKernel = null;    
    CKernel backgroundShadeKernel = null;
    CKernel updateGroupbufferShadeImageKernel = null;
    CKernel textureInitPassKernel = null;
    CKernel updateToTextureColorRTKernel = null;
    CKernel setupBSDFRaytraceKernel = null;
    CKernel fastShadeKernel = null;
    CKernel fastShadeNormalsKernel = null;
    CKernel fastShadeTextureUVKernel = null;
    
    RTextureApplyPass texApplyPass = null;
    
    //environment map if any
    RaytraceEnvironment envmap = null;
    
    //on screen intersection mouse click
    CMemory<IntValue> groupIndex = null;  
    CMemory<FloatValue> groupBound = null;
    CKernel findBoundKernel = null;
    
    public RaytraceDevice(int w, int h)
    {
        this.width = w; 
        this.height = h;
        this.raytraceBitmap = new BitmapARGB(w, h);
        this.overlay = new Overlay(w, h);
        this.globalWorkSize = width * height;
        this.localWorkSize = 250;
    }

    @Override
    public void setAPI(RaytraceAPI api) {
        this.api = api;
        init(api.getConfigurationCL(), api.getDisplay(BlendDisplay.class));
    }
    
    public void initBuffers()
    {
        raysBuffer          = configuration.createBufferB(RRay.class, globalWorkSize, READ_WRITE);
        cameraBuffer        = configuration.createBufferB(RCamera.class, 1, READ_WRITE);
        count               = configuration.createFromI(IntValue.class, new int[]{globalWorkSize}, READ_WRITE);
        isectBuffer         = configuration.createBufferB(RIntersection.class, globalWorkSize, READ_WRITE);
        imageBuffer         = configuration.createBufferI(IntValue.class, globalWorkSize, READ_WRITE);        
        groupBuffer         = configuration.createBufferI(IntValue.class, globalWorkSize, READ_WRITE);
        texBuffer           = configuration.createBufferI(RTextureData.class, globalWorkSize, READ_WRITE);
        bsdfBuffer          = configuration.createBufferB(RBsdf.class, globalWorkSize, READ_WRITE);
        texApplyPass        = new RTextureApplyPass(api, texBuffer, count);
        
        groupIndex          = configuration.createBufferI(IntValue.class, 1, READ_WRITE);
        groupBound          = configuration.createBufferF(FloatValue.class, 6, READ_WRITE);
    }
    
    public void initKernels()
    {        
        initCameraRaysKernel                = configuration.createKernel("InitCameraRayData", cameraBuffer, raysBuffer);
        intersectPrimitivesKernel           = configuration.createKernel("IntersectPrimitives", raysBuffer, isectBuffer, count, mesh.clPoints(), mesh.clTexCoords(), mesh.clNormals(), mesh.clFaces(), mesh.clSize(), bvh.getNodes(), bvh.getBounds());
        fastShadeKernel                     = configuration.createKernel("fastShade", isectBuffer, bsdfBuffer, imageBuffer);
        fastShadeNormalsKernel              = configuration.createKernel("fastShadeNormals", isectBuffer, imageBuffer);
        fastShadeTextureUVKernel            = configuration.createKernel("fastShadeTextureUV", isectBuffer, imageBuffer);
        backgroundShadeKernel               = configuration.createKernel("backgroundShade", isectBuffer, cameraBuffer, imageBuffer, raysBuffer, envmap.getRgbCL(), envmap.getEnvMapSize());
        updateGroupbufferShadeImageKernel   = api.getConfigurationCL().createKernel("updateGroupbufferShadeImage", isectBuffer, cameraBuffer, groupBuffer);
        textureInitPassKernel               = configuration.createKernel("textureInitPassRT", bsdfBuffer, isectBuffer, texBuffer);
        setupBSDFRaytraceKernel             = configuration.createKernel("SetupBSDFRaytrace", isectBuffer, raysBuffer, bsdfBuffer, mesh.clMaterials());
        updateToTextureColorRTKernel        = configuration.createKernel("updateToTextureColorRT", bsdfBuffer, texBuffer);
        findBoundKernel                     = configuration.createKernel("findBound", groupIndex, mesh.clPoints(),  mesh.clTexCoords(), mesh.clNormals(), mesh.clFaces(), mesh.clSize(), groupBound);
    }
    
    public void findBound(int instance, RBound bound)
    {
        groupBound.mapWriteMemory(buffer->{
            buffer.set(0, new FloatValue(Float.POSITIVE_INFINITY));
            buffer.set(1, new FloatValue(Float.POSITIVE_INFINITY));
            buffer.set(2, new FloatValue(Float.POSITIVE_INFINITY));
            buffer.set(3, new FloatValue(Float.NEGATIVE_INFINITY));
            buffer.set(4, new FloatValue(Float.NEGATIVE_INFINITY));
            buffer.set(5, new FloatValue(Float.NEGATIVE_INFINITY));
        });
        groupIndex.setCL(new IntValue(instance));
        
        int local = 128;
        int global = getGlobal(mesh.clSize().getCL().v, local);
        
        configuration.execute1DKernel(findBoundKernel, global, local);
        
        //return bound and mark changes
        groupBound.mapReadMemory((buffer)->{
            bound.minimum.x = buffer.get(0).v;
            bound.minimum.y = buffer.get(1).v;
            bound.minimum.z = buffer.get(2).v;
            bound.maximum.x = buffer.get(3).v;
            bound.maximum.y = buffer.get(4).v;
            bound.maximum.z = buffer.get(5).v;
        });
        
    }
    
    public boolean isCoordinateAnInstance(double x, double y)
    {
        return overlay.isInstance(x, y);
    }
    
    public int getInstanceValue(double x, double y)
    {
        return overlay.get(x, y);
    }
    
    public Overlay getOverlay()
    {
        return overlay;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    @Override
    public void updateImage() {      
        //transfer data from opencl to cpu
        imageBuffer.transferFromDevice();
        groupBuffer.transferFromDevice();
        //write to bitmap and overlay
        raytraceBitmap.writeColor((int[]) imageBuffer.getBufferArray(), 0, 0, width, height);
        overlay.copyToArray((int[])groupBuffer.getBufferArray());
        //image fill
        Platform.runLater(()-> display.imageFill(RAYTRACE_IMAGE.name(), raytraceBitmap));
        
    }

    @Override
    public void set(RMesh mesh, RNormalBVH bvhBuild) {
        this.mesh = mesh;
        this.bvh = bvhBuild;        
        this.priorityBound = bvhBuild.getBound();
    }

    @Override
    public void setGlobalSize(int globalSize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLocalSize(int localSize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void execute() {        
        if(cameraModel.isSynched(cameraBuffer.get(0)))
            raytraceThread.chill();       
        updateCamera();
        configuration.execute1DKernel(initCameraRaysKernel, globalWorkSize, localWorkSize);
        configuration.execute1DKernel(intersectPrimitivesKernel, globalWorkSize, localWorkSize);
        configuration.execute1DKernel(setupBSDFRaytraceKernel, globalWorkSize, localWorkSize);
        
        //pass texture
        configuration.execute1DKernel(textureInitPassKernel, globalWorkSize, localWorkSize);
        texApplyPass.process();
        configuration.execute1DKernel(updateToTextureColorRTKernel, globalWorkSize, localWorkSize);
        
        configuration.execute1DKernel(backgroundShadeKernel, globalWorkSize, localWorkSize); 
        
        //shade type
        if(null == shadeType)
            configuration.execute1DKernel(fastShadeKernel, globalWorkSize, localWorkSize);
        else switch (shadeType) {
            case COLOR_SHADE:
                configuration.execute1DKernel(fastShadeKernel, globalWorkSize, localWorkSize);
                break;
            case NORMAL_SHADE:
                configuration.execute1DKernel(fastShadeNormalsKernel, globalWorkSize, localWorkSize);
                break;
            case TEXTURE_SHADE:
                configuration.execute1DKernel(fastShadeTextureUVKernel, globalWorkSize, localWorkSize);
                break;                
            default:
                configuration.execute1DKernel(fastShadeKernel, globalWorkSize, localWorkSize);
                break;
        }
        
        configuration.execute1DKernel(updateGroupbufferShadeImageKernel, globalWorkSize, localWorkSize);
        updateImage();
        configuration.finish();      
    }

    @Override
    public void pause() {
        raytraceThread.pauseExecution();
    }

    @Override
    public void stop() {
        raytraceThread.stopExecution();
    }

    @Override
    public void resume() {
        raytraceThread.resumeExecution();
    }

    @Override
    public boolean isPaused() {
        return raytraceThread.isPaused();
    }

    @Override
    public boolean isRunning() {
        return !raytraceThread.isPaused();
    }

    @Override
    public boolean isStopped() {
        return raytraceThread.isTerminated();
    }

    @Override
    public void updateCamera() {
        RCamera cam = cameraModel.getCameraStruct();
        cam.setDimension(new RPoint2(getWidth(), getHeight()));
        cameraBuffer.setCL(cam);
    }

    @Override
    public void setCamera(RCamera cameraData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public RBound getBound(){
        return bvh.getBound();
    } 
    
    @Override
    public RBound getPriorityBound()
    {
        return priorityBound;
    }

    @Override
    public RCameraModel getCameraModel() {
        return cameraModel;
    }
    
    private void init(OpenCLConfiguration platform, BlendDisplay display)
    {
        this.configuration = platform;
        this.display = display;
        this.envmap = api.getEnvironmentalMapCL();
        initBuffers();
        initKernels();        
    }
    
    public void reposition(RBound box)
    {
        OrientationModel<RPoint3, RVector3, RRay, RBound> orientation = new OrientationModel(RPoint3.class, RVector3.class);
        orientation.repositionLocation(cameraModel, box);     
    }    
    
    public RMesh getMesh()
    {
        return mesh;
    }
    
    public RNormalBVH getBVH()
    {
        return bvh;
    }
    
    @Override
    public void start()
    {     
       raytraceThread.startExecution(()-> {
            execute();
            raytraceThread.pauseExecution();       
        });      
    }
    
    public void setShadeType(ShadeType shadeType)
    {
        this.shadeType = shadeType;
        this.resume();
    }
}
