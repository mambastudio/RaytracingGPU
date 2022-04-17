/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.struct.structbyte.Structure;
import raytracing.geom.RColor4;
import raytracing.geom.RPoint3;

/**
 *
 * @author user
 */
public class RSurfaceParameter2 extends Structure {
    //this surface is done by texture
    public boolean          isDiffuseTexture;
    public boolean          isGlossyTexture;
    public boolean          isRoughnessTexture;
    public boolean          isMirrorTexture;
    
    //brdf parameters
    public RColor4          diffuse_color;
    public RPoint3          diffuse_param;
    public RColor4          glossy_color;
    public RPoint3          glossy_param;
    public RColor4          mirror_color;
    public RPoint3          mirror_param;
    public RColor4          emission_color;
    public RPoint3          emission_param;
    
    public RSurfaceParameter2()
    {
        isDiffuseTexture = false;
        isGlossyTexture = false;
        isRoughnessTexture = false;
        isMirrorTexture = false;
        
        diffuse_color   = new RColor4(0.95f, 0.95f, 0.95f);
        diffuse_param   = new RPoint3(1, 0, 0);
        glossy_color    = new RColor4(0.95f, 0.95f, 0.95f);
        glossy_param    = new RPoint3();
        mirror_color    = new RColor4(0.95f, 0.95f, 0.95f);
        mirror_param    = new RPoint3();
        emission_color  = new RColor4(1f, 1f, 1f);
        emission_param  = new RPoint3();
    }
    
    public RSurfaceParameter2 copy()
    {
        RSurfaceParameter2 param    = new RSurfaceParameter2();
        param.isDiffuseTexture      = isDiffuseTexture;
        param.isGlossyTexture       = isGlossyTexture;
        param.isRoughnessTexture    = isRoughnessTexture;
        param.isMirrorTexture       = isMirrorTexture;
        
        param.diffuse_color         = diffuse_color.copy();
        param.diffuse_param         = diffuse_param.copy(); 
        param.glossy_color          = glossy_color .copy(); 
        param.glossy_param          = glossy_param.copy();  
        param.mirror_color          = mirror_color.copy();  
        param.mirror_param          = mirror_param.copy();  
        param.emission_color        = emission_color.copy();
        param.emission_param        = emission_param.copy();
        
        return param;
    }
}
