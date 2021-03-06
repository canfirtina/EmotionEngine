/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import user.util.SecurityControl;
import user.manager.UserManager;

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
    private Button closeButton;
    @FXML
    private Button minimizeButton;

    PresentingController presentingController;

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
    }

    @Override
    public void setPresentingScreen(PresentingController presentingController) {

        this.presentingController = presentingController;
    }

    @FXML
    private void signUpPressed(ActionEvent event) {

        if (SecurityControl.isValidEmailAddress(emailField.getText())) {

            if (passwordField.getText().length() >= 8) {

                if (UserManager.getInstance().newUser(emailField.getText(), SecurityControl.getCipherText(passwordField.getText()))) {
                    emailField.clear();
                    passwordField.clear();
                    warningLabel.setText("");
                } else {
                    warningLabel.setText("Something went wrong");
                }
            } else {
                warningLabel.setText("Password length must be at least 8 characters");
            }
        } else {
            warningLabel.setText("Invalid email");
        }
        presentingController.displayScreen(ScreenInfo.LoginScreen.screenId());
    }

    @FXML
    private void cancelButtonPressed(ActionEvent event) {

        emailField.clear();
        passwordField.clear();
        warningLabel.setText("");

        presentingController.displayScreen(ScreenInfo.LoginScreen.screenId());
    }

    @Override
    public void willPresented(){

    }
}
