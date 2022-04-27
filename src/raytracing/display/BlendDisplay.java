/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.display;

import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import raytracing.abstracts.LayerDisplayAbstract;

/**
 *
 * @author user
 */
public class BlendDisplay extends LayerDisplayAbstract {
   
    public BlendDisplay(String... layers)
    {
        super();
        initDisplay(layers);        
    }
    
    @Override
    public final void initDisplay(String... layers)
    {
        for(int i = 0; i<layers.length; i++)
            if(i == 0)
                imageArray.put(layers[i], new WrappedImageView());
            else
            {
                imageArray.put(layers[i], new WrappedImageView());
                imageArray.get(layers[i]).setBlendMode(BlendMode.SRC_OVER);
            }
        
        for(String name : layers)  
        {
            ImageView imageview = imageArray.get(name);
            //imageview.setManaged(false);
            getChildren().add(imageview);
        }                    
    }
        
    @Override
    public void mousePressed(MouseEvent e)
    {     
        mouseLocX = e.getX();
        mouseLocY = e.getY();        
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {        
        float currentLocX = (float) e.getX();
        float currentLocY = (float) e.getY();
        
        float dx = (float) (currentLocX - mouseLocX);
        float dy = (float) (currentLocY - mouseLocY);
      
        if (e.isSecondaryButtonDown())
        {
            translationDepth.setValue(dy * 0.1f);            
        }
        else
        {
            Point2D pointxy = new Point2D(-dx*0.5, -dy*0.5);
            translationXY.setValue(pointxy);    
            
        }
        this.mouseLocX = currentLocX;
        this.mouseLocY = currentLocY;                    
    }   

    @Override
    public int getImageWidth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getImageHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //https://stackoverflow.com/questions/12630296/resizing-images-to-fit-the-parent-node
    class WrappedImageView extends ImageView
    {
        WrappedImageView()
        {
            setPreserveRatio(false);
        }

        @Override
        public double minWidth(double height)
        {
            return 448;
        }

        @Override
        public double prefWidth(double height)
        {
            Image image = getImage();
            if (image == null) return minWidth(height);
            return image.getWidth();
        }

        @Override
        public double maxWidth(double height)
        {
            return 16384;
        }

        @Override
        public double minHeight(double width)
        {
            return 448;
        }

        @Override
        public double prefHeight(double width)
        {
            Image image = getImage();
            if (image == null) return minHeight(width);
            return image.getHeight();
        }

        @Override
        public double maxHeight(double width)
        {
            return 16384;
        }

        @Override
        public boolean isResizable()
        {
            return true;
        }

        @Override
        public void resize(double width, double height)
        {
            Image image = getImage();
            if (image != null) 
            {
                setFitWidth(image.getWidth());
                setFitHeight(image.getHeight());
            }
        }
    }
}
