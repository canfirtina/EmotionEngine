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
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import persistentdatamanagement.DataManager;
import sensormanager.listener.SensorListener;
import sensormanager.listener.SensorListenerEEG;
import sensormanager.listener.SensorListenerGSR;
import sensormanager.listener.SensorListenerHR;
import shared.Emotion;
import shared.TutorialInfo;
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
    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private ProgressIndicator tutorialListProgress;
    @FXML
    private Label activityLabel;

    PresentingController presentingController;

    private ArrayList<SensorListener> connectedSensors;
    private ArrayList<SensorListener> pendingSensors;
    private HashMap<String, Class> sensorsOnSerialPorts;
    private ArrayList<String> availableSerialPorts;
    private ArrayList<TutorialItem> tutorialItems;
    private ArrayList<ToggleButton> serialButtons;
    private ExecutorService executorService;

    private final String NOSENSORLIST = "Cancel";
    private final String CONNECT = "Connect";
    private final String DISCONNECT = "Disconnect";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        serialPortsPane.setDisable(true);
        connectButton.setDisable(true);
        tutorialList.setDisable(true);
        sensorsButtonBox.setDisable(true);

        executorService = Executors.newSingleThreadExecutor();
        tutorialListProgress.setVisible(false);

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

        tutorialList.setCellFactory(new Callback<ListView<TutorialItem>, javafx.scene.control.ListCell<TutorialItem>>() {
            @Override
            public ListCell<TutorialItem> call(ListView<TutorialItem> listView) {
                return new TutorialListCell();
            }
        });

        //------------
        //Activity List
        ObservableList<String> activities = FXCollections.observableArrayList("");
        activityList.setItems(activities);
        //------------

        serialButtons = new ArrayList<ToggleButton>();
        serialButtons.add(sensorButton1);
        serialButtons.add(sensorButton2);
        serialButtons.add(sensorButton3);
        serialButtons.add(sensorButton4);
        serialButtons.add(new ToggleButton("No Ports Available"));

        serialButtons.get(serialButtons.size() - 1).setMaxHeight(10000);
        serialButtons.get(serialButtons.size() - 1).setMaxWidth(10000);

        EmotionEngine engine = EmotionEngine.sharedInstance(null);
        engine.registerObserver(this);
    }

    @Override
    public void setPresentingScreen(PresentingController presentingController) {

        this.presentingController = presentingController;
    }

    @FXML
    private void refreshButtonPressed(ActionEvent event) {
        
        sensorsButtonBox.setDisable(true);
        connectButton.setDisable(true);
        serialPortsPane.getChildren().clear();

        ArrayList<SensorListener> connectedSensors = this.connectedSensors;
        if(connectedSensors==null)
            connectedSensors = new ArrayList<>();
        
        if (sensorsOnSerialPorts != null){
            for (String comport : sensorsOnSerialPorts.keySet())
                for (SensorListener connectedSensor : connectedSensors)
                    if (!connectedSensor.getSerialPort().equalsIgnoreCase(comport))
                        sensorsOnSerialPorts.remove(comport);
        }else
            sensorsOnSerialPorts = new HashMap<>();

        availableSerialPorts = new ArrayList<>();
        for(int i = 0;i<connectedSensors.size();++i){
            SensorListener listener  =  connectedSensors.get(i);
            availableSerialPorts.add(listener.getSerialPort());
            serialButtons.get(i).setText(listener.getSerialPort() + " - " + listener.toString());
            serialButtons.get(i).setDisable(false);
        }
        
        List<String> newAvailableSerialPorts = (List<String>)Arrays.asList(sensormanager.util.SerialPortUtilities.getConnectedPorts());
        //TODO newAvailable da zaten bagli olan sensorler donuyor
        
        for(int i=0;i<newAvailableSerialPorts.size();++i){
            serialButtons.get(i+connectedSensors.size()).setText(newAvailableSerialPorts.get(i));
            availableSerialPorts.add(newAvailableSerialPorts.get(i));
            serialButtons.get(i+connectedSensors.size()).setDisable(false);
        }
          
//        for(int i=0;i<sensorsOnSerialPorts.size();++i)
//            serialButtons.get(i).setText();
//            
//        for(int i=0;i<availableSerialPorts.size();++i){
//            SensorListener listener = connectedSensors.get(i);
//            serialButtons.get(i).setText(listener.getSerialPort() + " - " + listener.toString());
//        }
//            
//        for (int i = 0; i < availableSerialPorts.size() + sensorsOnSerialPorts.size(); i++)
//            serialButtons.get(i).setText(connectedSensors.get(iavailableSerialPorts.get(i));
//                    
//            for( String comport : sensorsOnSerialPorts.keySet())
//                if( !comport.equalsIgnoreCase(availableSerialPorts.get(i)))
//                    serialButtons.get(i).setText(availableSerialPorts.get(i));

        if (availableSerialPorts.size() > 0) {

            int columnSpan = availableSerialPorts.size() > 2 ? 1 : 2;
            int rowSpan = availableSerialPorts.size() > 1 ? 1 : 2;

            for (int i = 0; i < availableSerialPorts.size(); i++) {
                serialButtons.get(i).setSelected(false);
                serialPortsPane.add(serialButtons.get(i), i * columnSpan % 2, i * rowSpan % 2, columnSpan, rowSpan);
            }

            serialPortsPane.setDisable(false);
        } else {

            serialPortsPane.add(serialButtons.get(serialButtons.size() - 1), 0, 0, 2, 2);
            serialPortsPane.setDisable(true);
        }
    }

    @FXML
    private void connectButtonPressed(ActionEvent event) {

        ToggleButton selectedToggle = null;

        for (Node toggleButton : serialPortsPane.getChildren()) {
            if (((ToggleButton) toggleButton).isSelected()) {
                selectedToggle = (ToggleButton) toggleButton;
            }
        }

        sensorsButtonBox.setDisable(true);

        if (connectButton.getText().equalsIgnoreCase(CONNECT)) {
            updateSensorList();
        } else {
            SensorListener selectedListener = null;
            for (SensorListener listener : connectedSensors) {
                if (listener.getClass() == sensorsOnSerialPorts.get(availableSerialPorts.get(serialPortsPane.getChildren().indexOf(selectedToggle)))) {
                    selectedListener = listener;
                    break;
                }
            }
            if (selectedListener != null) {
                EmotionEngine.sharedInstance(null).disconnect(selectedListener);
                connectButton.setText(CONNECT);
                connectButton.setDisable(true);

                sensorsOnSerialPorts.remove(availableSerialPorts.get(serialPortsPane.getChildren().indexOf(selectedToggle)));
                selectedToggle.setText(availableSerialPorts.get(serialPortsPane.getChildren().indexOf(selectedToggle)));
            }

            if (sensorsOnSerialPorts.size() > 0) {
                connectButton.setDisable(false);
            } else {
                connectButton.setDisable(true);
            }
        }

        for (Node toggleButton : serialPortsPane.getChildren()) {
            ((ToggleButton) toggleButton).setSelected(false);
        }

    }

    @FXML
    private void serialPortSelected(ActionEvent event) {

        if (serialPortsPane.getChildren().contains(event.getSource())) {

            ToggleButton selectedToggle = (ToggleButton) event.getSource();
            if (selectedToggle.isSelected()) {

                for (Node child : serialPortsPane.getChildren()) {
                    ((ToggleButton) child).setSelected(false);
                }

                selectedToggle.setSelected(true);
                sensorsButtonBox.setDisable(false);

                String comPort = availableSerialPorts.get(serialPortsPane.getChildren().indexOf(selectedToggle));

                connectButton.setText(CONNECT);
                if (connectedSensors != null) {
                    for (SensorListener sensor : connectedSensors) {
                        if (sensor.getSerialPort().equalsIgnoreCase(comPort)) {
                            sensorsButtonBox.setDisable(true);
                            connectButton.setText(DISCONNECT);
                            connectButton.setDisable(false);
                        }
                    }
                }
            } else {
                sensorsButtonBox.setDisable(true);
                connectButton.setText(CONNECT);
            }
        }
    }

    @FXML
    private void sensorTypeSelected(ActionEvent event) {

        ToggleButton selectedPort = null;

        for (Node child : serialPortsPane.getChildren()) {
            if (((ToggleButton) child).isSelected()) {
                selectedPort = (ToggleButton) child;
            }
        }

        Button selectedSensor = (Button) event.getSource();
        String comPort = availableSerialPorts.get(serialPortsPane.getChildren().indexOf(selectedPort));

        if (selectedSensor.getText().equalsIgnoreCase(new SensorListenerEEG(null).toString())) {

            sensorsOnSerialPorts.put(comPort, SensorListenerEEG.class);
            selectedPort.setText(comPort + " - " + selectedSensor.getText());
        } else if (selectedSensor.getText().equalsIgnoreCase(new SensorListenerGSR(null).toString())) {

            sensorsOnSerialPorts.put(comPort, SensorListenerGSR.class);
            selectedPort.setText(comPort + " - " + selectedSensor.getText());
        } else if (selectedSensor.getText().equalsIgnoreCase(new SensorListenerHR(null).toString())) {

            sensorsOnSerialPorts.put(comPort, SensorListenerHR.class);
            selectedPort.setText(comPort + " - " + selectedSensor.getText());
        } else {
            sensorsOnSerialPorts.remove(comPort);
            selectedPort.setText(comPort);
        }

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

        for (SensorListener sensor : connectedSensors) {
            for (int i = 0; i < availableSerialPorts.size(); i++) {
                if (availableSerialPorts.get(i).equalsIgnoreCase(sensor.getSerialPort())) {
                    serialPortsPane.getChildren().get(i).setDisable(false);
                }
            }
        }

        for (SensorListener sensor : pendingSensors) {
            for (int i = 0; i < availableSerialPorts.size(); i++) {
                if (availableSerialPorts.get(i).equalsIgnoreCase(sensor.getSerialPort())) {
                    serialPortsPane.getChildren().get(i).setDisable(true);
                }
            }
        }

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

    @FXML
    private void logoutPressed(ActionEvent event) {

        presentingController.displayScreen(ScreenInfo.LoginScreen.screenId());
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

    @Override
    public void notifyError(EmotionEngine engine, SensorListener sensor) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < availableSerialPorts.size(); i++) {
                    if (availableSerialPorts.get(i).equalsIgnoreCase(sensor.getSerialPort())) {
                        serialPortsPane.getChildren().get(i).setDisable(true);
                    }
                }
                updateSensorList();
            }
        });

    }

    @Override
    public void willPresented() {

        //tutorial list
        tutorialList.setDisable(true);
        tutorialListProgress.setVisible(true);

        //Connect button
        connectButton.setDisable(true);
        //-------------

        //Serial Ports List
        serialPortsPane.getChildren().clear();

        serialPortsPane.add(serialButtons.get(serialButtons.size() - 1), 0, 0, 2, 2);
        serialPortsPane.setDisable(true);
        //----------------

        //sensor buttons
        openBCIButton.setText(new SensorListenerEEG(null).toString());
        gsrButton.setText(new SensorListenerGSR(null).toString());
        hrButton.setText(new SensorListenerHR(null).toString());
        sensorsButtonBox.setDisable(true);
        //--------------

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                //User Panel
                String userName = UserManager.getInstance().getCurrentUser().getName();
                File profilePic = new File("User Data/" + userName + "/profile.png");
                ArrayList<TutorialInfo> tutorialInfoDb = DataManager.getInstance().getAllTutorials();
                tutorialItems = new ArrayList<TutorialItem>();
                for (TutorialInfo info : tutorialInfoDb) {
                    tutorialItems.add(new TutorialItem(info.getName(), info.getImagePath(), info.getLink(), info.getDescription(), Emotion.emotionForValue(info.getEmotion())));
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        userEMail.setText(userName);
                        profileImage.setImage(new Image(profilePic.toURI().toString()));
                        //---------

                        ObservableList<TutorialItem> tutorials = FXCollections.observableArrayList(tutorialItems);

                        tutorialList.setItems(tutorials);

                        tutorialListProgress.setVisible(false);
                    }
                });
            }
        });
    }

    @Override
    public void emotionUpdated(EmotionEngine engine) {
        final Emotion emotion = engine.currentEmotion();
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                activityLabel.setText("Current Emotion: " + emotion.name());
            }
        });
    }
}
