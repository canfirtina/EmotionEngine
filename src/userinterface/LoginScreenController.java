/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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
    private Button signUpButton;
    @FXML
    private Button forgotPasswordButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
	@FXML
	private ProgressIndicator progressIndicator;
    
    private PresentingController presentingController;
	
	private ExecutorService executorService;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
		executorService = Executors.newSingleThreadExecutor();
		
    }    

    @Override
    public void setPresentingScreen(PresentingController presentingController) {
        
        this.presentingController = presentingController;
    }
    
    @FXML
    private void loginPressed( ActionEvent event){
        
        warningLabel.setText("");
        
        if( SecurityControl.isValidEmailAddress( emailField.getText())){
            if(passwordField.getText().length() > 0){
				progressIndicator.setVisible(true);
				executorService.execute(new Runnable(){
					@Override
					public void run() {
						
						if(UserManager.getInstance().login( emailField.getText(), SecurityControl.getCipherText(passwordField.getText()))){
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									progressIndicator.setVisible(false);
									emailField.clear();
									passwordField.clear();
									warningLabel.setText("");
									presentingController.displayScreen(ScreenInfo.ProfileScreen.screenId());
									executorService.shutdown();
								}
							});
						}
						else 
							warningLabel.setText("Username or password is wrong");
					}
					
				});
				
            }
                
        }else
            warningLabel.setText("Please enter a valid e-mail address");
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
