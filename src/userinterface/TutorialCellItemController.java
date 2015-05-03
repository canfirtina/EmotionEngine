/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import com.sun.javafx.scene.control.skin.FXVK;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import java.text.*;
import javafx.scene.text.Text;

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
    
    
    public TutorialCellItemController(){
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TutorialCellItem.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    /**
     * Initializes the controller class.
     */
    
    static String readFile(String path, Charset encoding) throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void setInfo( TutorialItem info){
         
        hyperLink.setText( info.getLabel());
        File imageFile = new File( info.getImagePath());
        try {
            
            textFlow.getChildren().add(new Text(readFile(info.getExplanationPath(), Charset.defaultCharset())));
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (IOException ex) {
            Logger.getLogger(TutorialCellItemController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public HBox getCellData(){
        
        return mainHBox;
    }
}
