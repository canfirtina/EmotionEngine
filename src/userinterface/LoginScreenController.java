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
import shared.ScreenInfo;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import usermanager.UserManager;

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
    private MessageDigest md;
    
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
        
        if( isValidEmailAddress( emailField.getText())){
            
            if(UserManager.getInstance().login( emailField.getText(), getCipherText(passwordField.getText())))
                System.out.println("login succ");
            else
                System.out.println("fucked up");
        }else{
            
            warningLabel.setText("Please enter a valid e-mail address");
        }
    }
    
    @FXML
    private void signUpPressed( ActionEvent event){
        
        presentingController.displayScreen(ScreenInfo.SignUpScreen.screenId());
    }
    
    @FXML
    private void forgotPasswordPressed( ActionEvent event){
        
        presentingController.displayScreen(ScreenInfo.ForgotPasswordScreen.screenId());
    }
    
    private boolean isValidEmailAddress( String email) {
           String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
           java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
           java.util.regex.Matcher m = p.matcher(email);
           return m.matches();
    }
    
    private String getCipherText( String password){
        
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] passBytes = password.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<digested.length;i++)
                sb.append(Integer.toHexString(0xff & digested[i]));
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
    
}
