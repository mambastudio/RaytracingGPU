/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.generic.AbstractRay;
import coordinate.struct.structbyte.StructBufferMemory;
import raytracing.geom.RInt2;
import raytracing.geom.RInt4;
import raytracing.geom.RPoint3;
import raytracing.geom.RVector3;

/**
 *
 * @author user
 */
public class RRay extends StructBufferMemory implements AbstractRay<RPoint3, RVector3>
{
    public RPoint3  o;
    public RVector3 d;    
    public RVector3 inv_d;   
    public RInt4 sign;    
    public RInt2 extra;    
    public float tMin;
    public float tMax;
        
    public RRay() 
    {        
        o = new RPoint3();
        d = new RVector3();
        inv_d = new RVector3();
        sign = new RInt4();
        extra = new RInt2();       
        tMin = 0.01f;
        tMax = Float.POSITIVE_INFINITY;   
    }
    
    public final void init()
    {                
        inv_d = new RVector3(1f/d.get(0), 1f/d.get(1), 1f/d.get(2));
        
        sign.set(0, inv_d.get(0) < 0 ? 1 : 0);
        sign.set(1, inv_d.get(1) < 0 ? 1 : 0);
        sign.set(2, inv_d.get(2) < 0 ? 1 : 0);
        
        this.refreshGlobalBuffer();
    }
    
    public int[] dirIsNeg()
    {
        int[] dirIsNeg = {sign.get(0), sign.get(1), sign.get(2)};
        return dirIsNeg;
    }
    
    @Override
    public final boolean isInside(float t) 
    {
        return (tMin < t) && (t < tMax);
    }
    
    public RVector3 getInvDir()
    {
        return new RVector3(inv_d);
    }
    
    @Override
    public void set(float ox, float oy, float oz, float dx, float dy, float dz) 
    {       
        o = new RPoint3(ox, oy, oz);
        d = new RVector3(dx, dy, dz).normalize();
        
        tMin = 0.01f;
        tMax = Float.POSITIVE_INFINITY;
        
        init();
    }

    @Override
    public void set(RPoint3 oc, RVector3 dc) 
    {
        o = oc;
        d = dc.normalize();
        
        tMin = 0.01f;
        tMax = Float.POSITIVE_INFINITY;
        
        init();
    }

    @Override
    public RPoint3 getPoint() {
        RPoint3 dest = new RPoint3();        
        dest.x = o.get(0) + (tMax * d.get(0));
        dest.y = o.get(1) + (tMax * d.get(1));
        dest.z = o.get(2) + (tMax * d.get(2));
        return dest;
    }

    @Override
    public RPoint3 getPoint(float t) {
        RPoint3 dest = new RPoint3();               
        dest.x = o.get(0) + (t * d.get(0));
        dest.y = o.get(1) + (t * d.get(1));
        dest.z = o.get(2) + (t * d.get(2));
        return dest;
    }

    @Override
    public RVector3 getDirection() {
        return new RVector3(d);
    }

    @Override
    public RVector3 getInverseDirection() {
        return new RVector3(inv_d);
    }

    @Override
    public RPoint3 getOrigin() {
        return new RPoint3(o);
    }

    @Override
    public float getMin() {
        return tMin;
    }

    @Override
    public float getMax() {
        return tMax;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("o ").append(o.get(0)).append(" ").append(o.get(1)).append(" ").append(o.get(2)).append(" ");
        builder.append("d ").append(d.get(0)).append(" ").append(d.get(1)).append(" ").append(d.get(2));
        return builder.toString();
    }

    @Override
    public AbstractRay<RPoint3, RVector3> copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
