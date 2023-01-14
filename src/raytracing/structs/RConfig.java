/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.struct.structbyte.StructBufferMemory;
import raytracing.geom.RInt2;

/**
 *
 * @author user
 */
public class RConfig extends StructBufferMemory{
    public int      hasEnvironment;
    public RInt2    resolutionR; //divisible by 2^n and 64
    public RInt2    resolutionG;
    public int      frameCount;
    public int      localSize;
    
    public RConfig()
    {
        this.hasEnvironment = 0;
        this.resolutionR = new RInt2(448, 448);
        this.resolutionG = new RInt2(448, 448);
        this.frameCount = 1;
        this.localSize = 64;
    }
    
    public RConfig copy()
    {
        RConfig config = new RConfig();
        config.hasEnvironment = hasEnvironment;
        config.resolutionR = resolutionR.copy();
        config.resolutionG = resolutionG.copy();
        config.frameCount = frameCount;
        config.localSize = localSize;
        return config;
    }
    
    public void setHasEnvironment(boolean hasEnv)
    {
        hasEnvironment = hasEnv ? 1 : 0;
    }
    
    public void setResolutionR(int width, int height)
    {
        resolutionR.x = (int) Math.floor(width / 64) * 64;
        resolutionR.y = (int) Math.floor(height / 64) * 64;
    }
    
    public int getLocalSize()
    {
        return localSize;
    }
    
    public int getGlobalSize()
    {
        return resolutionR.x * resolutionR.y;
    }
    
    public RInt2 getResolutionR()
    {
        return resolutionR;
    }
    
    public int getResolutionRSize()
    {
        return resolutionR.x * resolutionR.y;
    }
    
    public void setResolutionG(int width, int height)
    {
        resolutionR.x = width;
        resolutionR.y = height;
    }
    
    public boolean isResolutionRSame(int width, int height)
    {
        return width == resolutionR.x && height == resolutionR.y;
    }
    
    public boolean isResolutionGSame(int width, int height)
    {
        return width == resolutionG.x && height == resolutionG.y;
    }
}
