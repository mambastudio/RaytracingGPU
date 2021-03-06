package raytracing.abstracts;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import bitmap.core.AbstractDisplay;
import coordinate.generic.AbstractBound;
import coordinate.generic.AbstractMesh;
import coordinate.generic.raytrace.AbstractAccelerator;
import coordinate.model.CameraModel;
import raytracing.structs.RConfig;

/**
 *
 * @author user
 * @param <A>
 * @param <D>
 * @param <M>
 * @param <MS>
 * @param <AC>
 * @param <BB>
 * @param <CM>
 * @param <CD>
 */
public interface RayDeviceInterface <
        A       extends RayAPI, 
        D       extends AbstractDisplay, 
        M       extends RayMaterial, 
        MS      extends AbstractMesh,
        AC      extends AbstractAccelerator,
        BB      extends AbstractBound,
        CM      extends CameraModel,
        CD      extends CameraDataAbstract> {    
    public enum DeviceBuffer{
        IMAGE_BUFFER,
        GROUP_BUFFER,
        RENDER_BUFFER
    }
            
    public void setAPI(A api);
    
    public void set(MS mesh, AC bvhBuild);     
    
    default void start(){throw new UnsupportedOperationException("Operation not supported");}    
    
    //render either with new config or no config
    default void execute(){throw new UnsupportedOperationException("Operation not supported");}     
    default void execute(RConfig configRay){throw new UnsupportedOperationException("Operation not supported");}     
    
    default void pause(){throw new UnsupportedOperationException("Operation not supported");} 
    default void stop(){throw new UnsupportedOperationException("Operation not supported");} 
    
    //render either with new config or no config
    default void resume(){throw new UnsupportedOperationException("Operation not supported");} 
    default void resume(RConfig configRay){throw new UnsupportedOperationException("Operation not supported");}     
    
    public boolean isPaused();
    public boolean isRunning();
    public boolean isStopped();
        
    default void updateImage(RConfig configRay){}    
    public void updateCamera(RConfig configRay);    
    
    public void setCamera(CD cameraData);    
    public CM getCameraModel(); 
    
    default BB getPriorityBound(){return null;}; //specific bound in mind, like selected by mouse
    default void setPriorityBound(BB bound){};
    default BB getBound(){return null;}; 
    
    //selected object or specific object bounds
    default BB getGroupBound(int value){return null;};
}
