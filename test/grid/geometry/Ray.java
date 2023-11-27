/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.geometry;

import coordinate.generic.AbstractRay;
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
public class Ray implements StructBase<Ray>, AbstractRay<Point4f, Vector4f, Ray>
{
    public Point4f  o;
    public Vector4f d;    
    public Vector4f inv_d;   
    public Point4i sign;   
    public float tMin;
    public float tMax;
    
    public static final float EPSILON = 0.01f;// 0.01f;
    
    private static final LayoutMemory layout = LayoutGroup.groupLayout(
            new Point4f().getLayout().withId("o"),
            new Vector4f().getLayout().withId("d"),
            new Vector4f().getLayout().withId("inv_d"),
            new Point4i().getLayout().withId("sign"),
            LayoutValue.JAVA_FLOAT.withId("tMin"),
            LayoutValue.JAVA_FLOAT.withId("tMax")
    ); 
    
    private static final ValueState o_xState = layout.valueState(PathElement.groupElement("o"), PathElement.groupElement("x"));
    private static final ValueState o_yState = layout.valueState(PathElement.groupElement("o"), PathElement.groupElement("y"));
    private static final ValueState o_zState = layout.valueState(PathElement.groupElement("o"), PathElement.groupElement("z"));
    private static final ValueState o_wState = layout.valueState(PathElement.groupElement("o"), PathElement.groupElement("w"));
                 
    private static final ValueState d_xState = layout.valueState(PathElement.groupElement("d"), PathElement.groupElement("x"));
    private static final ValueState d_yState = layout.valueState(PathElement.groupElement("d"), PathElement.groupElement("y"));
    private static final ValueState d_zState = layout.valueState(PathElement.groupElement("d"), PathElement.groupElement("z"));
    private static final ValueState d_wState = layout.valueState(PathElement.groupElement("d"), PathElement.groupElement("w"));
                                   
    private static final ValueState inv_d_xState = layout.valueState(PathElement.groupElement("inv_d"), PathElement.groupElement("x"));
    private static final ValueState inv_d_yState = layout.valueState(PathElement.groupElement("inv_d"), PathElement.groupElement("y"));
    private static final ValueState inv_d_zState = layout.valueState(PathElement.groupElement("inv_d"), PathElement.groupElement("z"));
    private static final ValueState inv_d_wState = layout.valueState(PathElement.groupElement("inv_d"), PathElement.groupElement("w"));
    
    private static final ValueState sign_xState = layout.valueState(PathElement.groupElement("sign"), PathElement.groupElement("x"));
    private static final ValueState sign_yState = layout.valueState(PathElement.groupElement("sign"), PathElement.groupElement("y"));
    private static final ValueState sign_zState = layout.valueState(PathElement.groupElement("sign"), PathElement.groupElement("z"));
    private static final ValueState sign_wState = layout.valueState(PathElement.groupElement("sign"), PathElement.groupElement("w"));
    
    private static final ValueState tMin_State = layout.valueState(PathElement.groupElement("tMin"));
    private static final ValueState tMax_State = layout.valueState(PathElement.groupElement("tMax"));                                  
    
    public Ray() 
    {
        o = new Point4f();
        d = new Vector4f();  
        
        tMin = EPSILON;
        tMax = Float.POSITIVE_INFINITY;        
    }
    
    
    public final void init()
    {        
        inv_d = new Vector4f(safe_rcp(d.x), safe_rcp(d.y), safe_rcp(d.z));
        sign = new Point4i();
        sign.x = inv_d.x < 0 ? 1 : 0;
        sign.y = inv_d.y < 0 ? 1 : 0;
        sign.z = inv_d.z < 0 ? 1 : 0;
    }
    
    public int[] dirIsNeg()
    {
        int[] dirIsNeg = {sign.x, sign.y, sign.z};
        return dirIsNeg;
    }
    
    @Override
    public final boolean isInside(float t) 
    {
        return (tMin < t) && (t < tMax);
    }
    
    public Vector4f getInvDir()
    {
        return new Vector4f(inv_d);
    }
    
    @Override
    public void set(float ox, float oy, float oz, float dx, float dy, float dz) {
        o = new Point4f(ox, oy, oz);
        d = new Vector4f(dx, dy, dz).normalize();  
        
        tMin = EPSILON;
        tMax = Float.POSITIVE_INFINITY;
        
        init();
    }

