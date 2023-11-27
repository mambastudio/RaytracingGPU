/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.geometry;

import coordinate.generic.g2.AbstractSCoordStruct;
import coordinate.memory.type.LayoutGroup;
import coordinate.memory.type.LayoutMemory;
import coordinate.memory.type.LayoutMemory.PathElement;
import coordinate.memory.type.LayoutValue;
import coordinate.memory.type.MemoryRegion;
import coordinate.memory.type.ValueState;

/**
 *
 * @author user
 */
public class Point4f extends AbstractSCoordStruct<
        Point4f, Vector4f, 
        Point4f> {
    public float x, y, z, w;
    
    private static final LayoutMemory layout = LayoutGroup.groupLayout(
        LayoutValue.JAVA_FLOAT.withId("x"),
        LayoutValue.JAVA_FLOAT.withId("y"),
        LayoutValue.JAVA_FLOAT.withId("z"),
        LayoutValue.JAVA_FLOAT.withId("w"));            
    
    private static final ValueState xState = layout.valueState(PathElement.groupElement("x"));
    private static final ValueState yState = layout.valueState(PathElement.groupElement("y"));
    private static final ValueState zState = layout.valueState(PathElement.groupElement("z"));
    private static final ValueState wState = layout.valueState(PathElement.groupElement("w"));
    
    public Point4f(){
        super();
    }

    public Point4f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point4f(Point4f p) {
        x = p.x;
        y = p.y;
        z = p.z;
    }
                
    public static final Vector4f sub(Point4f p1, Point4f p2) 
    {
        Vector4f dest = new Vector4f();
        dest.x = p1.x - p2.x;
        dest.y = p1.y - p2.y;
        dest.z = p1.z - p2.z;
        return dest;
    }

    public static final Point4f mid(Point4f p1, Point4f p2) 
    {
        Point4f dest = new Point4f();
        dest.x = 0.5f * (p1.x + p2.x);
        dest.y = 0.5f * (p1.y + p2.y);
        dest.z = 0.5f * (p1.z + p2.z);
        return dest;
    }
    
    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public float[] getArray() {
        return new float[]{x, y, z, 0};
    }

    @Override
    public void set(float... values) {        
        x = values[0];
        y = values[1];
        z = values[2];
    }

    @Override
    public float get(char axis) {
        switch (axis) {
            case 'x':
                return x;
            case 'y':
                return y;
            case 'z':
                return z;
            case 'w':
                return w;
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        }
    }

    @Override
    public void set(char axis, float value) {
        switch (axis) {
            case 'x':
                x = value;
                break;
            case 'y':
                y = value;
                break;
            case 'z':
                z = value;
                break;
            case 'w':
                w = value;
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public Point4f copyStruct() {
        return new Point4f(x, y, z);
    }

    @Override
    public Point4f getSCoordInstance() {
        return new Point4f();
    }

    @Override
    public Vector4f getVCoordInstance() {
        return new Vector4f();
    }
    
    @Override
    public void setIndex(int index, float value) {
        switch (index)
        {
            case 0:
                x = value;
                break;
            case 1:
                y = value;
                break;    
            case 2:
                z = value;
                break;
        }
    }
        
    @Override
    public String toString()
    {
        float[] array = getArray();
        return String.format("(%3.2f, %3.2f, %3.2f)", array[0], array[1], array[2]);
    }

    @Override
    public int getByteSize() {
        return 4;
    }

    @Override
    public void fieldToMemory(MemoryRegion memory) {
        xState.set(memory, x); 
        yState.set(memory, y);
        zState.set(memory, z);
        wState.set(memory, w);
    }

    @Override
    public void memoryToField(MemoryRegion memory) {
        x = (float) xState.get(memory);
        y = (float) yState.get(memory);
        z = (float) zState.get(memory);
        w = (float) zState.get(memory);
    }

    @Override
    public Point4f newStruct() {
        return new Point4f();
    }

    @Override
    public LayoutMemory getLayout()
    {
        return layout;
    }
    
    @Override
    public Point4f copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
