package emotionlearner;
import java.util.ArrayList;

import persistentdatamanagement.DataManager;
import persistentdatamanagement.DataManagerObserver;
import SensorManager.*;
import shared.*;

public class EmotionEngine implements SensorObserver,SensorFactory, DataManagerObserver {
	/**
	 * Sensor listeners attached to emotion engine
	 */
	private ArrayList<SensorListener> sensorListeners;
	
	/**
	 * Feature extractors that processes raw data from the sensorListeners
	 */
	private ArrayList<FeatureExtractor> featureExtractors;
	
	/**
	 * Persistency manager used for previously saved emotion data
	 */
	private DataManager persistentDataManager;
	
	/**
	 * Learns and predicts emotions
	 */
	private EmotionClassifier emotionClassifier;
	
	/**
	 * Return a single shared instance of EmotionEngine
	 * @param persistentDataManager
	 * @return
	 */
	public static EmotionEngine sharedInstance(DataManager persistentDataManager){
		return null;
	}
	
	/**
	 * Starts a training session with a label
	 * @param label
	 * @return
	 */
	public boolean openTrainingSession(Label label){
		return true;
	}
	
	/**
	 * Finishes the current training session
	 * @return
	 */
	public boolean closeTrainingSession(){
		return true;
	}
	
	/**
	 * Starts to classify the physiological data session
	 * @return
	 */
	public boolean openClassifySession(){
		return true;
	}
	
	/**
	 * Stops classifying session
	 * @return
	 */
	public boolean stopClassifySession(){
		return true;
	}
	
	/**
	 * Returns last calculated emotional status for the classifying session  
	 * @return
	 */
	public Label currentEmotion(){
		return null;
	}
	
	/**
	 * Creates a sensor listener of a type which uses a certain port number
	 * @param portNumber
	 * @param sensorType
	 */
	@Override
	public void createSensorListener(int portNumber, 
			Class<SensorListener> sensorType) {
	}
	
	/**
	 * Called when new data is ready to use of the sensor
	 * @param sensor is the sensor that has new data
	 */
	@Override
	public void dataArrived(SensorListener sensor) {
		
	}

	/**
	 * Called when a connection error of a sensor occurs
	 * @param sensor is the sensor which has the connection error
	 */
	@Override
	public void connectionError(SensorListener sensor) {
		
	}

	/**
	 * Called when the connection of a sensor is established
	 * @param sensor is the sensor whose connection is established
	 */
	@Override
	public void connectionEstablished(SensorListener sensor) {
		
	}

	/**
	 * Called when the status of data manager changes
	 * @param manager
	 */
	@Override
	public void notify(DataManager manager) {
		
	}
	
}