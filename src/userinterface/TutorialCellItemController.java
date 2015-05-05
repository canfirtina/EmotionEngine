/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import emotionlearner.engine.EmotionEngine;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import shared.Emotion;

/**
 * FXML Controller class
 *
 * @author CanFirtina
 */
public class TutorialCellItemController implements Initializable {

    @FXML
    private HBox mainHBox;
    @FXML
    private ImageView imageView;
    @FXML
    private Hyperlink hyperLink;
    @FXML
    private TextFlow textFlow;

    private TutorialItem item;

    public TutorialCellItemController() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TutorialCellItem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the controller class.
     */

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setInfo(TutorialItem info) {

        hyperLink.setText(info.getLabel());
        hyperLink.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                tutorialEditTriggered();
            }
        });

        textFlow.getChildren().add(new Text(info.getExplanationPath()));
        imageView.setImage(new Image(info.getImagePath()));
        imageView.setFitWidth(100);
        item = info;
    }

    @FXML
    private void tutorialEditTriggered() {

        final MediaPlayer video = new MediaPlayer(new Media(item.getMediaPath()));

            EmotionEngine engine = EmotionEngine.sharedInstance(null);

            final Emotion label = item.getEmotion();

            MediaView vidView = new MediaView(video);
            vidView.setFitWidth(1024);
            vidView.setFitHeight(768);

            Stage stage = new Stage();
            stage.setTitle(item.getLabel());
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

    public HBox getCellData() {

        return mainHBox;
    }
}
