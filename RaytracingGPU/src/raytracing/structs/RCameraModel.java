/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.model.CameraModel;
import coordinate.model.Transform;
import raytracing.coordinates.RPoint3;
import raytracing.coordinates.RVector3;

/**
 *
 * @author user
 */
public class RCameraModel extends CameraModel <RPoint3, RVector3, RRay>{
    public RCameraModel(RPoint3 position, RPoint3 lookat, RVector3 up, float horizontalFOV) {
        super(position.copy(), lookat.copy(), up.copy(), horizontalFOV);
    }
    
    public void set(RPoint3 position, RPoint3 lookat, RVector3 up, float horizontalFOV)
    {
        this.position = position.copy();
        this.lookat = lookat.copy();
        this.up = up.copy();
        this.fov = horizontalFOV;
        this.cameraTransform = new Transform<>();
    }

    @Override
    public RCameraModel copy() {
        RCameraModel camera = new RCameraModel(position, lookat, up, fov);
        camera.setUp();
        return camera;
    }
    
    public RCamera getCameraStruct()
    {
        RCamera camera = new RCamera();        
        camera.setPosition(position);
        camera.setLookat(lookat);
        camera.setUp(up);
        camera.setFov(fov);
        
        return camera;        
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
