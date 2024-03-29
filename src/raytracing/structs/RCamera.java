/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import raytracing.abstracts.CameraDataAbstract;
import raytracing.geom.RPoint2;
import raytracing.geom.RPoint3;
import raytracing.geom.RVector3;


/**
 *
 * @author user
 */
public class RCamera extends CameraDataAbstract {
    public RPoint3 position; 
    public RPoint3 lookat;
    public RVector3 up;
    public RPoint2 dimension;
    public float fov;

    public RCamera()
    {
        position = new RPoint3();
        lookat = new RPoint3();
        dimension = new RPoint2();
        up = new RVector3();            
    }

    public void setPosition(RPoint3 position)
    {
        this.position = position;
        this.refreshGlobalBuffer();
    }

    public void setLookat(RPoint3 lookat)
    {
        this.lookat = lookat;
        this.refreshGlobalBuffer();
    }

    public void setUp(RVector3 up)
    {
        this.up = up;
        this.refreshGlobalBuffer();
    }

    public void setDimension(RPoint2 dimension)
    {
        this.dimension = dimension;
        this.refreshGlobalBuffer();
    }

    public void setFov(float fov)
    {
        this.fov = fov;
        this.refreshGlobalBuffer();
    }
    
    public boolean isSynched(RCamera cameraStruct)
    {
        float x  = position.x;
        float x1 = cameraStruct.position.get(0);
        float y  = position.y;
        float y1 = cameraStruct.position.get(1);
        float z  = position.z;
        float z1 = cameraStruct.position.get(2);
        
        return (x == x1) && (y == y1) &&  (z == z1);
    }
}
