/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

/**
 *
 * @author ayhun
 */
public class TutorialInfo {
    private String name;
    private String description;
    private String link;
    private String imagePath;
    private int emotion;
    
    public TutorialInfo(String name,String description, String link, String imagePath, int emotion){
        this.name=name;
        this.description=description;
        this.link=link;
        this.imagePath=imagePath;
        this.emotion=emotion;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getEmotion() {
        return emotion;
    }
    
}
