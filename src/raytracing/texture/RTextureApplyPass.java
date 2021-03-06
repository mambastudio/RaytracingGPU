/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.texture;

import javafx.scene.image.Image;
import raytracing.abstracts.RayAPI;
import raytracing.structs.RTextureData;
import wrapper.core.CMemory;
import wrapper.core.memory.values.IntValue;

/**
 *
 * @author user
 */
public class RTextureApplyPass {
    private CMemory<RTextureData> texBuffer = null;
    private int[] texIntBuffer = null;    
    private CMemory<IntValue> count = null;
    private int[] countIntBuffer = null;
    private RayAPI api = null;
    
    private final TextureInfoCache textureData;
    
    
    public RTextureApplyPass(RayAPI api, CMemory<RTextureData> texBuffer, CMemory<IntValue> count) {
        this.texBuffer = texBuffer;
        this.count = count;
        this.texIntBuffer = (int[]) texBuffer.getBufferArray();
        this.countIntBuffer = (int[]) count.getBufferArray();
        this.api = api;
        this.textureData = new TextureInfoCache(texIntBuffer);
    }
   
    public void process()
    {
        /*        
        texBuffer.transferFromDevice();
        count.transferFromDevice();
               
        for(int index = 0; index < countIntBuffer[0]; index++)
        {            
            textureData.setIndex(index);
            if(textureData.hasDiffuseTexture())
            {
                Image image = api.getMaterial(textureData.getMaterialIndex()).getDiffuseTexture(); //TO CORRECT/UPDATE
                float x = textureData.getDiffuseTextureU();
                float y = textureData.getDiffuseTextureV();
                             
                if(image != null)
                {
                    float dx = (float) x * ((float)image.getWidth() - 1);
                    float dy = (float) y * ((float)image.getHeight() - 1);

                    textureData.setDiffuseArgb(image, (int) dx, (int)dy);
                }
            }
            
            if(textureData.hasGlossyTexture())
            {   
                Image image = api.getMaterial(textureData.getMaterialIndex()).getGlossyTexture(); //TO CORRECT/UPDATE
                float x = textureData.getGlossyTextureU();
                float y = textureData.getGlossyTextureV();
                
                if(image != null)
                {
                    float dx = (float) x * ((float)image.getWidth() - 1);
                    float dy = (float) y * ((float)image.getHeight() - 1);

                    textureData.setGlossyArgb(image, (int) dx, (int)dy);
                }
            }
            
            if(textureData.hasRoughnessTexture())
            {   
                Image image = api.getMaterial(textureData.getMaterialIndex()).getRoughnessTexture(); //TO CORRECT/UPDATE
                float x = textureData.getRoughnessTextureU();
                float y = textureData.getRoughnessTextureV();
                
                if(image != null)
                {
                    float dx = (float) x * ((float)image.getWidth() - 1);
                    float dy = (float) y * ((float)image.getHeight() - 1);

                    textureData.setRoughnessArgb(image, (int) dx, (int)dy);
                }
            }
           
        }
        texBuffer.transferToDevice();
        */
    }
}
