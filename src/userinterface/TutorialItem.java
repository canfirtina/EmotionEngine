/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import javafx.scene.image.Image;
import shared.Emotion;

/**
 *
 * @author CanFirtina
 */
public class TutorialItem {
    
    private String label;
    private Image image;
    private String mediaPath;
    private String explanationPath;
    private Emotion emotion;
    
    public TutorialItem(){
        
        this.label = "No Item";
        this.image = null;
        this.mediaPath = "";
        this.explanationPath = "";
        this.emotion = Emotion.BORED;
    }
    
    public TutorialItem( String label, Image image, String mediaPath, String explanationPath, Emotion emotion){
        
        this.label = label;
        this.image = image;
        this.mediaPath = mediaPath;
        this.explanationPath = explanationPath;
        this.emotion = emotion;
    }
    
    public String getLabel(){
        
        return label;
    }
    
    public Image getImagePath(){
        
        return image;
    }
    
    public String getMediaPath(){
        
        return mediaPath;
    }
    
    public String getExplanationPath(){
        
        return explanationPath;
    }
    
    public Emotion getEmotion(){
        
        return emotion;
    }
}
