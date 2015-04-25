package emotionlearner;

import java.util.ArrayList;
import java.util.List;
import sensormanager.SensorListener;
import shared.Emotion;
import shared.FeatureListController;

/**
 * Uses various classifier and decides based on these weighted decision
 * @author aliyesilyaprak
 */
public class EnsembleClassifier {
	
	private FeatureListController trainController;
	
	/**
	 * Classifier constructor
	 */
	public EnsembleClassifier(){
		
	}
	
	
	
	/**
	 * classifies emotional status
	 * @param featureController
	 * @return 
	 */
	public Emotion classify(FeatureListController featureController){
		return null;
		
	}
	
	/**
	 * Trains the system with feature set
	 * @param featureController 
	 */
	public void train(FeatureListController featureController){
		FeatureListController prevFeatureController = trainController;
		trainController = featureController;
		
		List<SensorListener> prevListeners = prevFeatureController.sensorListeners;
		List<SensorListener> listeners = featureController.sensorListeners;
		
		ArrayList<SensorListener> newSensorListeners = new ArrayList<>();
		//ArrayList<SensorListener> 
		for(SensorListener listener : listeners)
			if(!prevListeners.contains(listener))
				newSensorListeners.add(listener);
		
		
		
	}
	
	
}
