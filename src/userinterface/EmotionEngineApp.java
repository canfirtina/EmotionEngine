/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shared.ScreenInfo;

/**
 *
 * @author CanFirtina
 */
public class EmotionEngineApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        EmotionEngineController mainContainer = new EmotionEngineController();
        mainContainer.addScreen(ScreenInfo.LoginScreen.screenId(), ScreenInfo.LoginScreen.screenFileName());
        mainContainer.addScreen(ScreenInfo.SignUpScreen.screenId(), ScreenInfo.SignUpScreen.screenFileName());
        mainContainer.addScreen(ScreenInfo.ForgotPasswordScreen.screenId(), ScreenInfo.ForgotPasswordScreen.screenFileName());
        mainContainer.addScreen(ScreenInfo.ProfileScreen.screenId(), ScreenInfo.ProfileScreen.screenFileName());

        mainContainer.displayScreen(ScreenInfo.LoginScreen.screenId());

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 750, 480);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        launch(args);
    }
}
