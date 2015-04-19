package shared;

import java.util.ArrayList;

/**
 * Keeps features extracted from a series of samples of a single sensor.
 */
public class UserData {

    /**
     * FeatureLists that are included in this window.
     */
    private ArrayList<FeatureList> instances;
    private ArrayList<Emotion> labels;

    public UserData() {
        instances = new ArrayList<FeatureList>();
        labels = new ArrayList<Emotion>();
    }

    public UserData(ArrayList<FeatureList> instances, ArrayList<Emotion> labels) {
        if (instances.size() == labels.size()) {
            this.instances = instances;
            this.labels = labels;
        } else {
            instances = new ArrayList<FeatureList>();
            labels = new ArrayList<Emotion>();
        }

    }

    public UserData(String data) {
        instances = new ArrayList<FeatureList>();
        labels = new ArrayList<Emotion>();

        String[] lines = data.split("\n");

        for (String line : lines) {
            String[] current = line.split(",");
            Emotion label = Emotion.valueOf(current[0]);
            double[] features = new double[current.length - 2];
            for (int i = 1; i < current.length; i++) {
                features[i] = Double.parseDouble(current[i]);
            }

            instances.add(new FeatureList(features));
            labels.add(label);
        }
    }
    
    public void add(FeatureList features, Emotion label){
        instances.add(features);
        labels.add(label);
    }
    
    public ArrayList<FeatureList> getFeatures() {
        return new ArrayList<>(instances);
    }

    public ArrayList<Emotion> getLabels() {
        return new ArrayList<>(labels);
    }

    /**
     * @return String representation of the user data
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < instances.size(); i++) {
            out.append(labels.get(i).toString()).append(",");
            FeatureList list = instances.get(i);

            for (int j = 0; i < list.size() - 1; ++i) {
                out.append(list.get(j)).append(",");
            }
            out.append(list.get(list.size() - 1)).append("\n");
        }
        return out.toString();
    }

}
