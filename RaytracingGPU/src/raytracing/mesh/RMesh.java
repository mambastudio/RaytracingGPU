/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.mesh;

import raytracing.accelerator.RNormalBVH;
import coordinate.generic.AbstractMesh;
import coordinate.generic.raytrace.AbstractPrimitive;
import coordinate.list.CoordinateFloatList;
import coordinate.list.IntList;
import coordinate.parser.attribute.MaterialT;
import raytracing.coordinates.RPoint2;
import raytracing.coordinates.RPoint3;
import raytracing.coordinates.RVector3;
import raytracing.structs.RBound;
import raytracing.structs.RFace;
import raytracing.structs.RIntersection;
import raytracing.structs.RMaterial;
import raytracing.structs.RRay;
import wrapper.core.CMemory;
import static wrapper.core.CMemory.READ_ONLY;
import static wrapper.core.CMemory.READ_WRITE;
import wrapper.core.OpenCLConfiguration;
import wrapper.core.memory.values.IntValue;

/**
 *
 * @author user
 */
public class RMesh extends AbstractMesh<RPoint3, RVector3, RPoint2> implements AbstractPrimitive
        <RPoint3,         
         RRay, 
         RIntersection, 
         RNormalBVH, 
         RBound> {
       
    private final OpenCLConfiguration configuration;
    
    //opencl mesh bound
    private final RBound bounds;
    
    //opencl mesh data
    private CMemory<RPoint3> pointsBuffer = null;
    private CMemory<RPoint2> uvsBuffer = null;
    private CMemory<RVector3> normalsBuffer = null;
    private CMemory<RFace> facesBuffer = null;
    private CMemory<IntValue> sizeBuffer = null;
    
    //materials
    private CMemory<RMaterial> cmaterialsc = null;
        
    public RMesh(OpenCLConfiguration configuration)
    {
        this.configuration = configuration;
        
        points = new CoordinateFloatList(RPoint3.class);
        normals = new CoordinateFloatList(RVector3.class);
        texcoords = new CoordinateFloatList(RPoint2.class);
        triangleFaces = new IntList();
        bounds = new RBound();
    }

    @Override
    public int getSize() {
        return triangleSize();
    }

    @Override
    public RBound getBound(int primID) {
        RBound bbox = new RBound();
        bbox.include(getVertex1(primID));
        bbox.include(getVertex2(primID));
        bbox.include(getVertex3(primID));
        return bbox; 
    }

    @Override
    public RPoint3 getCentroid(int primID) {
        return getBound(primID).getCenter();
    }

    @Override
    public RBound getBound() {
        return bounds;
    }

    @Override
    public boolean intersect(RRay r, int primID, RIntersection isect) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean intersectP(RRay r, int primID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean intersect(RRay r, RIntersection isect) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean intersectP(RRay r) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public RNormalBVH getAccelerator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void buildAccelerator() {
        //this.accelerator = new RNormalBVH();
        //this.accelerator.build(this);
    }

    @Override
    public float getArea(int primID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addPoint(RPoint3 p) {
        points.add(p);
        bounds.include(p);
    }

    @Override
    public void addPoint(float... values) {
        RPoint3 p = new RPoint3(values[0], values[1], values[2]);
        points.add(p);
        bounds.include(p);
    }

    @Override
    public void addNormal(RVector3 n) {
        normals.add(n);
    }

    @Override
    public void addNormal(float... values) {
        RVector3 n = new RVector3(values[0], values[1], values[2]);
        normals.add(n);
    }

    @Override
    public void addTexCoord(RPoint2 t) {
        texcoords.add(t);
    }

    @Override
    public void addTexCoord(float... values) {
        RPoint2 p = new RPoint2(values[0], values[1]);
        texcoords.add(p);
    }
    
    public void initCLBuffers()
    {
        pointsBuffer = configuration.createFromF(RPoint3.class, getPointArray(), READ_ONLY); 
        uvsBuffer = configuration.createFromF(RPoint2.class, getTexCoordsArray(), READ_ONLY);
        normalsBuffer = configuration.createFromF(RVector3.class, getNormalArray(), READ_ONLY);
        facesBuffer = configuration.createFromI(RFace.class, getTriangleFacesArray(), READ_ONLY);
        sizeBuffer = configuration.createFromI(IntValue.class, new int[]{triangleSize()}, READ_ONLY);        
        cmaterialsc = configuration.createBufferB(RMaterial.class, this.getMaterialList().size(), READ_WRITE); 
        cmaterialsc.mapWriteIterator(materialArray -> {
            int i = 0;
            for(RMaterial cmat : materialArray)
            {
                cmat.setMaterial(new RMaterial()); //init array like any other opencl array
                MaterialT mat = this.getMaterialList().get(i);   
                cmat.setMaterialT(mat);                
                i++;
            }
        });        
    }
        
    public CMemory<RPoint3> clPoints()
    {
        return pointsBuffer;
    }
    
    public CMemory<RPoint2> clTexCoords()
    {
        return uvsBuffer;
    }
    
    public CMemory<RVector3> clNormals()
    {
        return normalsBuffer;
    }
    
    public CMemory<RFace> clFaces()
    {
        return facesBuffer;
    }
    
    public CMemory<IntValue> clSize()
    {
        return sizeBuffer;
    }
        
    public CMemory<RMaterial> clMaterials()
    {
        cmaterialsc.transferFromDevice();
        return cmaterialsc;
    }
        
    public void setMaterial(int index, RMaterial material)    
    {
        cmaterialsc.set(index, material);
        cmaterialsc.transferToDevice();
    }
    
    public RMaterial getMaterial(int index)
    {
        return cmaterialsc.get(index);
    }
   
}
