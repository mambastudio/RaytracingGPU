/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.parser.attribute.MaterialT;
import coordinate.struct.structbyte.StructBufferMemory;
import raytracing.abstracts.RayMaterial;

/**
 *
 * @author user
 */
public class RMaterial extends StructBufferMemory implements RayMaterial<RMaterial>  {
     //surface
    public RSurfaceParameter param; 
       

    public RMaterial()
    {
        param = new RSurfaceParameter();
    }
    
    public void setDiffuse(float r, float g, float b)
    {
        param.diffuse_color.set(r, g, b);
        this.refreshGlobalBuffer();
    }
    
    public void setEmitter(float r, float g, float b)
    {
        param.emission_color.set(r, g, b);
        param.emission_param.set('x', 1);
        param.emission_param.set('y', 15);
        this.refreshGlobalBuffer();
    }
    
    @Override
    public void setMaterial(RMaterial mat) {
        param = mat.param.copy();       
        this.refreshGlobalBuffer();
    }

    @Override
    public RMaterial copy() {
        RMaterial mat = new RMaterial();
        mat.param = param.copy();
        return mat;
    }
    
    public void setSurfaceParameter(RSurfaceParameter param)
    {
        this.param = param;
    }

    @Override
    public void setMaterialT(MaterialT mat) {
        param.diffuse_color.set(mat.diffuse.r, mat.diffuse.g, mat.diffuse.b, mat.diffuse.w); 
        param.mirror_color.set(mat.reflection.r, mat.reflection.g, mat.reflection.b, mat.reflection.w); 
        param.emission_color.set(mat.emitter.r, mat.emitter.g, mat.emitter.b, mat.emitter.w);         
        this.refreshGlobalBuffer();
    }
}
