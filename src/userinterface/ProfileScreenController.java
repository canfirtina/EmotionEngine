/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import emotionlearner.EmotionEngine;
import emotionlearner.EmotionEngineObserver;
import java.util.ArrayList;
import sensormanager.*;

/**
 * FXML Controller class
 *
 * @author CanFirtina
 */
public class ProfileScreenController implements Initializable, PresentedScreen, EmotionEngineObserver {

    @FXML
    private ImageView profileImage;
    @FXML
    private Label userEMail;
    @FXML
    private ListView<String> sensorList;
    @FXML
    private ListView<String> tutorialList;
    @FXML
    private ListView<String> activityList;
    @FXML
    private Button refreshButton;
    
    PresentingController presentingController;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> names = FXCollections.observableArrayList("");
        sensorList.setItems(names);
        tutorialList.setItems(names);
        activityList.setItems(names);
        
        EmotionEngine engine = EmotionEngine.sharedInstance(null);
        engine.registerObserver(this);
    }    

    @Override
    public void setPresentingScreen(PresentingController presentingController) {
        
        this.presentingController = presentingController;
    }
    
    @FXML
    private void refreshButtonPressed( ActionEvent event){
        
        updateSensorList();
    }
    
    private void updateSensorList(){
        
        HashMap<String, Class> availablePorts = sensormanager.COMPortListener.getConnectedPorts();
        
        EmotionEngine engine = EmotionEngine.sharedInstance(null);
        
        ArrayList<SensorListener> connectedSensors = engine.getConnectedSensors();
        ArrayList<SensorListener> pendingSensors = engine.getPendingSensors();
        
        ObservableList<String> sensors = FXCollections.observableArrayList();
        
        for( SensorListener sensor : connectedSensors)
            sensors.add(sensor.toString() + "-Connected");
        
        for( SensorListener sensor : pendingSensors)
            sensors.add(sensor.toString() + "-Pending");
        
        sensorList.setItems(sensors);
        
        for(String key : availablePorts.keySet()){
            Class c = availablePorts.get(key);
            boolean found = false;
            for(SensorListener l : connectedSensors)
                if(l.getClass() == c){
                    found = true;
                    break;
                }
            for(SensorListener l : pendingSensors)
                if(l.getClass() == c){
                    found = true;
                    break;
                }
            if( !found)
                engine.createSensorListener(key, c);
        }
    }
    
    @Override
    public void notify(EmotionEngine engine) {
        
        System.out.println("notified");
        updateSensorList();
    }
}
