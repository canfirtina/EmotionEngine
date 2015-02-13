package SharedSensorData;

import java.util.ArrayList;

/**
 * Created by Mustafa on 13.2.2015.
 */
public class FeatureWindow {
    private Label label;
    private ArrayList<FeatureList> instances;

    public FeatureWindow(Label label, ArrayList<FeatureList> instances) {
        this.label = label;
        this.instances = instances;
    }

    public void addFeatureList(FeatureList newList) {

    }

    public byte[] asByteArray() {
        return new byte[0];
    }

    public void fromByteArray(byte[] bytes) {

    }
}
