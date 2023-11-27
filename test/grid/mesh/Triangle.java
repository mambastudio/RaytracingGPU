/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.mesh;

import coordinate.generic.g2.AbstractTriangleStruct;
import coordinate.memory.type.LayoutGroup;
import coordinate.memory.type.LayoutMemory;
import coordinate.memory.type.LayoutMemory.PathElement;
import coordinate.memory.type.MemoryRegion;
import coordinate.memory.type.ValueState;
import grid.geometry.Vector4f;
import grid.geometry.Point4f;
import grid.geometry.BoundingBox;
import grid.geometry.Ray;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author user
 */
public class Triangle extends AbstractTriangleStruct<Point4f, Vector4f, Ray, BoundingBox, Triangle>
{
    private final Point4f p1;
    private final Point4f p2;
    private final Point4f p3;
    
    private Optional<Vector4f> n1;
    private Optional<Vector4f> n2;
    private Optional<Vector4f> n3;
    
    private static final LayoutMemory layout = LayoutGroup.groupLayout(
            new Point4f().getLayout().withId("p1"),
            new Point4f().getLayout().withId("p2"),
            new Point4f().getLayout().withId("p3"),
            new Vector4f().getLayout().withId("n1"),
            new Vector4f().getLayout().withId("n2"),
            new Vector4f().getLayout().withId("n3")
    );            
    
    
    private static ValueState p1_xState = layout.valueState(PathElement.groupElement("p1"), PathElement.groupElement("x"));
    private static ValueState p1_yState = layout.valueState(PathElement.groupElement("p1"), PathElement.groupElement("y"));
    private static ValueState p1_zState = layout.valueState(PathElement.groupElement("p1"), PathElement.groupElement("z"));
    private static ValueState p1_wState = layout.valueState(PathElement.groupElement("p1"), PathElement.groupElement("w"));
      
    private static ValueState p2_xState = layout.valueState(PathElement.groupElement("p2"), PathElement.groupElement("x"));
    private static ValueState p2_yState = layout.valueState(PathElement.groupElement("p2"), PathElement.groupElement("y"));
    private static ValueState p2_zState = layout.valueState(PathElement.groupElement("p2"), PathElement.groupElement("z"));
    private static ValueState p2_wState = layout.valueState(PathElement.groupElement("p2"), PathElement.groupElement("w"));
       
    private static ValueState p3_xState = layout.valueState(PathElement.groupElement("p3"), PathElement.groupElement("x"));
    private static ValueState p3_yState = layout.valueState(PathElement.groupElement("p3"), PathElement.groupElement("y"));
    private static ValueState p3_zState = layout.valueState(PathElement.groupElement("p3"), PathElement.groupElement("z"));
    private static ValueState p3_wState = layout.valueState(PathElement.groupElement("p3"), PathElement.groupElement("w"));
      
    private static ValueState n1_xState = layout.valueState(PathElement.groupElement("n1"), PathElement.groupElement("x"));
    private static ValueState n1_yState = layout.valueState(PathElement.groupElement("n1"), PathElement.groupElement("y"));
    private static ValueState n1_zState = layout.valueState(PathElement.groupElement("n1"), PathElement.groupElement("z"));
    private static ValueState n1_wState = layout.valueState(PathElement.groupElement("n1"), PathElement.groupElement("w"));
      
    private static ValueState n2_xState = layout.valueState(PathElement.groupElement("n2"), PathElement.groupElement("x"));
    private static ValueState n2_yState = layout.valueState(PathElement.groupElement("n2"), PathElement.groupElement("y"));
    private static ValueState n2_zState = layout.valueState(PathElement.groupElement("n2"), PathElement.groupElement("z"));
    private static ValueState n2_wState = layout.valueState(PathElement.groupElement("n2"), PathElement.groupElement("w"));
      
    private static ValueState n3_xState = layout.valueState(PathElement.groupElement("n3"), PathElement.groupElement("x"));
    private static ValueState n3_yState = layout.valueState(PathElement.groupElement("n3"), PathElement.groupElement("y"));
    private static ValueState n3_zState = layout.valueState(PathElement.groupElement("n3"), PathElement.groupElement("z"));
    private static ValueState n3_wState = layout.valueState(PathElement.groupElement("n3"), PathElement.groupElement("w"));
    
    public Triangle()
    {
        this(new Point4f(), new Point4f(), new Point4f());
    }
    
    public Triangle(Point4f p1, Point4f p2, Point4f p3)
    {
        this.p1 = Objects.requireNonNull(p1);
        this.p2 = Objects.requireNonNull(p2);
        this.p3 = Objects.requireNonNull(p3);
        
        this.n1 = Optional.empty();
        this.n2 = Optional.empty();
        this.n3 = Optional.empty();
    }
    
