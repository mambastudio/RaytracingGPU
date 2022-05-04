/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.fx;

import coordinate.parser.attribute.MaterialT;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import jfx.dnd.ReadObjectsHelper;
import jfx.dnd.WriteObjectsHelper;
import raytracing.structs.RMaterial;

/**
 *
 * @author user
 */
public class MaterialFX implements Serializable{
    //surface
    public transient SurfaceParameterFX param; 
    public transient StringProperty name;    
    private transient RMaterial rmaterial;
    
    public MaterialFX(String name)
    {
        this();
        this.name.set(name);
    }
    
    public MaterialFX()
    {
        param = new SurfaceParameterFX();        
        name = new SimpleStringProperty("default");
    }
    
    public MaterialFX(MaterialT mat)
    {
        this();
        this.setMaterialT(mat);
    }
   
    public Image getDiffuseTexture()
    {
        if(param.diffuseTexture.get() != null)
            return param.diffuseTexture.get().getImage();
        return null;
    }
    
   
    public Image getGlossyTexture()
    {
        if(param.glossyTexture.get() != null)
            return param.glossyTexture.get().getImage();
        return null;
    }
    
    
    public Image getRoughnessTexture()
    {
        if(param.roughnessTexture.get() != null)
            return param.roughnessTexture.get().getImage();
        return null;
    }
    
    public final void init()
    {
        param = new SurfaceParameterFX();        
        name = new SimpleStringProperty("default");
    }
        
    public void setMaterialT(MaterialT mat)
    {        
        this.name.set(mat.getNameString());
        this.param.diffuse_color.set(mat.diffuse.getColorFX());  
        this.param.mirror_color.set(mat.reflection.getColorFX());
        this.param.emission_color.set(mat.emitter.getColorFX());        
    }
    
    public RMaterial getRMaterial()
    {
        //TODO: Needs to affect changes done in this class
        return rmaterial;
    }
   
    public void setMaterialFX(MaterialFX m) {
        param.set(m.param);
        name.set(m.name.get());       
    }
   
    public MaterialFX copy() {
        MaterialFX mat = new MaterialFX();
        mat.param.set(param);
        mat.name.set(name.get());
        return mat;
    }
    
    public void setDiffuseColor(float r, float g, float b)
    {
        float r_ = getRange(r, 0, 1);
        float g_ = getRange(g, 0, 1);
        float b_ = getRange(b, 0, 1);
        param.diffuse_color.set(Color.color(r_, g_, b_));          
    }
    
    public void setDiffuseAmount(float value)
    {
        float level = getRange(value, 0, 1);        
        param.diffuse_param.x.set(level);        
    }
    
    public void setGlossyColor(float r, float g, float b)
    {
        float r_ = getRange(r, 0, 1);
        float g_ = getRange(g, 0, 1);
        float b_ = getRange(b, 0, 1);
        param.glossy_color.set(Color.color(r_, g_, b_));          
    }
    
    public void setGlossyAmount(float value)
    {
        float level = getRange(value, 0, 1);        
        param.glossy_param.x.set(level);        
    }
    
    public void setGlossRoughness(float ax, float ay)
    {
        float ax_ = getRange(ax, 0, 1);    
        float ay_ = getRange(ay, 0, 1);
        param.glossy_param.y.set(ax_);
        param.glossy_param.z.set(ay_);        
    }
    
    public void setGlossRoughness(float a)
    {
        float a_ = getRange(a, 0, 1);           
        param.glossy_param.y.set(a_);
        param.glossy_param.z.set(a_);        
    }
    
    private float getRange(float value, float min, float max)
    {
        float min_, max_;
        
        if(min < max)
        {
            min_ = min;
            max_ = max;
        }
        else if(max < min)
        {
            min_ = max;
            max_ = min;
        }
        else
        {
            return value;
        }        
        
        float level = value;
        if(level <= min_) level = min_;
        if(level >= max_) level = max_;
        
        return level;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(param);        
        WriteObjectsHelper.writeAllProp(s, 
                name);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        init();      
        param = (SurfaceParameterFX) s.readObject();        
        ReadObjectsHelper.readAllProp(s, 
                name);
        
    }
    
    @Override
    public String toString()
    {
        return name.get();
    }
}
