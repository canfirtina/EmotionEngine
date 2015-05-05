/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ImageView logoImageView;

    private PresentingController presentingController;

    private ExecutorService executorService;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        closeButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                System.exit(0);

            }
        });

        minimizeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                presentingController.getStage().setIconified(true);
            }
        });
        File logoImageFile = new File("image/logo_full_small.png");
        if( logoImageFile.exists())
            logoImageView.setImage(new Image(logoImageFile.toURI().toString()));
        logoImageView.setFitWidth(200);
        logoImageView.setFitHeight(150);
        progressIndicator.setVisible(false);
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void setPresentingScreen(PresentingController presentingController) {

        this.presentingController = presentingController;
    }

    @FXML
    private void loginPressed(ActionEvent event) {

        warningLabel.setText("");

        if (SecurityControl.isValidEmailAddress(emailField.getText())) {
            if (passwordField.getText().length() > 0) {
                progressIndicator.setVisible(true);
                loginButton.setDisable(true);
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        if (UserManager.getInstance().login(emailField.getText(), SecurityControl.getCipherText(passwordField.getText()))) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    emailField.clear();
                                    passwordField.clear();
                                    warningLabel.setText("");
                                    presentingController.displayScreen(ScreenInfo.ProfileScreen.screenId());
                                    executorService.shutdown();
                                }
                            });
                        } else {
                            warningLabel.setText("Username or password is wrong");
                        }
                        
                        progressIndicator.setVisible(false);
                        loginButton.setDisable(false);
                        //executorService.shutdown();
                    }

                });

            }

        } else {
            warningLabel.setText("Please enter a valid e-mail address");
        }
    }

    @FXML
    private void signUpPressed(ActionEvent event) {

        presentingController.displayScreen(ScreenInfo.SignUpScreen.screenId());
    }

    @FXML
    private void forgotPasswordPressed(ActionEvent event) {

        presentingController.displayScreen(ScreenInfo.ForgotPasswordScreen.screenId());
    }
}
