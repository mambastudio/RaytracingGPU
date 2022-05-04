/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.abstracts;

import coordinate.generic.AbstractCoordinateInteger;
import raytracing.abstracts.RayAPI.RayDeviceType;

/**
 *
 * @author user
 * @param <T>
 * @param <I2>
 */
public interface RayConfig<T extends RayConfig, I2 extends AbstractCoordinateInteger> {
    
    public T copy();
    
    public void setHasEnvironment(boolean hasEnv);
    public boolean hasEnvironment();
    
    public int getResolutionSize(RayDeviceType deviceType);
    public I2 getResolution(RayDeviceType deviceType);
    public void setResolution(RayDeviceType deviceType, int width, int height);
    public boolean isResolutionSame(RayDeviceType deviceType, int width, int height);
    public void setMaxResolution(RayDeviceType deviceType, int width, int height);
    public I2 getMaxResolution(RayDeviceType deviceType);
    
    default int getLocalSize(){return 64;};
    public int getGlobalSize(); //divisible by 2^n and 64
    
}
