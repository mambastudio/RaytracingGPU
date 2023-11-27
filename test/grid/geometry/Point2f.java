/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.geometry;

import coordinate.generic.g2.AbstractCoordFloatStruct;
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
public class Point2f extends AbstractCoordFloatStruct<Point2f>{
    public float x, y;
    
    private static final LayoutMemory layout = LayoutGroup.groupLayout(
        LayoutValue.JAVA_FLOAT.withId("x"),
        LayoutValue.JAVA_FLOAT.withId("y"));            
    
    private static final ValueState xState = layout.valueState(PathElement.groupElement("x"));
    private static final ValueState yState = layout.valueState(PathElement.groupElement("y"));
    
    public Point2f(){
        super();
    }

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point2f(Point2f p) {
        x = p.x;
        y = p.y;
    }
           
    @Override
    public void fieldToMemory(MemoryRegion memory) {
        xState.set(memory, x); 
        yState.set(memory, y);
    }

    @Override
    public void memoryToField(MemoryRegion memory) {
        x = (float) xState.get(memory);
        y = (float) yState.get(memory);
    }

    @Override
    public Point2f newStruct() {
        return new Point2f();
    }

    @Override
    public Point2f copyStruct() {
        return new Point2f(x, y);
    }
    
    @Override
    public LayoutMemory getLayout()
    {
        return layout;
    }

    @Override
    public void set(float... values) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float[] getArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getByteSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
