/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.fx.dialog;

import bitmap.display.gallery.NodeImageGalleryPanel;
import static bitmap.display.gallery.NodeImageGalleryPanel.ImageType.HDR;
import filesystem.core.file.FileObject;
import filesystem.explorer.FileExplorer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import static javafx.scene.control.ButtonType.OK;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jfx.dialog.DialogContent;
import jfx.dialog.DialogExtend;
import jfx.util.UtilityHandler;

/**
 *
 * @author user
 */
public class EnvmapDialog extends DialogExtend<Path> {
    
    int width, height;
    NodeImageGalleryPanel galleryPanel = new NodeImageGalleryPanel();    
    FileChooser hdrChooser = new FileChooser(FileObject.ExploreType.FOLDER);
    
    
    public EnvmapDialog(int width, int height)
    {
        this.width = width;
        this.height = height;
        
        this.galleryPanel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.galleryPanel.setImageFilter(HDR);
        hdrChooser.addExtensions(new FileExplorer.ExtensionFilter("HDR", ".hdr"));
        setup();
    }

    @Override
    public void setup() {
        //dialog content
        DialogContent<Path> galleryContent = new DialogContent<>();
        
        //gallery image load
        Button imageLoadBtn = new Button("Load to Gallery");        
        imageLoadBtn.setOnAction(e->{
            Optional<FileObject> hdrFolder = hdrChooser.showAndWait(UtilityHandler.getScene().getWindow());
            
            if(hdrFolder.isPresent())
            {
                
                galleryPanel.addImagesFromDirectory(hdrFolder.get().getPath());
            }
        });
        
        HBox hbox = new HBox();
        hbox.getChildren().add(imageLoadBtn);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(3, 3, 3, 3));
        
        
        VBox vbox = new VBox();        
        vbox.getChildren().addAll(hbox, galleryPanel);
        VBox.setVgrow(galleryPanel, Priority.ALWAYS);
        
        galleryContent.setContent(vbox, DialogContent.DialogStructure.HEADER_FOOTER);
        
        //dialog pane (main window)
        init(
                galleryContent,                
                Arrays.asList(
                        new ButtonType("Load", ButtonBar.ButtonData.OK_DONE),
                        new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)), 
                width, height, 
                false);
        
        //if closed, return something    
        //set buttons and click type return
        this.setSupplier((buttonType)->{
            if(buttonType == OK)
            {                 
                return galleryPanel.getSelectedFiles().get(0).toPath();               
            }
            else
                return null;
        });
    }
    
}
