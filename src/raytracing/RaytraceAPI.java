/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing;

import raytracing.envmap.REnvMap;
import raytracing.display.BlendDisplay;
import raytracing.device.RaytraceDevice;
import bitmap.core.AbstractDisplay;
import bitmap.image.BitmapRGBE;

import coordinate.parser.obj.OBJInfo;
import coordinate.parser.obj.OBJMappedParser;
import coordinate.parser.obj.OBJParser;
import coordinate.utility.Plugins;
import coordinate.utility.Timer;
import coordinate.utility.Value2Di;
import java.io.File;
import java.nio.file.Path;
import jfx.util.UtilityHandler;
import org.jocl.CL;
import raytracing.abstracts.RayAPI;
import raytracing.abstracts.RayAPI.RayDeviceType;
import static raytracing.abstracts.RayAPI.RayDeviceType.RAYTRACE;
import static raytracing.abstracts.RayAPI.ImageType.ALL_RAYTRACE_IMAGE;
import raytracing.abstracts.RayDeviceInterface;
import raytracing.accelerator.RNormalBVH;
import raytracing.cl.RaytraceSource;
import raytracing.geom.RPoint2;
import raytracing.geom.RPoint3;
import raytracing.geom.RVector3;
import raytracing.mesh.RMesh;
import raytracing.mesh.RTriangle;
import raytracing.structs.RBound;
import raytracing.structs.RConfig;
import raytracing.structs.RMaterial;
import raytracing.structs.RRay;
import wrapper.core.CMemory;
import wrapper.core.OpenCLConfiguration;
import wrapper.util.CLOptions;

/**
 *
 * @author user
 * 
 * Class to link between raydevice, ui and any other frameworks e.g. I/O
 * 
 */
public class RaytraceAPI implements RayAPI<RaytraceUIController, RPoint3, RVector3, RPoint2, RRay, RTriangle>{
    //Opencl configuration for running single ray tracing program 
    private OpenCLConfiguration configuration = null;
    
    //The display to be used, is independent of ui since it can be file writer (jpg, png,...) display
    private BlendDisplay displayRT = null;
    
    //Controller 
    private RaytraceUIController controllerImplementation = null;
   
    //Add unique objects here base on class type
    private final Plugins plugins = new Plugins();
    
    //device priority
    private RayDeviceType devicePriority = RAYTRACE;
    
    //mesh and accelerator
    private RMesh mesh;
    private RNormalBVH bvhBuild;
        
    //environment map
    private REnvMap envmap;
    
    //raytracing and rendering configuration
    private RConfig configRay;
    
    public RaytraceAPI()
    {
        CL.setExceptionsEnabled(true);
         
        //opencl configuration
        this.initOpenCLConfiguration();
    }
    
    @Override
    public void initOpenCLConfiguration() {
        if(configuration != null) 
            return;
        CLOptions options = CLOptions.include("build/classes");
        
        System.out.println(new File("").getAbsolutePath());
        
        configuration = OpenCLConfiguration.getDefault(options, RaytraceSource.readFiles());
    }

    @Override
    public OpenCLConfiguration getConfigurationCL() {
        return configuration;
    }

    @Override
    public void init() {        
        //render config
        configRay = new RConfig();        
        
        //create bitmap images
        this.initBitmap(ALL_RAYTRACE_IMAGE);
        
        //instantiate devices
        plugins.addPluginClass(RaytraceDevice.class, new RaytraceDevice(
                this.configRay.resolutionR.x, 
                this.configRay.resolutionR.y));
                
        //init default mesh before api
        initDefaultMesh();        
        //setup ui materialfx after init default mesh
        controllerImplementation.setupCurrentMaterialFX();
        
         //envmap
        envmap = new REnvMap(configuration);
        
        //set api       
        //TODO: Investigate why not use setDevice (throws exception)
        plugins.get(RaytraceDevice.class).setAPI(this);
        
        //start ray tracing
        plugins.get(RaytraceDevice.class).start();
    }
    
    public RConfig getRayConfiguration()
    {
        return configRay;
    }

