/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.envmap;

import bitmap.image.BitmapRGBE;
import coordinate.sampling.sat.SAT;
import raytracing.geom.RColor4;
import raytracing.geom.RInt3;
import wrapper.core.CMemory;
import static wrapper.core.CMemory.READ_ONLY;
import static wrapper.core.CMemory.READ_WRITE;
import wrapper.core.OpenCLConfiguration;
import wrapper.core.memory.values.FloatValue;

/**
 *
 * @author user
 */
public class REnvMap {
    private final OpenCLConfiguration configuration;
    
    //env map
    private CMemory<RColor4> crgb4;
    private CMemory<FloatValue> cenvlum;
    private CMemory<FloatValue> cenvlumsat;
    private final CMemory<RInt3> cenvmapSize;
    
    private SAT sat;
    
    public REnvMap(OpenCLConfiguration configuration)
    {
        this.configuration  = configuration;
        this.crgb4          = configuration.createBufferF(RColor4.class, 1, READ_ONLY);
        this.cenvlum        = configuration.createBufferF(FloatValue.class, 1, READ_WRITE);
        this.cenvlumsat     = configuration.createBufferF(FloatValue.class, 1, READ_WRITE);
        this.cenvmapSize    = configuration.createBufferI(RInt3.class, 1, READ_WRITE);        
    }
    
    public void setEnvironmentMap(BitmapRGBE bitmap)
    {        
        sat = new SAT(bitmap.getWidth(), bitmap.getHeight());
        sat.setArray(bitmap.getLuminanceArray());
        
        crgb4 = configuration.createFromF(RColor4.class, bitmap.getFloat4Data(), READ_ONLY);
        cenvlum = configuration.createFromF(FloatValue.class, sat.getFuncArray(), READ_WRITE);
        cenvlumsat = configuration.createFromF(FloatValue.class, sat.getSATArray(), READ_WRITE);  
        cenvmapSize.mapWriteMemory(envmapsize->{
            RInt3 esize = envmapsize.getCL();
            esize.set(bitmap.getWidth(), bitmap.getHeight(), 1);
        });
    }
    
    public CMemory<RInt3> getEnvMapSize()
    {
        return cenvmapSize;
    }
    
    public CMemory<RColor4> getRgbCL()
    {
        return crgb4;
    }
    
    public CMemory<FloatValue> getEnvLumCL()
    {
        return cenvlum;
    }
        
    public CMemory<FloatValue> getEnvLumSATCL()
    {
        return cenvlumsat;
    }
    
    public void setIsPresent(boolean isPresent)
    {
        cenvmapSize.mapWriteMemory(envmapsize->{
            RInt3 esize = envmapsize.getCL();
            esize.set('z', isPresent ? 1 : 0);
        });
    }    
}
