/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing;

import raytracing.display.BlendDisplay;
import raytracing.device.RaytraceDevice;

import coordinate.model.OrientationModel;
import coordinate.parser.attribute.MaterialT;
import coordinate.parser.obj.OBJInfo;
import filesystem.core.file.FileObject;
import filesystem.explorer.FileExplorer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import static javafx.scene.input.MouseButton.PRIMARY;
import javafx.scene.layout.StackPane;
import jfx.dialog.type.DialogProcess;
import jfx.util.UtilityHandler;
import static raytracing.abstracts.RayAPI.DeviceType.RAYTRACE;
import static raytracing.abstracts.RayAPI.ImageType.RAYTRACE_IMAGE;
import raytracing.abstracts.RayControllerInterface;
import static raytracing.device.RaytraceDevice.ShadeType.COLOR_SHADE;
import static raytracing.device.RaytraceDevice.ShadeType.NORMAL_SHADE;
import static raytracing.device.RaytraceDevice.ShadeType.TEXTURE_SHADE;
import raytracing.fx.MaterialFX2;
import raytracing.fx.dialog.FileChooser;
import raytracing.fx.dialog.OBJSettingDialogFX;
import raytracing.geom.RPoint3;
import raytracing.geom.RVector3;
import raytracing.structs.RBound;
import raytracing.structs.RRay;

/**
 * FXML Controller class
 *
 * @author user
 */
public class RaytraceUIController implements Initializable, RayControllerInterface<RaytraceAPI, MaterialFX2>{

    /**
     * Initializes the controller class.
     */    
    @FXML
    StackPane viewportPane;
    @FXML
    ToggleGroup shadeTypeGroup;
    @FXML
    RadioButton shadeRadioButton;
    @FXML
    RadioButton normalRadioButton;
    @FXML
    RadioButton textureRadioButton;
    
    
    private RaytraceAPI api;     
    private final OrientationModel<RPoint3, RVector3, RRay, RBound> orientation = new OrientationModel(RPoint3.class, RVector3.class);
       
    
    private FileChooser objChooser = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        shadeRadioButton.setUserData(COLOR_SHADE);
        normalRadioButton.setUserData(NORMAL_SHADE);
        textureRadioButton.setUserData(TEXTURE_SHADE);
        
        shadeTypeGroup.selectedToggleProperty().addListener((o, ov, nv)->{
            if(nv != null)
            {
                if(nv.getUserData() == COLOR_SHADE)
                    api.getDevice(RaytraceDevice.class).setShadeType(COLOR_SHADE);
                else if(nv.getUserData() == NORMAL_SHADE)
                    api.getDevice(RaytraceDevice.class).setShadeType(NORMAL_SHADE);
                else if(nv.getUserData() == TEXTURE_SHADE)
                    api.getDevice(RaytraceDevice.class).setShadeType(TEXTURE_SHADE);
            }
        });
        
        //obj file chooser
        objChooser = new FileChooser();
        objChooser.addExtensions(new FileExplorer.ExtensionFilter("OBJ", ".obj"));
    }    

    @Override
    public void displaySceneMaterial(ArrayList<MaterialT> materials) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAPI(RaytraceAPI api) {
        //set api
        this.api = api;
        
        //setup display
        setupDisplay(api);
    }
    
    private void setupDisplay(RaytraceAPI api)
    {
        api.getDisplay(BlendDisplay.class).translationDepth.addListener((observable, old_value, new_value) -> {  
            //current device that is rendering is raytrace
            if(!api.isDevicePriority(RAYTRACE)) return;
           
            orientation.translateDistance(api.getDevice(RaytraceDevice.class).getCameraModel(), 
                    new_value.floatValue() * 0.1f * api.getDevice(RaytraceDevice.class).getPriorityBound().getMaximumExtent());     
            api.getDevice(RaytraceDevice.class).resume();
        });
        
        api.getDisplay(BlendDisplay.class).translationXY.addListener((observable, old_value, new_value) -> {   
            //current device that is rendering is raytrace
            if(!api.isDevicePriority(RAYTRACE)) return;
            
            orientation.rotateX(api.getDevice(RaytraceDevice.class).getCameraModel(), (float) new_value.getX());
            orientation.rotateY(api.getDevice(RaytraceDevice.class).getCameraModel(), (float) new_value.getY());
            api.getDevice(RaytraceDevice.class).resume();            
        });
        
        api.getDisplay(BlendDisplay.class).setOnMouseClicked(e->{
            if(e.getClickCount() == 2 && e.getButton() == PRIMARY)
            {
                Point2D xy = api.getDisplay(BlendDisplay.class).getMouseOverXY(e, RAYTRACE_IMAGE.name());
                
                //get raytrace device
                
                //get instance in current pixel
                int instance = api.getDevice(RaytraceDevice.class).getInstanceValue(xy.getX(), xy.getY());
                
                if(instance > -1)
                {
                    RBound bound = new RBound();
                    api.getDevice(RaytraceDevice.class).findBound(instance, bound);
                    api.repositionCameraToBoundRT(bound);
                    api.getDevice(RaytraceDevice.class).setPriorityBound(bound);
                    api.getDevice(RaytraceDevice.class).resume();
                    
                }
            }
        });
        
        //add display component
        viewportPane.getChildren().add(api.getDisplay(BlendDisplay.class));
    }
    
    public boolean showOBJStatistics(OBJInfo info)
    {
        OBJSettingDialogFX objSettingDialog = new OBJSettingDialogFX(info);
        Optional<Boolean> optional = objSettingDialog.showAndWait(UtilityHandler.getScene()); 
        return optional.get();
    }
    
    public void openOBJFile(ActionEvent e)
    {        
        Optional<FileObject> fileOption = objChooser.showAndWait(
                UtilityHandler.getScene().getWindow());
        if(fileOption.isPresent())
        {
            DialogProcess processDialog = new DialogProcess(300, 100);
            processDialog.setRunnable(()->{
                api.initMesh(fileOption.get().getFile().toURI());
                api.getDevice(RaytraceDevice.class).resume();
            });
            processDialog.showAndWait(UtilityHandler.getScene());
        }
    }
    
    public void resetCameraToScene(ActionEvent e)
    {
        api.repositionCameraToSceneRT();
        api.getDevice(RaytraceDevice.class).resume();
    }
}