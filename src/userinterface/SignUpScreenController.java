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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import shared.ScreenInfo;
import shared.SecurityControl;
import usermanager.UserManager;

/**
 * FXML Controller class
 *
 * @author CanFirtina
 */
public class SignUpScreenController implements Initializable, PresentedScreen {
    
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label warningLabel;
    @FXML
    private Hyperlink cancelButton;
    
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
    private void signUpPressed( ActionEvent event){
        
        if( SecurityControl.isValidEmailAddress(emailField.getText())){
            
            if( passwordField.getText().length() >= 8){
                
                if( UserManager.getInstance().newUser( emailField.getText(), SecurityControl.getCipherText(passwordField.getText()))){
                    emailField.clear();
                    passwordField.clear();
                    warningLabel.setText("");
                }else
                    warningLabel.setText("Something went wrong");
            }else
                warningLabel.setText("Password length must be at least 8 characters");
        }else
            warningLabel.setText("Invalid email");
        presentingController.displayScreen(ScreenInfo.LoginScreen.screenId());
    }
    
    @FXML
    private void cancelButtonPressed( ActionEvent event){
        
        emailField.clear();
        passwordField.clear();
        warningLabel.setText("");
        
        presentingController.displayScreen( ScreenInfo.LoginScreen.screenId());
    }
}
