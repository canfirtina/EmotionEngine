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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ListView.EditEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import sensormanager.listener.SensorListener;
import sensormanager.listener.SensorListenerEEG;
import sensormanager.listener.SensorListenerGSR;
import sensormanager.listener.SensorListenerHR;
import shared.Emotion;
import user.manager.UserManager;

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
    private ListView<TutorialItem> tutorialList;
    @FXML
    private ListView<String> activityList;
    @FXML
    private Button refreshButton;
    @FXML
    private Button connectButton;
    @FXML
    private VBox sensorsButtonBox;
    @FXML
    private GridPane serialPortsPane;
    @FXML
    private ToggleButton sensorButton1;
    @FXML
    private ToggleButton sensorButton2;
    @FXML
    private ToggleButton sensorButton3;
    @FXML
    private ToggleButton sensorButton4;
    @FXML
    private Button openBCIButton;
    @FXML
    private Button gsrButton;
    @FXML
    private Button hrButton;

    PresentingController presentingController;

    private ArrayList<SensorListener> connectedSensors;
    private ArrayList<SensorListener> pendingSensors;
    private HashMap<String, Class> sensorsOnSerialPorts;
    private ArrayList<String> availableSerialPorts;
    private ArrayList<TutorialItem> tutorialItems;
    private ArrayList<ToggleButton> serialSerialButtons;

    private final String NOSENSORLIST = "Cancel";
    private final String CONNECT = "Connect";
    private final String DISCONNECT = "Disconnect";
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Tutorial List
        //TODO by hand for now
        tutorialItems = new ArrayList<TutorialItem>();
        tutorialItems.add(new TutorialItem("Baby plays with the dog", "tutorials/happy1/happy1.jpg", "tutorials/happy1/happy1.mp4", "tutorials/happy1/explanation.txt", Emotion.JOY));
        tutorialItems.add(new TutorialItem("Watching the water", "tutorials/boring1/boring1.jpg", "tutorials/boring1/boring1.mp4", "tutorials/boring1/explanation.txt", Emotion.BORED));
        ObservableList<TutorialItem> tutorials = FXCollections.observableArrayList(tutorialItems);

        tutorialList.setItems(tutorials);
        tutorialList.setCellFactory(new Callback<ListView<TutorialItem>, javafx.scene.control.ListCell<TutorialItem>>() {
            @Override
            public ListCell<TutorialItem> call(ListView<TutorialItem> listView) {
                return new TutorialListCell();
            }
        });

        tutorialList.setDisable(true);
        //------------

        //Activity List
        ObservableList<String> activities = FXCollections.observableArrayList("");
        activityList.setItems(activities);
        //------------

        //Connect button
        connectButton.setDisable(true);
        //-------------

        //User Panel
        userEMail.setText(UserManager.getInstance().getCurrentUser().getName());
        File profilePic = new File("icon-user-default.png");
        profileImage.setImage(new Image(profilePic.toURI().toString()));
        //---------

        //Serial Ports List
        //serialPortsPane.getChildren().clear();

        serialSerialButtons = new ArrayList<ToggleButton>();
        serialSerialButtons.add(sensorButton1);
        serialSerialButtons.add(sensorButton2);
        serialSerialButtons.add(sensorButton3);
        serialSerialButtons.add(sensorButton4);
        serialSerialButtons.add(new ToggleButton("No Ports Available"));

        serialSerialButtons.get(serialSerialButtons.size() - 1).setMaxHeight(10000);
        serialSerialButtons.get(serialSerialButtons.size() - 1).setMaxWidth(10000);

        //serialPortsPane.add(serialSerialButtons.get(serialSerialButtons.size() - 1), 0, 0, 2, 2);
        //serialPortsPane.setDisable(true);
        //----------------

        //sensor buttons
        openBCIButton.setText(new SensorListenerEEG(null).toString());
        gsrButton.setText(new SensorListenerGSR(null).toString());
        hrButton.setText(new SensorListenerHR(null).toString());
        sensorsButtonBox.setDisable(true);
        //--------------
        
        EmotionEngine engine = EmotionEngine.sharedInstance(null);
        engine.registerObserver(this);
    }

    @Override
    public void setPresentingScreen(PresentingController presentingController) {

        this.presentingController = presentingController;
    }

    @FXML
    private void refreshButtonPressed(ActionEvent event) {

        sensorsOnSerialPorts = new HashMap<String, Class>();
        serialPortsPane.getChildren().clear();

        availableSerialPorts = new ArrayList<String>();
        availableSerialPorts.addAll(Arrays.asList(sensormanager.util.SerialPortUtilities.getConnectedPorts()));

        for (int i = 0; i < availableSerialPorts.size(); i++) {
            serialSerialButtons.get(i).setText(availableSerialPorts.get(i));
        }

        if (availableSerialPorts.size() > 0) {

            int columnSpan = availableSerialPorts.size() > 2 ? 1 : 2;
            int rowSpan = availableSerialPorts.size() > 1 ? 1 : 2;

            for (int i = 0; i < availableSerialPorts.size(); i++) {
                serialPortsPane.add(serialSerialButtons.get(i), i * columnSpan % 2, i * rowSpan % 2, columnSpan, rowSpan);
            }

            serialPortsPane.setDisable(false);
        } else {

            serialPortsPane.add(serialSerialButtons.get(serialSerialButtons.size() - 1), 0, 0, 2, 2);
            serialPortsPane.setDisable(true);
        }
    }

    @FXML
    private void connectButtonPressed(ActionEvent event) {

        if( connectButton.getText().equalsIgnoreCase(CONNECT))
            updateSensorList();
        //else
            //TODO disconnect selected port
    }

    @FXML
    private void tutorialEditTriggered(EditEvent event) {

        TutorialItem tutorialItem = tutorialList.getItems().get(event.getIndex());

        File videoFile = new File(tutorialItem.getMediaPath());

        if (!videoFile.exists()) {
            System.err.println("no such video file");
            return;
        }

        final MediaPlayer video = new MediaPlayer(new Media(videoFile.toURI().toString()));

        EmotionEngine engine = EmotionEngine.sharedInstance(null);
        final Emotion label = tutorialItem.getEmotion();

        MediaView vidView = new MediaView(video);
        vidView.setFitWidth(750);
        vidView.setFitHeight(480);

        Stage stage = new Stage();
        stage.setTitle(tutorialItem.getLabel());
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

        video.setOnReady(new Runnable() {

            @Override
            public void run() {

                video.play();
            }
        });

        video.setOnPlaying(new Runnable() {

            @Override
            public void run() {

                engine.openTrainingSession(label);
            }
        });

        video.setOnPaused(new Runnable() {

            @Override
            public void run() {

                engine.closeTrainingSession();
            }
        });

        video.setOnEndOfMedia(new Runnable() {

            @Override
            public void run() {

                engine.closeTrainingSession();
                stage.close();
            }
        });
    }

    @FXML
    private void serialPortSelected(ActionEvent event) {
        
        if( serialPortsPane.getChildren().contains(event.getSource())){
            
            ToggleButton selectedToggle = (ToggleButton)event.getSource();
            if( selectedToggle.isSelected()){
                
                for( Node child : serialPortsPane.getChildren())
                    ((ToggleButton)child).setSelected(false);
                
                selectedToggle.setSelected(true);
                sensorsButtonBox.setDisable(false);
                
                String comPort = availableSerialPorts.get(serialPortsPane.getChildren().indexOf(selectedToggle));
                
                connectButton.setText(CONNECT);
                for( SensorListener sensor : connectedSensors)
                    if( sensor.getSerialPort().equalsIgnoreCase(comPort))
                        connectButton.setText(DISCONNECT);
            }else{
                sensorsButtonBox.setDisable(true);
                connectButton.setText(CONNECT);
            }
        }
    }
    
    @FXML
    private void sensorTypeSelected( ActionEvent event){
        
        ToggleButton selectedPort = null;
        
        for( Node child : serialPortsPane.getChildren())
            if( ((ToggleButton)child).isSelected())
                selectedPort = (ToggleButton)child;
        
        Button selectedSensor = (Button)event.getSource();
        String comPort = availableSerialPorts.get(serialPortsPane.getChildren().indexOf(selectedPort));
        
        if( selectedSensor.getText().equalsIgnoreCase(new SensorListenerEEG(null).toString())){
            
            sensorsOnSerialPorts.put(comPort, SensorListenerEEG.class);
            selectedPort.setText(comPort + " - " + selectedSensor.getText());
        }else if( selectedSensor.getText().equalsIgnoreCase(new SensorListenerGSR(null).toString())){
            
            sensorsOnSerialPorts.put(comPort, SensorListenerGSR.class);
            selectedPort.setText(comPort + " - " + selectedSensor.getText());
        }else if( selectedSensor.getText().equalsIgnoreCase(new SensorListenerHR(null).toString())){
            
            sensorsOnSerialPorts.put(comPort, SensorListenerHR.class);
            selectedPort.setText(comPort + " - " + selectedSensor.getText());
        }else{
            sensorsOnSerialPorts.remove(comPort);
            selectedPort.setText(comPort);
        }
        
        selectedPort.setSelected(false);
        
        if (sensorsOnSerialPorts.size() > 0) {
            connectButton.setDisable(false);
        } else {
            connectButton.setDisable(true);
        }
    }

    private void updateSensorList() {

        EmotionEngine engine = EmotionEngine.sharedInstance(null);

        connectedSensors = engine.getConnectedSensors();
        pendingSensors = engine.getPendingSensors();
        
        for( SensorListener sensor : connectedSensors)
            for( int i = 0; i < availableSerialPorts.size(); i++)
                if( availableSerialPorts.get(i).equalsIgnoreCase(sensor.getSerialPort()))
                    serialPortsPane.getChildren().get(i).setDisable(false);
        
        for( SensorListener sensor : pendingSensors)
            for( int i = 0; i < availableSerialPorts.size(); i++)
                if( availableSerialPorts.get(i).equalsIgnoreCase(sensor.getSerialPort()))
                    serialPortsPane.getChildren().get(i).setDisable(true);

        //sensorList.setItems(sensors);
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

        if (connectedSensors.size() > 0) {
            tutorialList.setDisable(false);
        } else {
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
