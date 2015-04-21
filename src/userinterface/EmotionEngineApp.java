/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author CanFirtina
 */
public class EmotionEngineApp extends Application {
    
    public static String screen1ID = "login";
    public static String screen1File = "LoginScreen.fxml";
    public static String screen2ID = "profile";
    public static String screen2File = "ProfileScreen.fxml";
    
    @Override
    public void start(Stage stage) throws Exception {
        
        EmotionEngineController mainContainer = new EmotionEngineController();
        mainContainer.addScreen(EmotionEngineApp.screen1ID, EmotionEngineApp.screen1File);
        mainContainer.addScreen(EmotionEngineApp.screen2ID, EmotionEngineApp.screen2File);
        
        mainContainer.displayScreen(EmotionEngineApp.screen1ID);
        
        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, Color.TRANSPARENT);
        stage.setScene(scene);
        //stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        
//        Rectangle r = new Rectangle(5, 5, stage.getWidth() - 10, stage.getHeight() - 10);
//        r.setFill(Color.STEELBLUE);
//        r.setEffect(new DropShadow());
//        root.getChildren().add(0,r);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
