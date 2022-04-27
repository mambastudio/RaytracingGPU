/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing;

import raytracing.display.BlendDisplay;
import bitmap.display.gallery.HDRImageLoaderFactory;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfx.util.UtilityHandler;
import static raytracing.abstracts.RayAPI.ImageType.OVERLAY_IMAGE;
import static raytracing.abstracts.RayAPI.ImageType.RAYTRACE_IMAGE;

/**
 *
 * @author user
 */
public class RaytracingGPU extends Application {
    static 
    {
        HDRImageLoaderFactory.install();        
    }

    public RaytracingGPU() {
        
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RaytraceUI.fxml"));
        Parent root = loader.load();
        
        
       
        //START EVERYTHING HERE
        //create api and set display
        RaytraceAPI api = new RaytraceAPI();       
        api.setDisplay(BlendDisplay.class, new BlendDisplay(RAYTRACE_IMAGE.name(), OVERLAY_IMAGE.name()));
                
        //set controller (which sets display inside)
        RaytraceUIController controller = (RaytraceUIController)loader.getController();
        api.set("controller", controller);        
        
        //init mesh, device, images
        api.init();
               
        //complete launch of ui
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("RaytraceGPU");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(550);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            Platform.runLater(()->System.exit(0));
        });
       
        //we now have the scene, set it to be accessed anywhere
        UtilityHandler.setScene(scene);
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        launch(args);
    }
    
}
