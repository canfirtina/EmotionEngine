package shared;

import java.util.ArrayList;

/**
 * Created by Mustafa on 13.2.2015.
 */
public class FeatureWindow {
    /**
     * Label of this window for training
     */
    private Label label;
    /**
     * FeatureLists that are included in this window
     */
    private ArrayList<FeatureList> instances;

    public FeatureWindow(Label label, ArrayList<FeatureList> instances) {
        this.label = label;
        this.instances = instances;
    }

    /**
     * adds a new feature list to window
     * @param newList
     */
    public void addFeatureList(FeatureList newList) {

    }

    /**
     * converts this window into binary representation
     * @return
     */
    public byte[] asByteArray() {
        return new byte[0];
    }

    /**
     *  a method to convert binary data to meaningful class variables
     * @param bytes
     */
    public void fromByteArray(byte[] bytes) {

    }
}
