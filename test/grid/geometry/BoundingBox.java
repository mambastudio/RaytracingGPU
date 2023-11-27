/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.geometry;

import coordinate.memory.type.LayoutGroup;
import coordinate.memory.type.LayoutMemory;
import coordinate.memory.type.LayoutMemory.PathElement;
import coordinate.memory.type.MemoryRegion;
import coordinate.memory.type.StructBase;
import coordinate.memory.type.ValueState;
import coordinate.shapes.AlignedBBoxShape;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 *
 * @author user
 */
public class BoundingBox implements StructBase<BoundingBox>, AlignedBBoxShape<Point4f, Vector4f, Ray, BoundingBox>{
    
    public final Point4f minimum;
    public final Point4f maximum;
    
    private final static LayoutMemory layout = LayoutGroup.groupLayout(
            new Point4f().getLayout().withId("minimum"),
            new Point4f().getLayout().withId("maximum")
    );  
    
    private static ValueState min_xState = layout.valueState(PathElement.groupElement("minimum"), PathElement.groupElement("x"));
    private static ValueState min_yState = layout.valueState(PathElement.groupElement("minimum"), PathElement.groupElement("y"));
    private static ValueState min_zState = layout.valueState(PathElement.groupElement("minimum"), PathElement.groupElement("z"));
    private static ValueState min_wState = layout.valueState(PathElement.groupElement("minimum"), PathElement.groupElement("w"));
    
    private static ValueState max_xState = layout.valueState(PathElement.groupElement("maximum"), PathElement.groupElement("x"));
    private static ValueState max_yState = layout.valueState(PathElement.groupElement("maximum"), PathElement.groupElement("y"));
    private static ValueState max_zState = layout.valueState(PathElement.groupElement("maximum"), PathElement.groupElement("z"));
    private static ValueState max_wState = layout.valueState(PathElement.groupElement("maximum"), PathElement.groupElement("w"));
    
    public BoundingBox() 
    {
         minimum =  new Point4f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
         maximum =  new Point4f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    }
    
    public BoundingBox(Point4f p) 
    {
        minimum = new Point4f(p);
        maximum = new Point4f(p);
    }
    
    public BoundingBox(Point4f p1, Point4f p2) 
    {     
        minimum = new Point4f(min(p1.x, p2.x), min(p1.y, p2.y), min(p1.z, p2.z));
        maximum = new Point4f(max(p1.x, p2.x), max(p1.y, p2.y), max(p1.z, p2.z));        
    }
    
    public BoundingBox(float x1, float y1, float z1, float x2, float y2, float z2) 
    {        
        minimum = new Point4f(min(x1, x2), min(y1, y2), min(z1, z2));
        maximum = new Point4f(max(x1, x2), max(y1, y2), max(z1, z2));        
    }

    @Override
    public BoundingBox include(Point4f p) {
        if (p != null) {
            if (p.x < minimum.get(0))
                minimum.set('x', p.x);
            if (p.x > maximum.get(0))
                maximum.set('x', p.x);
            if (p.y < minimum.get(1))
                minimum.set('y', p.y);
            if (p.y > maximum.get(1))
                maximum.set('y', p.y);
            if (p.z < minimum.get(2))
                minimum.set('z', p.z);
            if (p.z > maximum.get(2))
                maximum.set('z', p.z);
        }
        return this;
    }
    
    public BoundingBox include(float[] p) {
        if (p != null) {
            if (p[0] < minimum.get(0))
                minimum.set('x', p[0]);
            if (p[0] > maximum.get(0))
                maximum.set('x', p[0]);
            if (p[1] < minimum.get(1))
                minimum.set('y', p[1]);
            if (p[1] > maximum.get(1))
                maximum.set('y', p[1]);
            if (p[2] < minimum.get(2))
                minimum.set('z', p[2]);
            if (p[2] > maximum.get(2))
                maximum.set('z', p[2]);
        }
        return this;
    }

    @Override
    public Point4f getCenter() {
        Point4f dest = new Point4f();
        dest.x = 0.5f * (minimum.get(0) + maximum.get(0));
        dest.y = 0.5f * (minimum.get(1) + maximum.get(1));
        dest.z = 0.5f * (minimum.get(2) + maximum.get(2));
        return dest;
    }

    @Override
    public float getCenter(int dim) {
        return getCenter().get(dim);
    }

    @Override
    public Point4f getMinimum() {
        return new Point4f(minimum);
    }

    @Override
    public Point4f getMaximum() {
        return new Point4f(maximum);
    }

    @Override
    public BoundingBox getInstance() {
        return new BoundingBox();
    }

    @Override
    public void fieldToMemory(MemoryRegion memory) {
        min_xState.set(memory, minimum.x); 
        min_yState.set(memory, minimum.y);
        min_zState.set(memory, minimum.z);
        min_wState.set(memory, minimum.w);
        
        max_xState.set(memory, maximum.x); 
        max_yState.set(memory, maximum.y);
        max_zState.set(memory, maximum.z);
        max_wState.set(memory, maximum.w);
    }

    @Override
    public void memoryToField(MemoryRegion memory) {
        minimum.x = (float) min_xState.get(memory);
        minimum.y = (float) min_yState.get(memory);
        minimum.z = (float) min_zState.get(memory);
        minimum.w = (float) min_zState.get(memory);
        
        maximum.x = (float) max_xState.get(memory);
        maximum.y = (float) max_yState.get(memory);
        maximum.z = (float) max_zState.get(memory);
        maximum.w = (float) max_zState.get(memory);
        
    }

    @Override
    public BoundingBox newStruct() {
        return new BoundingBox();
    }

    @Override
    public BoundingBox copyStruct() {
        return new BoundingBox(minimum, maximum);
    }
    
    @Override
    public final String toString() {
        return String.format("(%.2f, %.2f, %.2f) to (%.2f, %.2f, %.2f)", minimum.get(0), minimum.get(1), minimum.get(2), maximum.get(0), maximum.get(1), maximum.get(2));
    } 

    @Override
    public LayoutMemory getLayout() {
        return layout;
    }
}
