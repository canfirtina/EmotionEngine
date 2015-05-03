/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import emotionlearner.engine.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

/**
 *
 * @author CanFirtina
 */
public class EmotionEngineApp extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

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
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {

                EmotionEngine.sharedInstance(null).stopEngine();
                System.exit(0);
            }
        });
        stage.initStyle(StageStyle.TRANSPARENT);

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        
        
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        launch(args);
        EmotionEngine.sharedInstance(null).stopEngine();
    }
}
