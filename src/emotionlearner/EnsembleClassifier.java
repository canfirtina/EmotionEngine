package emotionlearner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import sensormanager.SensorListener;
import sensormanager.SensorListenerEEG;
import shared.Emotion;
import shared.FeatureListController;
import weka.classifiers.*;

/**
 * Uses various classifier and decides based on these weighted decision
 * @author aliyesilyaprak
 */
public class EnsembleClassifier {
	/**
	 * Train data featureListController
	 */
	private FeatureListController trainController;
	
	/**
	 * All classifiers that are used 
	 */
	private ArrayList<Classifier> classifiers;
	
	/**
	 * Classifier weights
	 */
	private double[] weights;
	
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
		if(trainController == null)
			return null;
		return null;
		
	}
	
	/**
	 * Trains the system with feature set
	 * @param featureController 
	 */
	public void train(FeatureListController featureController){
		FeatureListController prevFeatureController = trainController;
		trainController = featureController;
		
		//compute which sensorlisteners are removed and added
		List<SensorListener> newListeners = new ArrayList<>();
		if(prevFeatureController!=null){
			List<SensorListener> prevListeners = prevFeatureController.sensorListeners;
			List<SensorListener> listeners = featureController.sensorListeners;

			//compute new sensors
			for(SensorListener listener : listeners)
				if(!prevListeners.contains(listener))
					newListeners.add(listener);
			
			//remove old sensors' classifiers
			Iterator<Classifier> classifierIt = classifiers.iterator();
			Iterator<SensorListener> listenerIt = prevListeners.iterator();
			while(listenerIt.hasNext()){
				SensorListener listener = listenerIt.next();
				Classifier classifier = classifierIt.next();
				
				if(!listeners.contains(listener)){
					listenerIt.remove();
					classifierIt.remove();
				}
			}
			
		}
		else 
			newListeners = featureController.sensorListeners;
		
		//add new classifiers for new sensors
		for(SensorListener listener : newListeners){
			try {
				String classPath = null;
				String[] options = null;
				
				//eeg sensor listener
				if(listener.getClass() == SensorListenerEEG.class){
					classPath ="weka.classifiers.functions.LibSVM";
					options = new String[]{"-K", "1", "-R", "1.0", "-D", "3"};
				}
				
				Classifier model = Classifier.forName( classPath, options);
				classifiers.add(model);
				
				
			} catch (Exception ex) {
				
			}
		}
		
	}
	
	
}
