package emotionlearner;
import java.util.ArrayList;

import persistentdatamanagement.DataManager;
import persistentdatamanagement.DataManagerObserver;
import sensormanager.*;
import shared.*;

/**
 * Backbone of the emotion recognition system. Communicates with sensor listeners and data manager,
 * uses feature extractors and emotion classifiers
 *
 */
public class EmotionEngine implements SensorObserver,SensorFactory, DataManagerObserver {
	/**
	 * Sensor listeners attached to emotion engine
	 */
	private ArrayList<SensorListener> sensorListeners;
	
	/**
	 * Sensor listeners that are pending connection
	 */
	private ArrayList<SensorListener> pendingSensorListeners;
	
	/**
	 * Feature extractors that processes raw data from sensor listeners
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
	 * Constructor
	 */
	public EmotionEngine(){
		this.sensorListeners = new ArrayList<SensorListener>();
		this.pendingSensorListeners = new ArrayList<SensorListener>();
		this.featureExtractors = new ArrayList<FeatureExtractor>();
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
	 * Creates a sensor listener of a type which uses a certain port number
	 * @param portNumber
	 * @param sensorType
	 */
	@Override
	public void createSensorListener(String comPort, 
			Class<SensorListener> sensorType) {
		SensorListener listener = null;
		
		if(sensorType.equals(SensorListenerEEG.class))
			listener = new SensorListenerEEG("COM4");
		
		listener.registerObserver(this);
		if(listener.connect())
			pendingSensorListeners.add(listener);
	}
	
	/**
	 * Called when new data is ready to use of the sensor
	 * @param sensor is the sensor that has new data
	 */
	@Override
	public void dataArrived(SensorListener sensor) {
		FeatureExtractor extractor = featureExtractors.get(sensorListeners.indexOf(sensor));
		
		//new epoch
		extractor.reset();
		TimestampedRawData[] rawDataArray = sensor.getSensorData();
		for(int i=0;i<rawDataArray.length;++i)
			extractor.appendRawData(rawDataArray[i]);
		
		FeatureList list = extractor.getFeatures();
		System.out.println("New Feature List");
		for(int i=0;i<list.size();++i)
			System.out.print(list.get(i));
		System.out.println();
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
		//if sensor is not in pending sensors
		if(!this.pendingSensorListeners.contains(sensor))
			return;
		
		FeatureExtractor extractor = null;
		DataEpocher epocher = null;
		if(sensor.getClass().equals( SensorListenerEEG.class)){
			extractor = new FeatureExtractorEEG();
			epocher = new TimeBasedDataEpocher(4000);
		}
		
		sensor.setDataEpocher(epocher);
		pendingSensorListeners.remove(sensor);
		sensorListeners.add(sensor);
		featureExtractors.add(extractor);
		
		sensor.startStreaming();
	}

	/**
	 * Called when the status of data manager changes
	 * @param manager
	 */
	@Override
	public void notify(DataManager manager) {
		
	}
	
}