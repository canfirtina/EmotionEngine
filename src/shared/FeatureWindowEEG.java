package shared;

import java.util.ArrayList;

/**
 * EEG Wrapper for FeatureWindow
 */
public class FeatureWindowEEG extends FeatureWindow {
    public FeatureWindowEEG(Label label, ArrayList<FeatureList> instances) {
        super(label,instances);
    }
}
