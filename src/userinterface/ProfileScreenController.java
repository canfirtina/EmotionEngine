/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import emotionlearner.engine.EmotionEngine;
import emotionlearner.engine.EmotionEngineObserver;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListView.EditEvent;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sensormanager.listener.SensorListener;
import sensormanager.listener.SensorListenerEEG;
import sensormanager.listener.SensorListenerGSR;
import sensormanager.listener.SensorListenerHR;
import shared.Emotion;

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
    @FXML
    private Button connectButton;

    PresentingController presentingController;

    private ArrayList<SensorListener> connectedSensors;
    private ArrayList<SensorListener> pendingSensors;
    private HashMap<String, Class> sensorsOnSerialPorts;
    private ArrayList<String> availableSerialPorts;
    
    private final String NOSENSORLIST = "None";
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<String> sensorNames = FXCollections.observableArrayList(
                new SensorListenerEEG(null).toString(),
                new SensorListenerGSR(null).toString(),
                new SensorListenerHR(null).toString(),
                NOSENSORLIST);

        sensorList.setCellFactory(ComboBoxListCell.forListView(sensorNames));

        ObservableList<String> tutorials = FXCollections.observableArrayList("Boring1", "Happy1");
        tutorialList.setItems(tutorials);

        ObservableList<String> activities = FXCollections.observableArrayList("");
        activityList.setItems(activities);

        sensorList.setEditable(true);
        tutorialList.setEditable(true);

        connectButton.setDisable(true);

        EmotionEngine engine = EmotionEngine.sharedInstance(null);
        engine.registerObserver(this);
        
        tutorialList.setDisable(true);
    }

    @Override
    public void setPresentingScreen(PresentingController presentingController) {

        this.presentingController = presentingController;
    }

    @FXML
    private void refreshButtonPressed(ActionEvent event) {

        sensorsOnSerialPorts = new HashMap<String, Class>();
        
        availableSerialPorts = new ArrayList<String>();
        availableSerialPorts.addAll(Arrays.asList(sensormanager.util.SerialPortUtilities.getConnectedPorts()));

        sensorList.setItems(FXCollections.observableArrayList(availableSerialPorts));

        connectButton.setDisable(true);
    }

    @FXML
    private void connectButtonPressed(ActionEvent event) {

        updateSensorList();
    }

    @FXML
    private void tutorialEditTriggered(EditEvent event) {

        File videoFile = null;
        EmotionEngine engine = EmotionEngine.sharedInstance(null);
        Emotion emotionLabel = null;
        
        if( event.getIndex() == 0) {
            videoFile = new File("videos/boring1.mp4");

            if( !videoFile.exists()){
                System.err.println("no such video file");
                return;
            }
            
            emotionLabel = Emotion.BORED;
        }else if( event.getIndex() == 1){
            
            videoFile = new File("videos/happy1.mp4");

            if( !videoFile.exists()){
                System.err.println("no such video file");
                return;
            }
            
            emotionLabel = Emotion.PEACEFUL;
            
        }
        final Emotion label = emotionLabel;
        
        final MediaPlayer video = new MediaPlayer(new Media(videoFile.toURI().toString()));
        video.setOnReady(new Runnable() {

            @Override
            public void run() {
                
                video.play();
                
                engine.openTrainingSession(label);
            }
        });

        MediaView vidView = new MediaView(video);
        vidView.setFitWidth(750);
        vidView.setFitHeight(480);
        
        Stage stage = new Stage();
        stage.setTitle(tutorialList.getItems().get(event.getIndex()));
        stage.setScene(new Scene(new Group(vidView), vidView.getFitWidth(), vidView.getFitHeight(), Color.BLACK));
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                video.stop();
                engine.closeTrainingSession();
            }
        });
    }

    @FXML
    private void sensorListEditTriggered(EditEvent event) {

        String key = availableSerialPorts.get(event.getIndex());
        String value = " - " + event.getNewValue().toString();

        if (value.endsWith(new SensorListenerEEG(null).toString()))
            sensorsOnSerialPorts.put(key, SensorListenerEEG.class);
        else if (value.endsWith(new SensorListenerGSR(null).toString()))
            sensorsOnSerialPorts.put(key, SensorListenerGSR.class);
        else if (value.endsWith(new SensorListenerHR(null).toString()))
            sensorsOnSerialPorts.put(key, SensorListenerHR.class);
        else if( value.endsWith(NOSENSORLIST)){
            sensorsOnSerialPorts.remove(key);
            value = "";
        }
        
        sensorList.getItems().set(event.getIndex(), key + value);

        if( sensorsOnSerialPorts.size() > 0)
            connectButton.setDisable(false);
        else
            connectButton.setDisable(true);
    }

    private void updateSensorList() {

        EmotionEngine engine = EmotionEngine.sharedInstance(null);

        connectedSensors = engine.getConnectedSensors();
        pendingSensors = engine.getPendingSensors();

        ObservableList<String> sensors = FXCollections.observableArrayList();

        for (SensorListener sensor : connectedSensors)
            sensors.add(sensor.getSerialPort() + "-" + sensor.toString() + "-Connected");

        for (SensorListener sensor : pendingSensors)
            sensors.add(sensor.getSerialPort() + "-" + sensor.toString() + "-Pending");

        sensorList.setItems(sensors);

        for (String key : sensorsOnSerialPorts.keySet()) {
            boolean found = false;
            for (SensorListener l : connectedSensors) {
                if (l.getSerialPort() == key) {
                    found = true;
                    break;
                }
            }
            for (SensorListener l : pendingSensors) {
                if (l.getSerialPort() == key) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                engine.createSensorListener(key, sensorsOnSerialPorts.get(key));
            }
        }
        
        if( connectedSensors.size() > 0)
            tutorialList.setDisable(false);
        else {
            tutorialList.setDisable(true);
            connectButton.setDisable(true);
        }
    }

    @Override
    public void notify(EmotionEngine engine) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                updateSensorList();
            }
        });        
    }
}
