package shared;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps features extracted from a series of samples of a single sensor.
 */
public class FeatureWindow {
    /**
     * Label of this window for training.
     */
    private Label label;
    
    /**
     * FeatureLists that are included in this window.
     */
    private List<FeatureList> instances;

    /**
     * Constructor with predefined featurelists
     * @param instances
     */
    public FeatureWindow(Label label, List<FeatureList> instances) {
        this.instances = instances;
    }
    
    /**
     * Constructor with an empty feature list container
     */
    public FeatureWindow(){
    	this.instances = new LinkedList<FeatureList>();
    }
    
    /**
     * Set emotion to all instances in set
     * @param emotion
     */
    public void setEmotionToAllInstances(Emotion emotion){
    	for(FeatureList list : instances)
    		list.setEmotion(emotion);
    }

    public void setEmotionToLastNInstances(int n){
    	
    }
    
    /**
     * Adds a new feature list to window.
     * @param newList
     */
    public void addFeatureList(FeatureList newList) {

    }

    /**
     * Converts this window into binary representation.
     * @return
     */
    public byte[] asByteArray() {
        return new byte[0];
    }

    /**
     * Convert binary data to meaningful class variables.
     * @param bytes
     */
    public void fromByteArray(byte[] bytes) {

    }
}
