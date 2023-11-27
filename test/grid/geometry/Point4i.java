/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.geometry;

import coordinate.generic.g2.AbstractCoordIntStruct;
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
public class Point4i extends AbstractCoordIntStruct<Point4i>{
    
    public int x, y, z, w;
    
    private static final LayoutMemory layout = LayoutGroup.groupLayout(
        LayoutValue.JAVA_INT.withId("x"),
        LayoutValue.JAVA_INT.withId("y"),
        LayoutValue.JAVA_INT.withId("z"),
        LayoutValue.JAVA_INT.withId("w"));            
    
    private static final ValueState xState = layout.valueState(PathElement.groupElement("x"));
    private static final ValueState yState = layout.valueState(PathElement.groupElement("y"));
    private static final ValueState zState = layout.valueState(PathElement.groupElement("z"));
    private static final ValueState wState = layout.valueState(PathElement.groupElement("w"));
    
    public Point4i(){
        super();
    }

    public Point4i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Point4i(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Point4i(Point4i p) {        
        x = p.x;
        y = p.y;
        z = p.z;
        w = p.w;
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
        x = (int) xState.get(memory);
        y = (int) yState.get(memory);
        z = (int) zState.get(memory);
        w = (int) zState.get(memory);
    }
    
    @Override
    public Point4i copyStruct() {
        return new Point4i(x, y, z);
    }

    @Override
    public Point4i newStruct() {
        return new Point4i();
    }

    @Override
    public LayoutMemory getLayout()
    {
        return layout;
    }

    @Override
    public int get(char axis) {
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
    public void set(char axis, int value) {
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
    public void set(int... values) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIndex(int index, int value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] getArray() {
        return new int[]{x, y, z, w};
    }

    @Override
    public int getSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getByteSize() {
        return (int) layout.byteSizeAggregate();
    }
    
}
