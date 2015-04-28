package emotionlearner;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
	private EnsembleClassifier emotionClassifier;
	
	/**
	 * Executer service which has a event queue
	 */
	private ExecutorService executorService;
	
	/**
	 * Queue locker for synchronization
	 */
	private final Object executorLocker;
	
	/**
	 * Sensor locker for synchronization
	 */
	private final Object sensorLocker;
	
	/**
	 * Train session locker for synchronization
	 */
	private final Object trainSessionLocker;
        
	/**
	 * registered observers which are to be notified when a sensor connection status is changed
	 */
	private ArrayList<EmotionEngineObserver> engineObservers;
        /**
         * Keep session information
         */
    
	/**
	 * FeatureListController that is used to keep training feature lists
	 */
	private FeatureListController trainingFeatures;
	
	/**
	 * FeatureListController that is used to keep test feature lists
	 */
	private FeatureListController testFeatures;
	
	/**
	 * open training session sets this variable
	 */
	private Emotion sessionEmotion;
	
	/**
	 * open training session instantiates this variable
	 */
	private FeatureListController sessionTrainingFeatures;
	
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
		this.sensorListeners = new ArrayList<>();
		this.pendingSensorListeners = new ArrayList<>();
		this.featureExtractors = new ArrayList<>();
		this.executorService = Executors.newSingleThreadExecutor();
		this.executorLocker = new Object();
		this.sensorLocker = new Object();
		this.trainSessionLocker = new Object();
		this.engineObservers = new ArrayList<>();
		this.trainingFeatures = new FeatureListController();
		
		//20 seconds for now
		this.testFeatures = new FeatureListController(20000);
		
        this.sessionEmotion = null;
	}
	
	/**
	 * Last n milliseconds are used for training as an emotion
	 * @param time
	 * @param emotion
	 * @return 
	 */
	public void trainLastNMilliseconds(final long time,final Emotion emotion){
		synchronized(executorLocker){
			executorService.submit(new Callable<Void>() {

				@Override
				public Void call(){
					synchronized(sensorLocker){
						for(SensorListener listener : testFeatures.getSensorListeners()){
							List<FeatureList> lists = testFeatures.getLastFeatureListsInMilliseconds(listener, time);
							if(lists!=null)
								for(FeatureList list : lists)
									trainingFeatures.addFeatureList(listener, list);
						}
					}
					//training operation is missing 
					
					return null;
				}
			});
		}
	}
	
	/**
	 * train whole feature list set
	 * @param controller 
	 */
	public void trainAll(final FeatureListController controller){
		synchronized(executorLocker){
			executorService.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					synchronized(sensorLocker){
						for(SensorListener listener:sensorListeners){
							List<FeatureList> lists = controller.getLastNFeatureList(listener, -1);
							if(lists!=null)
								for(FeatureList list : lists)
									trainingFeatures.addFeatureList(listener, list);
						}
					}

					//training operation is missing
					
					return null;
				}
			});
		}
	}
	
	
	
	/**
	 * Starts a training session with a label
	 * @param label
	 */
	public boolean openTrainingSession(Emotion emotion){
		synchronized(trainSessionLocker){
			this.sessionEmotion = emotion;
			this.sessionTrainingFeatures = new FeatureListController();
		}
		return true;
	}
	
	/**
	 * Finishes the current training session
	 * @return
	 */
	public void closeTrainingSession(){
		synchronized(trainSessionLocker){
			sessionEmotion = null;
		}
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
	public Emotion currentEmotion(){
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
            
            System.out.println(comPort + " " + sensorType.toString());
		synchronized (executorLocker) {
			executorService.submit(new Callable<Void>() {
				@Override
				public Void call(){
					SensorListener listener = null;
					
					if(sensorType.equals(SensorListenerEEG.class))
						listener = new SensorListenerEEG("COM4");
					
					listener.registerObserver(engine);
					
					synchronized(sensorLocker){
						pendingSensorListeners.add(listener);
					}
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
				@Override
				public Void call(){
					FeatureExtractor extractor = null;
					synchronized(sensorLocker){
						 extractor = featureExtractors.get(sensorListeners.indexOf(sensor));
					}
					//new epoch
					extractor.reset();
					List<TimestampedRawData> rawDataArray = sensor.getSensorData();
					extractor.appendRawData(rawDataArray);
					
					//get list and add it to test feature list controller
					FeatureList list = extractor.getFeatures();
					list.setTimestamp(new Timestamp(new Date().getTime()));
					testFeatures.addFeatureList(sensor, list);
					
					//if session t
					if(sessionTrainingFeatures!=null)
						sessionTrainingFeatures.addFeatureList(sensor, list);
					
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
				@Override
				public Void call(){
					synchronized(sensorLocker){
						//if there is no such sensor, ignore it
						if(!sensorListeners.contains(sensor))
							return null;

						featureExtractors.remove(sensorListeners.indexOf(sensor));
						sensorListeners.remove(sensor);
					}
					
					//unregister sensor listener from both training and test feature list controllers
					trainingFeatures.unregisterSensorListener(sensor);
					testFeatures.unregisterSensorListener(sensor);
					
					if(sessionTrainingFeatures!=null)
						sessionTrainingFeatures.unregisterSensorListener(sensor);
					
					
					
					notifyEngineObservers();
					
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
				@Override
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
					synchronized(sensorLocker){
						pendingSensorListeners.remove(sensor);
						sensorListeners.add(sensor);
					}
					featureExtractors.add(extractor);
					
					//register sensor listener to both training and test feature list controllers
					trainingFeatures.registerSensorListener(sensor);
					testFeatures.registerSensorListener(sensor);
					
					if(sessionTrainingFeatures!=null)
						sessionTrainingFeatures.registerSensorListener(sensor);
					
					sensor.startStreaming();
					
					notifyEngineObservers();
					
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
				@Override
				public Void call(){
					synchronized(sensorLocker){
						//if sensor is not in pending sensors
						if(!pendingSensorListeners.contains(sensor))
							return null;

						//remove sensor
						pendingSensorListeners.remove(sensor);
					}
					notifyEngineObservers();
					                               System.out.println("failed");
					return null;
				}
			});
		}
	}
	
	/**
	 * Register an EmotionEngineObserver to engine
	 * @param observer 
	 */
	public void registerObserver(final EmotionEngineObserver observer){
		synchronized(executorLocker){
			executorService.submit(new Callable<Void>(){
				@Override
				public Void call(){
					engineObservers.add(observer);
					return null;
				}
			});
		}
	}
	
	/**
	 * notify emotion engine's observers
	 */
	private void notifyEngineObservers(){
		for(EmotionEngineObserver o : engineObservers)
			o.notify(engine);
	}
	
	/**
	 * Returns sensors attached that are connected to engine
	 * @return 
	 */
	public ArrayList<SensorListener> getConnectedSensors(){
		synchronized(sensorLocker){
			return sensorListeners;
		}
	}
	
	/**
	 * Returns sensors that are pending
	 * @return 
	 */
	public ArrayList<SensorListener> getPendingSensors(){
		synchronized(sensorLocker){
			return pendingSensorListeners;
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