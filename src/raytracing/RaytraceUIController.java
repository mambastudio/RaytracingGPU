/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing;

import bitmap.image.BitmapRGBE;
import bitmap.reader.HDRBitmapReader;
import raytracing.display.BlendDisplay;
import raytracing.device.RaytraceDevice;

import coordinate.model.OrientationModel;
import coordinate.parser.attribute.MaterialT;
import coordinate.parser.obj.OBJInfo;
import filesystem.core.file.FileObject;
import filesystem.explorer.FileExplorer;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import static javafx.scene.input.MouseButton.PRIMARY;
import javafx.scene.layout.StackPane;
import jfx.dialog.type.DialogProcess;
import jfx.util.UtilityHandler;
import static raytracing.abstracts.RayAPI.RayDeviceType.RAYTRACE;
import static raytracing.abstracts.RayAPI.ImageType.RAYTRACE_IMAGE;
import raytracing.abstracts.RayControllerInterface;
import raytracing.device.RaytraceDevice.ShadeType;
import static raytracing.device.RaytraceDevice.ShadeType.COLOR_SHADE;
import static raytracing.device.RaytraceDevice.ShadeType.MATERIAL_SHADE;
import static raytracing.device.RaytraceDevice.ShadeType.NORMAL_SHADE;
import static raytracing.device.RaytraceDevice.ShadeType.TEXTURE_SHADE;
import static raytracing.device.RaytraceDevice.ShadeType.TRAVERSE_SHADE;
import raytracing.fx.MaterialFX;
import raytracing.fx.dialog.EnvmapDialog;
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
public class RaytraceUIController implements Initializable, RayControllerInterface<RaytraceAPI>{

    /**
     * Initializes the controller class.
     */    
    @FXML
    StackPane viewportPane;   
    @FXML
    ComboBox<ShadeType> shadeTypeCombo;    
    @FXML
    TreeView<MaterialFX> sceneMaterial;
    @FXML
    CheckBox envmapOnCheckBox;
    
    
    private RaytraceAPI api;     
    private final OrientationModel<RPoint3, RVector3, RRay, RBound> orientation = new OrientationModel(RPoint3.class, RVector3.class);
       
    private FileChooser objChooser = null;
    
    private List<MaterialFX> matFXList;
    
    //env light dialog
    private final EnvmapDialog explorerDialog = new EnvmapDialog(700, 500);
    
    private final ObjectProperty<BitmapRGBE> envBitmap = new SimpleObjectProperty<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //hide root
        sceneMaterial.setShowRoot(false);
        sceneMaterial.setRoot(new TreeItem(new MaterialFX()));
        
        //init shade type combo box
        shadeTypeCombo.getItems().add(COLOR_SHADE);
        shadeTypeCombo.getItems().add(NORMAL_SHADE);
        shadeTypeCombo.getItems().add(TEXTURE_SHADE);
        shadeTypeCombo.getItems().add(MATERIAL_SHADE);
        shadeTypeCombo.getItems().add(TRAVERSE_SHADE);
        shadeTypeCombo.getSelectionModel().select(COLOR_SHADE);
        
        shadeTypeCombo.setOnAction(event->{           
            ShadeType selectedItem = shadeTypeCombo.getSelectionModel().getSelectedItem();            
            if(null != selectedItem)
                switch (selectedItem) {
                case COLOR_SHADE:
                    api.getDevice(RaytraceDevice.class).setShadeType(COLOR_SHADE);
                    break;
                case NORMAL_SHADE:
                    api.getDevice(RaytraceDevice.class).setShadeType(NORMAL_SHADE);
                    break;
                case TEXTURE_SHADE:
                    api.getDevice(RaytraceDevice.class).setShadeType(TEXTURE_SHADE);
                    break;
                case MATERIAL_SHADE:
                    api.getDevice(RaytraceDevice.class).setShadeType(MATERIAL_SHADE);
                    break;
                case TRAVERSE_SHADE:
                    api.getDevice(RaytraceDevice.class).setShadeType(TRAVERSE_SHADE);
                    break;
                default:
                    break;
            }
           
        });
        
        //obj file chooser
        objChooser = new FileChooser();
        objChooser.addExtensions(new FileExplorer.ExtensionFilter("OBJ", ".obj"));
        