    @Override
    public <D extends AbstractDisplay> D getDisplay(Class<D> displayClass) {
        if(BlendDisplay.class.isAssignableFrom(displayClass))        
            return (D) displayRT;
        else
            throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <D extends AbstractDisplay> void setDisplay(Class<D> displayClass, D display) {
        if(BlendDisplay.class.isAssignableFrom(displayClass))        
            displayRT = (BlendDisplay) display;
        else
            throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Value2Di getImageSize(ImageType imageType) {
        switch (imageType) {            
            case RAYTRACE_IMAGE:
                return new Value2Di(configRay.getResolutionR().x, configRay.getResolutionR().y);
            default:
                return null;
        }
    }

    @Override
    public void setImageSize(ImageType imageType, int width, int height) {
        switch (imageType) {            
            case RAYTRACE_IMAGE:
                this.configRay.setResolutionR(width, height);
            default:
                break;
        }
    }

    @Override
    public int getGlobalSizeForDevice(RayDeviceType device) {
        switch (device) {
            case RAYTRACE:
                return this.configRay.getResolutionRSize();            
            default:
                return -1;
        }
    }

    @Override
    public void render(RayDeviceType device) {
        if(device.equals(RAYTRACE))
            plugins.get(RaytraceDevice.class).start();
    }
    
    @Override
    public void initMesh(Path path) {
        //load mesh and init mesh variables        
        OBJMappedParser parser = new OBJMappedParser();        
        parser.readAttributes(path.toUri());
        
        boolean succeed = UtilityHandler.runJavaFXThread(()->{
            //init size (estimate) of coordinate list/array
            OBJInfo info = parser.getInfo();
            return controllerImplementation.showOBJStatistics(info);
        });
        
        if(succeed)
        {
            mesh = null;
            mesh = new RMesh(getConfigurationCL());
            
            //init array sizes (eventually they will grow if bounds reach)
            mesh.initCoordList(RPoint3.class, RVector3.class, RPoint2.class, 0, 0, 0, 0);

            Timer parseTime = Timer.timeThis(() -> parser.read(path.toString(), mesh)); //Time parsing
            //UI.print("parse-time", parseTime.toString()); 
            mesh.initCLBuffers();

            //display material in ui
            //controllerImplementation.displaySceneMaterial(parser.getSceneMaterialList());

            //build accelerator
            Timer buildTime = Timer.timeThis(() -> {                                   //Time building
                this.setMessage("building accelerator");
                this.bvhBuild = new RNormalBVH(configuration);
                this.bvhBuild.build(mesh);      
            });
           // UI.print("build-time", buildTime.toString());

            //set to device for rendering/raytracing
            plugins.get(RaytraceDevice.class).set(mesh, bvhBuild);
            
            //init kernels with new buffers to reflect on new model            
            plugins.get(RaytraceDevice.class).initKernels();
            
            //reposition camera
            this.repositionCameraToSceneRT();
        }
    }

    
    @Override
    public void initDefaultMesh() {
        //A simple cornell box
        String cube =   "# Cornell Box\n" +
                        "o Floor\n" +
                        "v 1.014808 -1.001033 -0.985071\n" +
                        "v -0.995168 -1.001033 -0.994857\n" +
                        "v -1.005052 -1.001033 1.035119\n" +
                        "v 0.984925 -1.001033 1.044808\n" +
                        "vn 0.0000 1.0000 -0.0000\n" +
                        "s off\n" +
                        "f 1//1 2//1 3//1 4//1\n" +
                        "o Ceiling\n" +
                        "v 1.024808 0.988967 -0.985022\n" +
                        "v 1.014924 0.988967 1.044954\n" +
                        "v -1.005052 0.988967 1.035119\n" +
                        "v -0.995168 0.988967 -0.994857\n" +
                        "vn -0.0000 -1.0000 0.0000\n" +
                        "s off\n" +
                        "f 5//2 6//2 7//2 8//2\n" +
                        "o BackWall\n" +
                        "v 0.984925 -1.001033 1.044808\n" +
                        "v -1.005052 -1.001033 1.035119\n" +
                        "v -1.005052 0.988967 1.035119\n" +
                        "v 1.014924 0.988967 1.044954\n" +
                        "vn 0.0049 -0.0000 -1.0000\n" +
                        "s off\n" +
                        "f 9//3 10//3 11//3 12//3\n" +
                        "o RightWall\n" +
                        "v -1.005052 -1.001033 1.035119\n" +
                        "v -0.995168 -1.001033 -0.994857\n" +
                        "v -0.995168 0.988967 -0.994857\n" +
                        "v -1.005052 0.988967 1.035119\n" +
                        "vn 1.0000 0.0000 0.0049\n" +
                        "s off\n" +
                        "f 13//4 14//4 15//4 16//4\n" +
                        "o SmallBox\n" +
                        "v -0.526342 -0.401033 -0.752572\n" +
                        "v -0.699164 -0.401033 -0.173406\n" +
                        "v -0.129998 -0.401033 -0.000633\n" +
                        "v 0.052775 -0.401033 -0.569750\n" +
                        "v 0.052775 -1.001033 -0.569750\n" +
                        "v 0.052775 -0.401033 -0.569750\n" +
                        "v -0.129998 -0.401033 -0.000633\n" +
                        "v -0.129998 -1.001033 -0.000633\n" +
                        "v -0.526342 -1.001033 -0.752572\n" +
                        "v -0.526342 -0.401033 -0.752572\n" +
                        "v 0.052775 -0.401033 -0.569750\n" +
                        "v 0.052775 -1.001033 -0.569750\n" +
                        "v -0.699164 -1.001033 -0.173406\n" +
                        "v -0.699164 -0.401033 -0.173406\n" +
                        "v -0.526342 -0.401033 -0.752572\n" +
                        "v -0.526342 -1.001033 -0.752572\n" +
                        "v -0.129998 -1.001033 -0.000633\n" +
                        "v -0.129998 -0.401033 -0.000633\n" +
                        "v -0.699164 -0.401033 -0.173406\n" +
                        "v -0.699164 -1.001033 -0.173406\n" +
                        "vn 0.0000 1.0000 -0.0000\n" +
                        "vn 0.9521 0.0000 0.3058\n" +
                        "vn 0.3010 -0.0000 -0.9536\n" +
                        "vn -0.2905 0.0000 0.9569\n" +
                        "vn -0.9582 0.0000 -0.2859\n" +
                        "s off\n" +
                        "f 17//5 18//5 19//5 20//5\n" +
                        "f 21//6 22//6 23//6 24//6\n" +
                        "f 25//7 26//7 27//7 28//7\n" +
                        "f 33//8 34//8 35//8 36//8\n" +
                        "f 29//9 30//9 31//9 32//9\n" +
                        "o TallBox\n" +
                        "v 0.530432 0.198967 -0.087419\n" +
                        "v -0.040438 0.198967 0.089804\n" +
                        "v 0.136736 0.198967 0.670674\n" +
                        "v 0.707606 0.198967 0.493451\n" +
                        "v 0.530432 -1.001033 -0.087418\n" +
                        "v 0.530432 0.198967 -0.087419\n" +
                        "v 0.707606 0.198967 0.493451\n" +
                        "v 0.707606 -1.001033 0.493451\n" +
                        "v 0.707606 -1.001033 0.493451\n" +
                        "v 0.707606 0.198967 0.493451\n" +
                        "v 0.136736 0.198967 0.670674\n" +
                        "v 0.136736 -1.001033 0.670674\n" +
                        "v 0.136736 -1.001033 0.670674\n" +
                        "v 0.136736 0.198967 0.670674\n" +
                        "v -0.040438 0.198967 0.089804\n" +
                        "v -0.040438 -1.001033 0.089804\n" +
                        "v -0.040438 -1.001033 0.089804\n" +
                        "v -0.040438 0.198967 0.089804\n" +
                        "v 0.530432 0.198967 -0.087419\n" +
                        "v 0.530432 -1.001033 -0.087418\n" +
                        "vn -0.0000 1.0000 -0.0000\n" +
                        "vn 0.9565 0.0000 -0.2917\n" +
                        "vn 0.2965 0.0000 0.9550\n" +
                        "vn -0.9565 0.0000 0.2917\n" +
                        "vn -0.2965 -0.0000 -0.9550\n" +
                        "s off\n" +
                        "f 37//10 38//10 39//10 40//10\n" +
                        "f 41//11 42//11 43//11 44//11\n" +
                        "f 45//12 46//12 47//12 48//12\n" +
                        "f 49//13 50//13 51//13 52//13\n" +
                        "f 53//14 54//14 55//14 56//14\n" +
                        "o Light\n" +
                        "v 0.240776 0.978967 -0.158830\n" +
                        "v 0.238926 0.978967 0.221166\n" +
                        "v -0.231068 0.978967 0.218877\n" +
                        "v -0.229218 0.978967 -0.161118\n" +
                        "vn 0.0000 -1.0000 0.0000\n" +
                        "s off\n" +
                        "f 57//15 58//15 59//15 60//15\n" +
                        "o LeftWall\n" +
                        "v 1.014808 -1.001033 -0.985071\n" +
                        "v 0.984925 -1.001033 1.044808\n" +
                        "v 1.014924 0.988967 1.044954\n" +
                        "v 1.024808 0.988967 -0.985022\n" +
                        "vn -0.9999 0.0100 -0.0098\n" +
                        "s off\n" +
                        "f 61//16 62//16 63//16 64//16";
        
        //load mesh and init mesh variables
        mesh = new RMesh(configuration);   
        
        //parser info and attributes
        OBJParser parser = new OBJParser(); 
        parser.readAttributesString(cube); 
        parser.readString(cube, mesh);
        
        mesh.initCLBuffers();
        
        //modify materials
        /*
            0 floor
            1 ceiling
            2 back wall
            3 right wall
            4 small box
            5 tall box
            6 emitter
            7 left wall            
        */
        
        
        //NEW MATERIAL
        CMemory<RMaterial> materialsc =  mesh.clMaterials();
        
        RMaterial emitterc = materialsc.get(6);
        //emitter.setDiffuse(0.7647f, 0.6902f, 0.5686f);  //khaki            
        emitterc.setEmitter(1f, 1f, 1f);
               
        RMaterial rightc = materialsc.get(3);           
        rightc.setDiffuse(0, 0.8f, 0);

        RMaterial leftc = materialsc.get(7);         
        leftc.setDiffuse(0.8f, 0f, 0);

        RMaterial backc = materialsc.get(2);           
        backc.setDiffuse(0.7647f, 0.6902f, 0.5686f);  //khaki

        RMaterial ceilingc = materialsc.get(1);           
        ceilingc.setDiffuse(0.7647f, 0.6902f, 0.5686f);  //khaki

        RMaterial floorc = materialsc.get(0);           
        floorc.setDiffuse(0.7647f, 0.6902f, 0.5686f);  //khaki

        RMaterial smallboxc = materialsc.get(4);  
        smallboxc.setDiffuse(0.7647f, 0.6902f, 0.5686f);  //khaki
        //smallbox.setEmitter(1, 1, 1);
        //smallbox.setEmitterEnabled(true);

        RMaterial tallboxc = materialsc.get(5);           
        tallboxc.setDiffuse(0.7647f, 0.6902f, 0.5686f);  //khaki  
        
        //transfer material to device
        materialsc.transferToDevice();
        
        materialsc.getBufferArray();
        
                
        //build accelerator
        bvhBuild = new RNormalBVH(configuration);
        bvhBuild.build(mesh);  
        
        //set to device for rendering/raytracing
        plugins.get(RaytraceDevice.class).set(mesh, bvhBuild);        
    }
    
    @Override
    public RMesh getCurrentMesh()
    {
        return mesh;
    }
    
    @Override
    public void startDevice(RayDeviceType device) {
        if(device.equals(RAYTRACE))
            plugins.get(RaytraceDevice.class).start();
        else
            throw new UnsupportedOperationException("issue with device type which is not recognised");
    }

    @Override
    public void pauseDevice(RayDeviceType device) {
        if(device.equals(RAYTRACE))
            plugins.get(RaytraceDevice.class).pause();
        else
            throw new UnsupportedOperationException("issue with device type which is not recognised");
    }

    @Override
    public void stopDevice(RayDeviceType device) {
        if(device.equals(RAYTRACE))
            plugins.get(RaytraceDevice.class).stop();
        else
            System.out.println("no device");
    }

    @Override
    public void resumeDevice(RayDeviceType device) {
        if(device.equals(RAYTRACE))
            plugins.get(RaytraceDevice.class).resume();
        else
            System.out.println("no device");
    }

    @Override
    public boolean isDeviceRunning(RayDeviceType device) {
        switch (device) {
            case RAYTRACE:
                return plugins.get(RaytraceDevice.class).isRunning();           
            default:
                throw new UnsupportedOperationException(device+ " not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void setDevicePriority(RayDeviceType device) {
        this.devicePriority = device;
    }

    @Override
    public RayDeviceType getDevicePriority() {
        return this.devicePriority;
    }

    @Override
    public boolean isDevicePriority(RayDeviceType device) {
        return devicePriority.equals(device);
    }

    @Override
    public <Device extends RayDeviceInterface> Device getDevice(Class<Device> clazz){       
        return plugins.get(clazz);       
    }
    
    @Override
    public <Device extends RayDeviceInterface> void setDevice(Class<Device> clazz, Device device) {        
        plugins.addPluginClass(clazz, device);        
        device.setAPI(this);               
    }

    @Override
    public RaytraceUIController getController(String controller) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set(String controller, RaytraceUIController controllerImplementation) {
        this.controllerImplementation = controllerImplementation;        
        this.controllerImplementation.setAPI(this);
    }

    @Override
    public void setEnvironmentMap(BitmapRGBE bitmap) {
        envmap.setEnvironmentMap(bitmap);         
    }

    public void setIsEnvmapPresent(boolean value)
    {
        envmap.setIsPresent(value);
    }
    
    
    public REnvMap getEnvironmentalMapCL()
    {
        return envmap;
    }
    
    //this changes direction (by reseting direction to z axis)
    public void repositionCameraToSceneRT()
    {
        RaytraceDevice device = getDevice(RaytraceDevice.class);
        RBound bound = device.getBound();
        device.setPriorityBound(bound);
        device.getCameraModel().set(bound.getCenter().add(new RVector3(0, 0, -3)), new RPoint3(), new RVector3(0, 1, 0), 45);
        device.reposition(bound);
    }
   
    
    //this changes only position but direction remains intact
    public void repositionCameraToBoundRT(RBound bound)
    {
        RaytraceDevice device = getDevice(RaytraceDevice.class);
        device.setPriorityBound(bound);
        device.reposition(bound);
    }
}
