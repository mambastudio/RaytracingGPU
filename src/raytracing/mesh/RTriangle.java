/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.mesh;

import coordinate.shapes.TriangleShape;
import raytracing.geom.RPoint3;
import raytracing.geom.RVector3;
import raytracing.structs.RBound;
import raytracing.structs.RRay;

/**
 *
 * @author user
 */
public class RTriangle extends TriangleShape<RPoint3, RVector3, RRay>  {
    
    protected RTriangle(RPoint3 p1, RPoint3 p2, RPoint3 p3)
    {
        super(p1, p2, p3);        
    }

    @Override
    public RVector3 e1() {
        return RPoint3.sub(pp1, pp2);
    }

    @Override
    public RVector3 e2() {
        return RPoint3.sub(pp3, pp1);
    }
    
    public RPoint3 v0()
    {
        return pp1;
    }
    
    public float nx()
    {
        return n.x;
    }
    
    public float ny()
    {
        return n.y;
    }
    
    public float nz()
    {
        return n.z;
    }
    
    /// Packs the normal components into a float3 structure.
    public RVector3 normal() { return new RVector3(nx(), ny(), nz()); }
    
    public RBound getBound()
    {
        RBound b = new RBound();
        b.include(pp1, pp2, pp3);
        return b;
    }
}