    @Override
    public void set(Point4f o, Vector4f d) {
        set(o.x, o.y, o.z, d.x, d.y, d.z);
    }

    @Override
    public Point4f getPoint() {
        Point4f dest = new Point4f();        
        dest.x = o.x + (tMax * d.x);
        dest.y = o.y + (tMax * d.y);
        dest.z = o.z + (tMax * d.z);
        return dest;
    }

    @Override
    public Point4f getPoint(float t) {
        Point4f dest = new Point4f();        
        dest.x = o.x + (t * d.x);
        dest.y = o.y + (t * d.y);
        dest.z = o.z + (t * d.z);
        return dest;
    }

    @Override
    public Vector4f getDirection() {
        return d;
    }
        
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("Ray: ").append("\n");
        builder.append("         o    ").append(String.format("(%.5f, %.5f, %.5f)", o.x, o.y, o.z)).append("\n");
        builder.append("         d    ").append(String.format("(%.5f, %.5f, %.5f)", d.x, d.y, d.z)).append("\n");
        builder.append("         tMin ").append(String.format("(%.5f)", tMin)).append("\n");
        builder.append("         tMax ").append(String.format("(%.5f)", tMax));
                
        return builder.toString();   
    }

    @Override
    public float getMin() {
        return tMin;
    }

    @Override
    public float getMax() {
        return tMax;
    }
    
    public final void setMax(float t) {
        tMax = t;
    }
    
    @Override
    public Point4f getOrigin() {
        return o.copyStruct();
    }

    @Override
    public Vector4f getInverseDirection() {
        return inv_d.copyStruct();
    }
    
    private float safe_rcp(float x) {
        return x != 0 ? 1.0f / x : Math.copySign(Float.intBitsToFloat(0x7f800000), x);
    }

    @Override
    public Ray copyStruct() {
        Ray ray = new Ray();
        ray.set(o, d);
        ray.tMin = tMin;
        ray.tMax = tMax;
        return ray;
    }

    @Override
    public void fieldToMemory(MemoryRegion memory) {
        o_xState.set(memory, o.x); 
        o_yState.set(memory, o.y);
        o_zState.set(memory, o.z);
        o_wState.set(memory, o.w);
        
        d_xState.set(memory, d.x); 
        d_yState.set(memory, d.y);
        d_zState.set(memory, d.z);
        d_wState.set(memory, d.w);
        
        inv_d_xState.set(memory, inv_d.x); 
        inv_d_yState.set(memory, inv_d.y);
        inv_d_zState.set(memory, inv_d.z);
        inv_d_wState.set(memory, inv_d.w);
        
        sign_xState.set(memory, sign.x);
        sign_yState.set(memory, sign.y);
        sign_zState.set(memory, sign.z);
        sign_wState.set(memory, sign.w);
        
        tMin_State.set(memory, tMin);
        tMax_State.set(memory, tMax);
    }

    @Override
    public void memoryToField(MemoryRegion memory) {
        o.x = (float) o_xState.get(memory);
        o.y = (float) o_yState.get(memory);
        o.z = (float) o_zState.get(memory);
        o.w = (float) o_zState.get(memory);
        
        d.x = (float) d_xState.get(memory);
        d.y = (float) d_yState.get(memory);
        d.z = (float) d_zState.get(memory);
        d.w = (float) d_zState.get(memory);
        
        inv_d.x = (float) inv_d_xState.get(memory);
        inv_d.y = (float) inv_d_yState.get(memory);
        inv_d.z = (float) inv_d_zState.get(memory);
        inv_d.w = (float) inv_d_zState.get(memory);
        
        sign.x = (int) sign_xState.get(memory);
        sign.y = (int) sign_yState.get(memory);
        sign.z = (int) sign_zState.get(memory);
        sign.w = (int) sign_zState.get(memory);
        
        tMin = (float) tMin_State.get(memory);
        tMax = (float) tMax_State.get(memory);
    }

    @Override
    public Ray newStruct() {
        return new Ray();
    }

    @Override
    public LayoutMemory getLayout() {
        return layout;
    }

    @Override
    public Ray copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
