package raytracing.abstracts;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import bitmap.core.AbstractDisplay;
import bitmap.image.BitmapARGB;
import bitmap.image.BitmapRGBE;
import coordinate.generic.AbstractCoordinateFloat;
import coordinate.generic.AbstractMesh;
import coordinate.generic.AbstractRay;
import coordinate.generic.SCoord;
import coordinate.generic.VCoord;
import coordinate.shapes.TriangleShape;
import coordinate.utility.Value2Di;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import wrapper.core.OpenCLConfiguration;

/**
 *
 * @author user
 * @param <I>
 * @param <P>
 * @param <N>
 * @param <T>
 * @param <R>
 * @param <TriShape>
 */
public interface RayAPI <
        I extends RayControllerInterface,
        P extends SCoord, 
        N extends VCoord, 
        T extends AbstractCoordinateFloat,
        R extends AbstractRay<P, N>,
        TriShape extends TriangleShape<P, N, R>>
{
    enum ImageType{
        RAYTRACE_IMAGE, 
        RENDER_IMAGE,
        OVERLAY_IMAGE,
        ALL_RAYTRACE_IMAGE
    };
    
    enum RayDeviceType{
        RAYTRACE,
        RENDER
    }
    
    
    public final StringProperty message = new SimpleStringProperty();
    
    //just the size of group size, but is depended on a local size power of 2
    public static int getNumOfGroups(int length, int LOCALSIZE)
    {
        int a = length/LOCALSIZE;
        int b = length%LOCALSIZE; //has remainder
        
        return (b > 0)? a + 1 : a;
            
    }
    
    //returns a global size of power of 2
    public static int getGlobal(int size, int LOCALSIZE)
    {
        if (size % LOCALSIZE == 0) { 
            return (int) ((Math.floor(size / LOCALSIZE)) * LOCALSIZE); 
        } else { 
            return (int) ((Math.floor(size / LOCALSIZE)) * LOCALSIZE) + LOCALSIZE; 
        } 
    }
    
    default void setMessage(String string)
    {
        Platform.runLater(()->message.set(string));
    }
    
    public void initOpenCLConfiguration();    
    public OpenCLConfiguration getConfigurationCL();
    
    public void init();
        
    default BitmapARGB getBitmap(ImageType imageType){return null;}; //TO DELETE
    default void setBitmap(ImageType name, BitmapARGB bitmap){}; //TO DELETE
    default void initBitmap(ImageType name){}; //TO DELETE    
    public <D extends AbstractDisplay> D getDisplay(Class<D> displayClass);
    public <D extends AbstractDisplay> void setDisplay(Class<D> displayClass, D display);    
    public Value2Di getImageSize(ImageType imageType);    
    public void setImageSize(ImageType name, int width, int height);
    
    default void readImageFromDevice(RayDeviceType device, ImageType imageType){}; //TO DELETE
    default void applyImage(ImageType name, Supplier<BitmapARGB> supply){}; //TO DELETE
    public default int getImageWidth(ImageType imageType){return getImageSize(imageType).x;}
    public default int getImageHeight(ImageType imageType){return getImageSize(imageType).y;}
    
    public int getGlobalSizeForDevice(RayDeviceType device);
    
    public void render(RayDeviceType device);
    
    public default void initMesh(String uri) {initMesh(Paths.get(uri));}    
    public default void initMesh(URI uri)    {initMesh(Paths.get(uri));}       
    public default void initDefaultMesh(){}
    public void initMesh(Path path);
    public AbstractMesh<P, N, T, R, TriShape> getCurrentMesh();
    
    public void startDevice(RayDeviceType device);
    public void pauseDevice(RayDeviceType device);
    public void stopDevice(RayDeviceType device);
    public void resumeDevice(RayDeviceType device);
    public boolean isDeviceRunning(RayDeviceType device);
    
    public void setDevicePriority(RayDeviceType device);
    public RayDeviceType getDevicePriority();
    public boolean isDevicePriority(RayDeviceType device);
        
    public <Device extends RayDeviceInterface> Device getDevice(Class<Device> clazz);
    public <Device extends RayDeviceInterface> void setDevice(Class<Device> clazz, Device device);
    
    public I getController(String controller);
    public void set(String controller, I controllerImplementation);
        
    public void setEnvironmentMap(BitmapRGBE bitmap);
    
    public default <T> T getObject(Supplier<T> supplier)
    {
        return supplier.get();
    }
    
    default RayLight getLightManager()
    {
        return null;
    }
}
