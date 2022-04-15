/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.parser.attribute.MaterialT;
import coordinate.struct.structbyte.Structure;
import raytracing.abstracts.RayMaterial;

/**
 *
 * @author user
 */
public class RMaterial extends Structure implements RayMaterial<RMaterial>  {
     //surface
    public RSurfaceParameter2 param; 
       

    public RMaterial()
    {
        param = new RSurfaceParameter2();
    }
    
    public void setDiffuse(float r, float g, float b)
    {
        param.diffuse_color.set(r, g, b);
        this.refreshGlobalArray();
    }
    
    public void setEmitter(float r, float g, float b)
    {
        param.emission_color.set(r, g, b);
        param.emission_param.set('x', 1);
        param.emission_param.set('y', 15);
        this.refreshGlobalArray();
    }
    
    @Override
    public void setMaterial(RMaterial mat) {
        param = mat.param.copy();       
        this.refreshGlobalArray();
    }

    @Override
    public RMaterial copy() {
        RMaterial mat = new RMaterial();
        mat.param = param.copy();
        return mat;
    }
    
    public void setSurfaceParameter(RSurfaceParameter2 param)
    {
        this.param = param;
    }

    @Override
    public void setMaterialT(MaterialT mat) {
        param.diffuse_color.set(mat.diffuse.r, mat.diffuse.g, mat.diffuse.b, mat.diffuse.w);        
        this.refreshGlobalArray();
    }
}
