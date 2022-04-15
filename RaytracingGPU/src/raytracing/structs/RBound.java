/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;


import coordinate.generic.AbstractBound;
import coordinate.struct.structfloat.FloatStruct;
import raytracing.coordinates.RPoint3;
import raytracing.coordinates.RVector3;

/**
 *
 * @author user
 */
public class RBound extends FloatStruct implements AbstractBound<RPoint3, RVector3, RRay, RBound>
{
    public RPoint3 minimum;
    public RPoint3 maximum;    
    
    public RBound() 
    {
        minimum = new RPoint3();
        maximum = new RPoint3();
        
        minimum.set('x', Float.POSITIVE_INFINITY); 
        minimum.set('y', Float.POSITIVE_INFINITY); 
        minimum.set('z', Float.POSITIVE_INFINITY);
        
        maximum.set('x', Float.NEGATIVE_INFINITY); 
        maximum.set('y', Float.NEGATIVE_INFINITY); 
        maximum.set('z', Float.NEGATIVE_INFINITY);         
    }
    
    public RBound(RPoint3 min, RPoint3 max)
    {
        this();
        include(min);
        include(max);   
       
    }
    
    public void setBound(RBound bound)
    {
        minimum = bound.getMinimum();
        maximum = bound.getMaximum();
        this.refreshGlobalArray();
    }
    
    
    
    @Override
    public final void include(RPoint3 p) {
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
        this.refreshGlobalArray();
    }

    @Override
    public RPoint3 getCenter() {
        RPoint3 dest = new RPoint3();
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
    public RPoint3 getMinimum() {
        return new RPoint3(minimum);
    }

    @Override
    public RPoint3 getMaximum() {
        return new RPoint3(maximum);
    }

    @Override
    public RBound getInstance() {
        return new RBound();
    }
   
    @Override
    public final String toString() {
        return String.format("(%.2f, %.2f, %.2f) to (%.2f, %.2f, %.2f)", minimum.get(0), minimum.get(1), minimum.get(2), maximum.get(0), maximum.get(1), maximum.get(2));
    }     
}
