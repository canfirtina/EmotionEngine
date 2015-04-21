/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import shared.ScreenInfo;
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
    
    PresentingController presentingController;
    private MessageDigest md;
    
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
        
        if( isValidEmailAddress(emailField.getText())){
            
            if( passwordField.getText().length() >= 8){
                
                if( UserManager.getInstance().newUser( emailField.getText(), getCipherText(passwordField.getText())))
                    System.out.println("ok");
                else
                    System.out.println("not ok");
            }else{
                
                
            }
        }else{
            
            
        }
        presentingController.displayScreen(ScreenInfo.LoginScreen.screenId());
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
