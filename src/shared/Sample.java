package shared;

import java.io.Serializable;

/**
 * Keeps FeatureWindows of multiple sensors together.
 */
public class Sample implements Serializable {
    private Label label;
    private FeatureWindow[] sensorData;

    public Sample(Label label, FeatureWindow[] sensorData) {
        this.label = label;
        this.sensorData = sensorData;
    }

    /**
     * Adds a window from sensor to this sample.
     * @param featureWindow
     */
    public void addFeatureWindow(FeatureWindow featureWindow) {

    }

}
