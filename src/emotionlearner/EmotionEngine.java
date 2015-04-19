package emotionlearner;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	 * Executer service which has a event queue
	 */
	private ExecutorService executorService;
	
	/**
	 * Queue locker for synchronization
	 */
	private Object executorLocker;
        
        /**
         * Keep session information
         */
        private Emotion sessionLabel;
	
	/**
	 * Singleton instance
	 */
	private static EmotionEngine engine = null;
	
	/**
	 * Return a single shared instance of EmotionEngine
	 * @param persistentDataManager
	 * @return
	 */
	public static EmotionEngine sharedInstance(DataManager persistentDataManager){
		if(engine==null)
			engine = new EmotionEngine();
		
		return engine;
	}
	
	/**
	 * Constructor
	 */
	private EmotionEngine(){
		this.sensorListeners = new ArrayList<SensorListener>();
		this.pendingSensorListeners = new ArrayList<SensorListener>();
		this.featureExtractors = new ArrayList<FeatureExtractor>();
		this.executorService = Executors.newSingleThreadExecutor();
		this.executorLocker = new Object();
                this.sessionLabel = null;
	}
	
	/**
	 * Starts a training session with a label
	 * @param label
	 */
	public boolean openTrainingSession(Emotion emotion){
		return true;
	}
	
	/**
	 * Finishes the current training session
	 * @return
	 */
	public void closeTrainingSession(){
		sessionLabel = null;
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
	 * @param comPort
	 * @param sensorType
	 */
	@Override
	public void createSensorListener(String comPort, 
			final Class sensorType) {
		synchronized (executorLocker) {
			executorService.submit(new Callable<Void>() {
				public Void call(){
					SensorListener listener = null;
					
					if(sensorType.equals(SensorListenerEEG.class))
						listener = new SensorListenerEEG("COM4");
					
					listener.registerObserver(engine);
					pendingSensorListeners.add(listener);
					listener.connect();

					return null;
				}
			});
		}
	}
	
	/**
	 * Called when new data is ready to use of the sensor
	 * @param sensor is the sensor that has new data
	 */
	@Override
	public void dataArrived(final SensorListener sensor) {
		synchronized (executorLocker) {
			executorService.submit(new Callable<Void>() {
				public Void call(){
					FeatureExtractor extractor = featureExtractors.get(sensorListeners.indexOf(sensor));
					
					//new epoch
					extractor.reset();
					List<TimestampedRawData> rawDataArray = sensor.getSensorData();

					extractor.appendRawData(rawDataArray);
					
					FeatureList list = extractor.getFeatures();
					
					System.out.println("New Feature List");
					for(int i=0;i<list.size();++i)
						System.out.print(list.get(i));
					System.out.println();
                                        
                                        // For testing
                                        //DataManager.getInstance().saveSample(list, sensor.getSensorType(), sessionLabel);
                                        
					return null;
				}
			});
		}
	}

	/**
	 * Called when a connection error of a sensor occurs
	 * @param sensor is the sensor which has the connection error
	 */
	@Override
	public void connectionError(final SensorListener sensor) {
		synchronized(executorLocker){
			executorService.submit(new Callable<Void>(){
				public Void call(){
					//if there is no such sensor, ignore it
					if(!sensorListeners.contains(sensor))
						return null;
					
					featureExtractors.remove(sensorListeners.indexOf(sensor));
					sensorListeners.remove(sensor);
					
					
					return null;
				}
			});
		}
	}

	/**
	 * Called when the connection of a sensor is established
	 * @param sensor is the sensor whose connection is established
	 */
	@Override
	public void connectionEstablished(final SensorListener sensor) {
		synchronized (executorLocker) {
			executorService.submit(new Callable<Void>() {
				public Void call(){
					//if sensor is not in pending sensors
					if(!pendingSensorListeners.contains(sensor))
						return null;
					
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
					return null;
				}
			});
		}
	}

	/**
	 * Called when the pending connection is failed
	 * @param sensor is the sensor whose connection is failed
	 */
	@Override
	public void connectionFailed(final SensorListener sensor) {
		synchronized (executorLocker) {
			executorService.submit(new Callable<Void>() {
				public Void call(){
					//if sensor is not in pending sensors
					if(!pendingSensorListeners.contains(sensor))
						return null;
					
					//remove sensor
					pendingSensorListeners.remove(sensor);
					
					return null;
				}
			});
		}
	}
	
	/**
	 * Called when the status of data manager changes
	 * @param manager
	 */
	@Override
	public void notify(DataManager manager) {
		
	}

	
	
}