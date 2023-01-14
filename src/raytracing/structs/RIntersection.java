/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.generic.raytrace.AbstractIntersection;
import coordinate.struct.structbyte.StructBufferMemory;
import raytracing.geom.RPoint2;
import raytracing.geom.RPoint3;

/**
 *
 * @author user
 */
public class RIntersection extends StructBufferMemory implements AbstractIntersection{
        
    public RPoint3 p;
    public RPoint3 n;
    public RPoint3 ng;
    public RPoint3 d;    
    public RPoint2 uv;
    public int mat;
    public int id;
    public int hit; 
    public int traverseHit;
    
    public RIntersection()
    {        
        this.p = new RPoint3();
        this.n = new RPoint3();
        this.ng = new RPoint3();
        this.d = new RPoint3();        
        this.uv = new RPoint2();
        this.mat = 0;
        this.id = 0;
        this.hit = 0;
        this.traverseHit = 0;
    }
    
    public void setMat(int mat)
    {
        this.mat = mat;
        this.refreshGlobalBuffer();
    }

    public void setHit(int hit)
    {
        this.hit = hit;
        this.refreshGlobalBuffer();
    }
    
    public int getHit()
    {
        this.refreshGlobalBuffer();
        return hit;
    } 
    
    public boolean isHit()
    {
        this.refreshGlobalBuffer();
        return hit == 1;
    }
}
