/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * FXML Controller class
 *
 * @author CanFirtina
 */
public class TutorialScreenController extends MediaView implements Initializable, PresentedScreen {

    PresentingController presentingController;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        

    }    

    @Override
    public void setPresentingScreen(PresentingController presentingController) {
        
        System.out.println("alsjdalskd");
        this.presentingController = presentingController;
    }
}
