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
    private String imagePath;
    private String mediaPath;
    private String explanationPath;
    private Emotion emotion;
    
    public TutorialItem(){
        
        this.label = "No Item";
        this.imagePath = "";
        this.mediaPath = "";
        this.explanationPath = "";
        this.emotion = Emotion.BORED;
    }
    
    public TutorialItem( String label, String imagePath, String mediaPath, String explanationPath, Emotion emotion){
        
        this.label = label;
        this.imagePath = imagePath;
        this.mediaPath = mediaPath;
        this.explanationPath = explanationPath;
        this.emotion = emotion;
    }
    
    public String getLabel(){
        
        return label;
    }
    
    public String getImagePath(){
        
        return imagePath;
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
