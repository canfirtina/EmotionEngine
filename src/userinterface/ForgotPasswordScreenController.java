/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import user.manager.UserManager;
import user.util.SecurityControl;

/**
 * FXML Controller class
 *
 * @author CanFirtina
 */
public class ForgotPasswordScreenController implements Initializable, PresentedScreen {

    @FXML
    private TextField emailField;
    
    PresentingController presentingController;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void setPresentingScreen(PresentingController presentingController) {
        
        this.presentingController = presentingController;
    }
    
    @FXML
    private void sendPressed( ActionEvent event){
        
        emailField.clear();
        
        presentingController.displayScreen(ScreenInfo.LoginScreen.screenId());
    }
    
    @FXML
    private void cancelButtonPressed( ActionEvent event){
        
        emailField.clear();
        
        presentingController.displayScreen( ScreenInfo.LoginScreen.screenId());
    }
}
