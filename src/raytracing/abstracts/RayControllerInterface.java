package raytracing.abstracts;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.fxml.Initializable;

/**
 *
 * @author user
 * @param <A>
 */
public interface RayControllerInterface<A extends RayAPI> extends Initializable {
    public enum PrintType {
        PROGRESS,
        ERROR
    }
        
    public void setAPI(A api);        
}
