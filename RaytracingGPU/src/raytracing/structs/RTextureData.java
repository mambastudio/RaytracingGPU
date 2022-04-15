/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.struct.structint.IntStruct;
import raytracing.coordinates.RInt4;

/**
 *
 * @author user
 */
public final class RTextureData extends IntStruct{
    public RInt4 diffuseTexture;       //x, y-coord, argb, has_texture (0 or 1, false or true)
    public RInt4 glossyTexture;
    public RInt4 roughnessTexture;      
    public RInt4 mirrorTexture;    
    public RInt4 parameters; //x = materialID
        
    public RTextureData()
    {
        diffuseTexture = new RInt4();
        glossyTexture = new RInt4();
        roughnessTexture = new RInt4();
        mirrorTexture = new RInt4();
        parameters = new RInt4();
    }    
}
