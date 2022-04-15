package raytracing.accelerator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.concurrent.TimeUnit;
import raytracing.abstracts.RayAcceleratorInterface;
import raytracing.mesh.RMesh;
import raytracing.structs.RBound;
import raytracing.structs.RIntersection;
import raytracing.structs.RNode;
import raytracing.structs.RRay;
import wrapper.core.CMemory;
import static wrapper.core.CMemory.READ_WRITE;
import wrapper.core.CResourceFactory;
import wrapper.core.OpenCLConfiguration;

/**
 *
 * @author user
 */
public class RNormalBVH implements RayAcceleratorInterface {
    
     //Primitive
    RMesh primitives;
    int[] objects;
    
    //Tree, Primitive index, Boundingbox   
    CMemory<RBound> bounds;
    CMemory<RNode> nodes;
    
    //Node counter
    int nodesPtr = 0;
    
    //Opencl configuration
    OpenCLConfiguration configuration;
    
    public RNormalBVH(OpenCLConfiguration configuration)
    {
        this.configuration = configuration;
    }
    
    @Override
    public void build(RMesh primitives) {
        long time1 = System.nanoTime();
        
        this.primitives = primitives; 
        this.objects = new int[primitives.getSize()];
        
        for(int i = 0; i<this.primitives.getSize(); i++)
            objects[i] = i;
        
        //Release memory
        CResourceFactory.releaseMemory("nodes", "bounds");
        
        //Allocate BVH root node
        nodes   = configuration.createBufferI(RNode.class, this.primitives.getSize() * 2 - 1,  READ_WRITE);
        bounds  = configuration.createBufferF(RBound.class, this.primitives.getSize() * 2 - 1,  READ_WRITE);
        
        RNode root = new RNode();
        nodes.setCL(root);        
        nodesPtr = 1;
                
        subdivide(0, 0, primitives.getSize());
        
        long time2 = System.nanoTime();
        
        System.out.println(nodes.getSize());
        
        long timeDuration = time2 - time1;
        String timeTaken= String.format("BVH build time: %02d min, %02d sec", 
                TimeUnit.NANOSECONDS.toMinutes(timeDuration), 
                TimeUnit.NANOSECONDS.toSeconds(timeDuration));
        System.out.println(timeTaken);    
        
        nodes.transferToDevice();
        bounds.transferToDevice();
    }
    private void subdivide(int parentIndex, int start, int end)
    {
        //Calculate the bounding box for the root node
        RBound bb = new RBound();
        RBound bc = new RBound();
        calculateBounds(start, end, bb, bc);
        
        nodes.index(parentIndex, parent -> parent.bound  = parentIndex);
        bounds.index(parentIndex, bound -> bound.setBound(bb));
            
        //Initialize leaf
        if(end - start < 2)
        {        
            nodes.index(parentIndex, (RNode parent) -> {
                parent.child = objects[start];
                parent.isLeaf = 1;
            });            
            return;
        }
        
         //Subdivide parent node
        RNode left;      
        RNode right;
        int leftIndex, rightIndex;      
        synchronized(this)
        {
            left            = new RNode();   left.parent = parentIndex;
            right           = new RNode();   right.parent = parentIndex;
            
            nodes.set(nodesPtr, left); leftIndex   = nodesPtr;   nodes.index(parentIndex, parent -> parent.left = nodesPtr++); 
            nodes.set(nodesPtr, right); rightIndex  = nodesPtr;   nodes.index(parentIndex, parent -> parent.right = nodesPtr++);  
            
            nodes.index(leftIndex, leftNode -> leftNode.sibling = rightIndex);
            nodes.index(rightIndex, rightNode -> rightNode.sibling = leftIndex);                      
        }   
        
        //set the split dimensions
        int split_dim = bc.maximumExtentAxis();        
        int mid = getMid(bc, split_dim, start, end);
                
        //Subdivide
        subdivide(leftIndex, start, mid);
        subdivide(rightIndex, mid, end);
    }
        
    private int getMid(RBound bc, int split_dim, int start, int end)
    {
        //split on the center of the longest axis
        float split_coord = bc.getCenter(split_dim);

        //partition the list of objects on this split            
        int mid = partition(primitives, objects, start, end, split_dim, split_coord);

        //if we get a bad split, just choose the center...
        if(mid == start || mid == end)
            mid = start + (end-start)/2;
        
        return mid;
    }
    
    private void calculateBounds(int first, int end, RBound bb, RBound bc)
    {                
        for(int p = first; p<end; p++)
        {
            bb.include(primitives.getBound(objects[p]));
            bc.include(primitives.getBound(objects[p]).getCenter());
        }        
    }

    @Override
    public boolean intersect(RRay ray, RIntersection isect) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean intersectP(RRay ray) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void intersect(RRay[] rays, RIntersection[] isects) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RBound getBound() {       
        return bounds.get(0).getCopy();
    }

    @Override
    public CMemory<RBound> getBounds() {
        return bounds;
    }

    @Override
    public CMemory<RNode> getNodes() {
        return nodes;
    }
    
}
