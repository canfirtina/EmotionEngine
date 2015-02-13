package SharedSensorData;

/**
 * Created by Mustafa on 13.2.2015.
 */
public class Sample {
    private Label label;
    private FeatureWindow[] sensorData;

    public Sample(Label label, FeatureWindow[] sensorData) {
        this.label = label;
        this.sensorData = sensorData;
    }
}
