/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.abstracts;

import bitmap.image.BitmapRGBE;

/**
 *
 * @author user
 * @param <Buf1>
 * @param <Buf2>
 * @param <Buf3>
 * @param <Buf4>
 */
public interface RayEnvironment<Buf1, Buf2, Buf3, Buf4> {
    
    public Buf1 getEnvLumBuffer(); 
    
    default Buf1 getEnvLumSATBuffer(){
        throw new UnsupportedOperationException("This is not supported");
    }
    default Buf2 getEnvLumSubgridSATBuffer(){
        throw new UnsupportedOperationException("This is not supported");
    }   
    
    public Buf3 getEnvRgb();
    public Buf4 getEnvConfig();
    
    public void setEnvironmentMap(BitmapRGBE bitmap);
    public void setIsPresent(boolean isPresent);
    public boolean isPresent();
}
