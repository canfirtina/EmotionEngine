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
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import user.util.SecurityControl;
import user.manager.UserManager;

/**
 * FXML Controller class
 *
 * @author CanFirtina
 */
public class LoginScreenController implements Initializable, PresentedScreen {

    @FXML
    private Label warningLabel;
    @FXML
    private Hyperlink signUpButton;
    @FXML
    private Hyperlink forgotPasswordButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    
    PresentingController presentingController;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void setPresentingScreen(PresentingController presentingController) {
        
        this.presentingController = presentingController;
    }
    
    @FXML
    private void loginPressed( ActionEvent event){
        
        warningLabel.setText("");
        
//        if( SecurityControl.isValidEmailAddress( emailField.getText())){
//            
//            if(passwordField.getText().length() > 0 && UserManager.getInstance().login( emailField.getText(), SecurityControl.getCipherText(passwordField.getText()))){
                emailField.clear();
                passwordField.clear();
                warningLabel.setText("");
                presentingController.displayScreen(ScreenInfo.ProfileScreen.screenId());
//            }else
//                warningLabel.setText("Username or password is wrong");
//        }else
//            warningLabel.setText("Please enter a valid e-mail address");
    }
    
    @FXML
    private void signUpPressed( ActionEvent event){
        
        presentingController.displayScreen(ScreenInfo.SignUpScreen.screenId());
    }
    
    @FXML
    private void forgotPasswordPressed( ActionEvent event){
        
        presentingController.displayScreen(ScreenInfo.ForgotPasswordScreen.screenId());
    }
}
