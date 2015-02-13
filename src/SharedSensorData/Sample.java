package SharedSensorData;

import java.io.Serializable;

/**
 * Created by Mustafa on 13.2.2015.
 */
public class Sample implements Serializable {
    private Label label;
    private FeatureWindow[] sensorData;

    public Sample(Label label, FeatureWindow[] sensorData) {
        this.label = label;
        this.sensorData = sensorData;
    }

    public void addFeatureWindow(FeatureWindow featureWindow) {

    }

}
