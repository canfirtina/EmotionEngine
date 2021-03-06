package userinterface;

import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Catches the user interactions made to EmotionEngineUI View. User's emotion engine actions are controlled by this controller
 *
 */
public class EmotionEngineController extends StackPane implements PresentingController{

    private HashMap<String, Node> screenCollection = new HashMap<>();
    private HashMap<String, PresentedScreen> controllers = new HashMap<>();
    private Stage stage;
    /**
     * Create EmotionEngineController
     */
    public EmotionEngineController(){

        super();
    }

    /**
     * Returns the screen (value) with the given name (key)
     * @param name is the key to find the corresponding screen (value)
     * @return screen (value)
     */
    public Node getScreen( String name) {
        return screenCollection.get( name);
    }

    @Override
    public boolean displayScreen(final String screenName) {

        if ( screenCollection.get( screenName) != null) {   //screen loaded
            final DoubleProperty opacity = opacityProperty();

            if ( !getChildren().isEmpty()) {    //if there is more than one screen
                Timeline fade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new Duration(1000), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        getChildren().remove(0); //remove the displayed screen
                        getChildren().add(0, screenCollection.get(screenName)); //add the screen
                        Timeline fadeIn = new Timeline(
                                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                new KeyFrame(new Duration(800), new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent t) {
                                        PresentedScreen screen = controllers.get(screenName);
                                        screen.willPresented();
                                    }
                                },new KeyValue(opacity, 1.0)));
                        fadeIn.play();
                    }
                }, new KeyValue(opacity, 0.0)));
                fade.play();

            } else {
                setOpacity(0.0);
                getChildren().add(screenCollection.get(screenName)); //no one else been displayed, then just show
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(2500), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
            System.out.println("screen hasn't been loaded!!!\n");
            return false;
        }
    }

    @Override
    public boolean addScreen( String screenName, String resource) {
        try {
            FXMLLoader screenLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadedScreen = (Parent)screenLoader.load();
            PresentedScreen presentedScreenController = ((PresentedScreen)screenLoader.getController());
            presentedScreenController.setPresentingScreen(this);
            screenCollection.put(screenName, loadedScreen);
            controllers.put(screenName, presentedScreenController);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeScreen( String screenName) {

        if( screenCollection.remove( screenName) == null) {
            System.err.println("Screen with name " + screenName + " does not exist. Cannot remove it");
            controllers.remove(screenName);
            return false;
        }

        return true;
    }

    @Override
    public Stage getStage() {

        return stage;
    }

    public void setStage( Stage stage){

        this.stage = stage;
    }
}
