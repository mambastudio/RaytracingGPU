/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid;

import grid.geometry.Vector4f;
import grid.geometry.Point4f;
import coordinate.generic.raytrace.AbstractIntersection;
import coordinate.memory.type.LayoutGroup;
import coordinate.memory.type.LayoutMemory;
import coordinate.memory.type.LayoutMemory.PathElement;
import coordinate.memory.type.LayoutValue;
import coordinate.memory.type.MemoryRegion;
import coordinate.memory.type.StructBase;
import coordinate.memory.type.ValueState;

/**
 *
 * @author user
 */
public class Intersection implements StructBase<Intersection>, AbstractIntersection{
    public Point4f p;
    public Vector4f n;
    public float u;
    public float v;  
    
    private static final LayoutMemory layout = LayoutGroup.groupLayout(
        new Point4f().getLayout().withId("p"),
        new Vector4f().getLayout().withId("n"),
        LayoutValue.JAVA_FLOAT.withId("u"),
        LayoutValue.JAVA_FLOAT.withId("v")
    );            
    
    private static final ValueState p_xState = layout.valueState(PathElement.groupElement("p"), PathElement.groupElement("x"));
    private static final ValueState p_yState = layout.valueState(PathElement.groupElement("p"), PathElement.groupElement("y"));
    private static final ValueState p_zState = layout.valueState(PathElement.groupElement("p"), PathElement.groupElement("z"));
    private static final ValueState p_wState = layout.valueState(PathElement.groupElement("p"), PathElement.groupElement("w"));
    
    private static final ValueState n_xState = layout.valueState(PathElement.groupElement("n"), PathElement.groupElement("x"));
    private static final ValueState n_yState = layout.valueState(PathElement.groupElement("n"), PathElement.groupElement("y"));
    private static final ValueState n_zState = layout.valueState(PathElement.groupElement("n"), PathElement.groupElement("z"));
    private static final ValueState n_wState = layout.valueState(PathElement.groupElement("n"), PathElement.groupElement("w"));
    
    private static final ValueState uState = layout.valueState(LayoutMemory.PathElement.groupElement("u"));
    private static final ValueState vState = layout.valueState(LayoutMemory.PathElement.groupElement("v"));
    
    
    public Intersection()
    {
        super();
        p = new Point4f();
        n = new Vector4f();
        u = 0;
        v = 0;
    }

    @Override
    public void fieldToMemory(MemoryRegion memory) {
        p_xState.set(memory, p.x); 
        p_yState.set(memory, p.y);
        p_zState.set(memory, p.z);
        p_wState.set(memory, p.w);
        
        n_xState.set(memory, n.x); 
        n_yState.set(memory, n.y);
        n_zState.set(memory, n.z);
        n_wState.set(memory, n.w);
        
        uState.set(memory, u);
        vState.set(memory, v);
    }

    @Override
    public void memoryToField(MemoryRegion memory) {
        p.x = (float) p_xState.get(memory);
        p.y = (float) p_yState.get(memory);
        p.z = (float) p_zState.get(memory);
        p.w = (float) p_zState.get(memory);
        
        n.x = (float) n_xState.get(memory);
        n.y = (float) n_yState.get(memory);
        n.z = (float) n_zState.get(memory);
        n.w = (float) n_zState.get(memory);
        
        u = (float) uState.get(memory);
        v = (float) vState.get(memory);
    }

    @Override
    public Intersection newStruct() {
        return new Intersection();
    }

    @Override
    public Intersection copyStruct() {
        Intersection isect = new Intersection();
        isect.p = p.copyStruct();
        isect.n = n.copyStruct();
        isect.u = u;
        isect.v = v;
        return isect;
    }
    
    @Override
    public LayoutMemory getLayout()
    {
        return layout;
    }
}
