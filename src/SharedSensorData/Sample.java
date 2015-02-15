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

    /**
     * adds a window from sensor to this sample
     * @param featureWindow
     */
    public void addFeatureWindow(FeatureWindow featureWindow) {

    }

}