    public Triangle(Point4f p1, Point4f p2, Point4f p3, Vector4f n1, Vector4f n2, Vector4f n3)
    {
        this(p1, p2, p3);
        
        this.n1 = Optional.of(n1);
        this.n2 = Optional.of(n2);
        this.n3 = Optional.of(n3);
    }

    @Override
    public void fieldToMemory(MemoryRegion memory) {
        p1_xState.set(memory, p1.x);
        p1_yState.set(memory, p1.y);
        p1_zState.set(memory, p1.z);
        p1_wState.set(memory, p1.w);
        
        p2_xState.set(memory, p2.x);
        p2_yState.set(memory, p2.y);
        p2_zState.set(memory, p2.z);
        p2_wState.set(memory, p2.w);
        
        p3_xState.set(memory, p3.x);
        p3_yState.set(memory, p3.y);
        p3_zState.set(memory, p3.z);
        p3_wState.set(memory, p3.w);
        
        n1_xState.set(memory, n1.orElse(new Vector4f()).x);
        n1_yState.set(memory, n1.orElse(new Vector4f()).y);
        n1_zState.set(memory, n1.orElse(new Vector4f()).z);
        n1_wState.set(memory, n1.orElse(new Vector4f()).w);
        
        n2_xState.set(memory, n2.orElse(new Vector4f()).x);
        n2_yState.set(memory, n2.orElse(new Vector4f()).y);
        n2_zState.set(memory, n2.orElse(new Vector4f()).z);
        n2_wState.set(memory, n2.orElse(new Vector4f()).w);
        
        n3_xState.set(memory, n3.orElse(new Vector4f()).x);
        n3_yState.set(memory, n3.orElse(new Vector4f()).y);
        n3_zState.set(memory, n3.orElse(new Vector4f()).z);
        n3_wState.set(memory, n3.orElse(new Vector4f()).w);
    }

    @Override
    public void memoryToField(MemoryRegion memory) {
        p1.x = (float) p1_xState.get(memory);
        p1.y = (float) p1_yState.get(memory);
        p1.z = (float) p1_zState.get(memory);
        p1.w = (float) p1_zState.get(memory);
        
        p2.x = (float) p2_xState.get(memory);
        p2.y = (float) p2_yState.get(memory);
        p2.z = (float) p2_zState.get(memory);
        p2.w = (float) p2_zState.get(memory);
        
        p3.x = (float) p3_xState.get(memory);
        p3.y = (float) p3_yState.get(memory);
        p3.z = (float) p3_zState.get(memory);
        p3.w = (float) p3_zState.get(memory); 
        
        Vector4f nn1 = new Vector4f();
        nn1.x = (float) n1_xState.get(memory);
        nn1.y = (float) n1_yState.get(memory);
        nn1.z = (float) n1_zState.get(memory);
        nn1.w = (float) n1_wState.get(memory);
        n1 = Optional.ofNullable(nn1.isZero() ? null : nn1);
        
        Vector4f nn2 = new Vector4f();
        nn2.x = (float) n2_xState.get(memory);
        nn2.y = (float) n2_yState.get(memory);
        nn2.z = (float) n2_zState.get(memory);
        nn2.w = (float) n2_wState.get(memory);
        n2 = Optional.ofNullable(nn2.isZero() ? null : nn2);
        
        Vector4f nn3 = new Vector4f();
        nn3.x = (float) n3_xState.get(memory);
        nn3.y = (float) n3_yState.get(memory);
        nn3.z = (float) n3_zState.get(memory);
        nn3.w = (float) n3_wState.get(memory);
        n3 = Optional.ofNullable(nn3.isZero() ? null : nn3);
    }

    @Override
    public Triangle newStruct() {
        return new Triangle();
    }

    @Override
    public Triangle copyStruct() {
        Point4f pp1 = p1.copy(), pp2 = p2.copy(), pp3 = p3.copy();
        
        Vector4f nn1 = n1.map(o -> o.copy()).get(); 
        Vector4f nn2 = n2.map(o -> o.copy()).get();
        Vector4f nn3 = n3.map(o -> o.copy()).get();
        
        return new Triangle(pp1, pp2, pp3, nn1, nn2, nn3);
    }

    @Override
    public long sizeOf()
    {
        return layout.byteSizeAggregate();
    }
    
    @Override
    public LayoutMemory getLayout()
    {
        return layout;
    }
    
    @Override
    public Point4f p1() {
        return p1;
    }

    @Override
    public Point4f p2() {
        return p2;
    }

    @Override
    public Point4f p3() {
        return p3;
    }

    @Override
    public Optional<Vector4f> n1() {
        return n1;
    }

    @Override
    public Optional<Vector4f> n2() {
        return n2;
    }

    @Override
    public Optional<Vector4f> n3() {
        return n3;
    }

    @Override
    public BoundingBox getBound() {
        return new BoundingBox().include(p1(), p2(), p3());
    }
}
