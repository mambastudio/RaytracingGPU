/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.struct.structbyte.Structure;
import raytracing.geom.RInt2;

/**
 *
 * @author user
 */
public class RState extends Structure {
    public RInt2 seed;    
    public float frameCount;
    
    public RState()
    {
        seed = new RInt2();        
        frameCount = 0;
    }
    
    public void setSeed(int seed0, int seed1)
    {
        seed.x = seed0;
        seed.y = seed1;
        this.refreshGlobalArray();
    }
  
    public void setFrameCount(float frameCount)
    {
        this.frameCount = frameCount;
        this.refreshGlobalArray();
    }
}