        //disable checkbox when application starts (no env by default)
        envmapOnCheckBox.disableProperty().bind(envBitmap.isNull());
        envmapOnCheckBox.selectedProperty().addListener((o, ov, nv)->{          
            api.setIsEnvmapPresent(nv);
            api.getDevice(RaytraceDevice.class).resume(api.getRayConfiguration());
        });
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
            api.getDevice(RaytraceDevice.class).resume(api.getRayConfiguration());
        });
        
        api.getDisplay(BlendDisplay.class).translationXY.addListener((observable, old_value, new_value) -> {   
            //current device that is rendering is raytrace
            if(!api.isDevicePriority(RAYTRACE)) return;
            
            orientation.rotateX(api.getDevice(RaytraceDevice.class).getCameraModel(), (float) new_value.getX());
            orientation.rotateY(api.getDevice(RaytraceDevice.class).getCameraModel(), (float) new_value.getY());
            api.getDevice(RaytraceDevice.class).resume(api.getRayConfiguration());            
        });
        
        api.getDisplay(BlendDisplay.class).setOnMouseClicked(e->{
            
          
            if(e.getClickCount() == 2 && e.getButton() == PRIMARY)
            {
                Point2D xy = api.getDisplay(BlendDisplay.class).getMouseOverXY(e, RAYTRACE_IMAGE.name());
               
                //get instance in current pixel
                int instance = api.getDevice(RaytraceDevice.class).getInstanceValue(xy.getX(), xy.getY());                
                if(instance > -1)
                {
                    RBound bound = new RBound();
                    api.getDevice(RaytraceDevice.class).findBound(instance, bound);
                    api.repositionCameraToBoundRT(bound);
                    api.getDevice(RaytraceDevice.class).setPriorityBound(bound);
                    api.getDevice(RaytraceDevice.class).resume(api.getRayConfiguration());
                }
            }
        });
        
        
        //if window or layout changes size, raytrace with new viewport settings
        ChangeListener<Number> listener = (o, ov, nv)->{
            double displayWidth     = viewportPane.getWidth();
            double displayHeight    = viewportPane.getHeight();   
            
            if(displayWidth < 448) //always power of 2 and divisible by 64 (local size)
                displayWidth = 448;
            if(displayHeight < 448)
                displayHeight = 448;
                      
            //update global ray configuration and raytrace new
            api.getRayConfiguration().setResolutionR((int)displayWidth, (int)displayHeight);            
            api.getDevice(RaytraceDevice.class).resume(api.getRayConfiguration());
        };
        
        viewportPane.widthProperty().addListener(listener);
        viewportPane.heightProperty().addListener(listener);
        
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
                api.getDevice(RaytraceDevice.class).resume(api.getRayConfiguration());
            });
            processDialog.showAndWait(UtilityHandler.getScene());
        }
        setupCurrentMaterialFX(); //load material to UI
    }
    
    public void loadEnvMap(ActionEvent e)
    {
        
        Optional<Path> path = explorerDialog.showAndWait(UtilityHandler.getScene());
        HDRBitmapReader reader = new HDRBitmapReader();
            
        if(path.isPresent())
        {
            BitmapRGBE bitmapRGBE = reader.load(path.get());
            envBitmap.set(bitmapRGBE);            
            api.setEnvironmentMap(bitmapRGBE);
            api.getDevice(RaytraceDevice.class).setEnvMapInKernel();
            api.getDevice(RaytraceDevice.class).resume(api.getRayConfiguration());     
        }
    }
    
    public void resetCameraToScene(ActionEvent e)
    {
        api.repositionCameraToSceneRT();
        api.getDevice(RaytraceDevice.class).resume(api.getRayConfiguration());
    }
    
    //setup ui material openOBJFile(e) in this class and init RaytraceAPI.java
    public void setupCurrentMaterialFX()
    {
        ArrayList<MaterialT> materialsT = api.getCurrentMesh().getMaterialList();
        matFXList = Arrays.asList(new MaterialFX[materialsT.size()]); //Fixed
        for(int i = 0; i<matFXList.size(); i++)
        {            
            matFXList.set(i, new MaterialFX(materialsT.get(i)));           
        }
        
        sceneMaterial.getRoot().getChildren().clear();         
        for(MaterialFX matFX: matFXList)
        {
            sceneMaterial.getRoot().getChildren().add(new TreeItem(matFX));
        }  
    }
    
}