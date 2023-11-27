/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.geometry;

import coordinate.generic.g2.AbstractVCoordStruct;
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
public class Vector4f extends AbstractVCoordStruct<
        Point4f, Vector4f, 
        Vector4f> {
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
    
    public Vector4f(){}
    public Vector4f(float x, float y, float z){this.x = x; this.y = y; this.z = z;};
    public Vector4f(Vector4f v) {this.x = v.x; this.y = v.y; this.z = v.z;}
        
    @Override
    public Vector4f getCoordInstance() {
        return new Vector4f();
    }

    @Override
    public Vector4f copyStruct() {
        return new Vector4f(x, y, z);
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
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void set(float... values) {        
        x = values[0];
        y = values[1];
        z = values[2];
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
    public int getSize() {
        return 4;
    }

    @Override
    public float[] getArray() {
        return new float[]{x, y, z, 0};
    }
      
    @Override
    public String toString()
    {
        float[] array = getArray();
        return String.format("(%8.2f, %8.2f, %8.2f)", array[0], array[1], array[2]);
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
    public Vector4f newStruct() {
        return new Vector4f();
    }

    @Override
    public LayoutMemory getLayout()
    {
        return layout;
    }
    
    @Override
    public Vector4f copy() {
        return copyStruct();
    }
}
