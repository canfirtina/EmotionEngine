package shared;

import java.util.ArrayList;

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
    private ArrayList<FeatureList> instances;

    public FeatureWindow(Label label, ArrayList<FeatureList> instances) {
        this.label = label;
        this.instances = instances;
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
