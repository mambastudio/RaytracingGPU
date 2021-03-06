package raytracing.abstracts;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import coordinate.parser.attribute.MaterialT;
import javafx.scene.image.Image;

/**
 *
 * @author user
 * @param <M>
 */
public interface RayMaterial <M extends RayMaterial> {    
    
    public void setMaterial(M m);
    public M copy();
    public void setMaterialT(MaterialT t);
    default Image getDiffuseTexture(){return null;}
    default Image getGlossyTexture(){return null;}
    default Image getRoughnessTexture(){return null;}
}
