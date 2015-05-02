package emotionlearner.engine;
import communicator.Communicator;
import emotionlearner.classifier.ClassifierObserver;
import sensormanager.data.TimestampedRawData;
import sensormanager.listener.SensorObserver;
import sensormanager.listener.SensorListener;
import sensormanager.listener.SensorListenerEEG;
import sensormanager.listener.SensorFactory;
import emotionlearner.feature.FeatureExtractor;
import sensormanager.data.DataEpocher;
import emotionlearner.feature.FeatureExtractorEEG;
import emotionlearner.classifier.EnsembleClassifier;
import sensormanager.data.TimeBasedDataEpocher;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import persistentdatamanagement.DataManager;
import persistentdatamanagement.DataManagerObserver;
import shared.*;

/**
 * Backbone of the emotion recognition system. Communicates with sensor listeners and data manager,
 * uses feature extractors and emotion classifiers
 *
 */
public class EmotionEngine implements SensorObserver,SensorFactory, DataManagerObserver, ClassifierObserver{
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
	 * set to true if classify session is opened
	 */
	private boolean isClassifySessionOpen;
	
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
		if(engine==null){
			engine = new EmotionEngine();
			engine.startEngine();
		}
		
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
		this.emotionClassifier = new EnsembleClassifier();
		this.emotionClassifier.registerObserver(this);
		
        this.sessionEmotion = null;
		this.testFeatures = new FeatureListController(50000);
		
	}
	
	/**
	 * starts engine and call related components
	 */
	private void startEngine(){
		persistentDataManager = DataManager.getInstance();		
		Communicator.startServer();
		Communicator.waitClient();
	}
	
	/**
	 * stops engine and sensor streaming
	 */
	public void stopEngine(){
		synchronized(sensorLocker){
			for(SensorListener l : sensorListeners){
				l.stopStreaming();
				l.disconnect();
			}
		}
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
					if(sessionTrainingFeatures != null)
						return null;
					
					FeatureListController lastTrainController = new FeatureListController();
					synchronized(sensorLocker){
						for(SensorListener listener : testFeatures.getSensorListeners())
							lastTrainController.registerSensorListener(listener);
						
						for(SensorListener listener : testFeatures.getSensorListeners()){
							List<FeatureList> lists = testFeatures.getLastFeatureListsInMilliseconds(listener, time);
							if(lists!=null)
								for(FeatureList list : lists){
									list.setEmotion(emotion);
									lastTrainController.addFeatureList(listener, list);
								}
						}
						
						for(SensorListener listener : lastTrainController.getSensorListeners())
							persistentDataManager.saveMultipleSamples(lastTrainController.getLastNFeatureList(listener, -1), listener);

					}
					
					//training operation is missing 
					trainAll(lastTrainController);
					
					
					return null;
				}
			});
		}
	}
	
	/**
	 * gets saved features from data manager and train them
	 */
	private void trainFromDataManager(){
		FeatureListController fc = new FeatureListController();
		for(SensorListener listener : sensorListeners){
			fc.registerSensorListener(listener);
			ArrayList<FeatureList> lists = persistentDataManager.getGameData(listener);
			for(FeatureList list : lists)
				fc.addFeatureList(listener, list);
		}
		
		trainAll(fc);	
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
					for(SensorListener l : controller.getSensorListeners())
						emotionClassifier.trainOfSensor(trainingFeatures, l);
					
					return null;
				}
			});
		}
	}
	
	
	
	/**
	 * Starts a training session with a label
	 * @param emotion
	 */
	public boolean openTrainingSession(final Emotion emotion){
		synchronized (executorLocker) {
			executorService.submit(new Callable<Void>() {
				@Override
				public Void call(){
					if(sessionTrainingFeatures!=null)
						return null;
					
					synchronized(trainSessionLocker){
						sessionEmotion = emotion;
						sessionTrainingFeatures = new FeatureListController();
						for(SensorListener listener : sensorListeners)
							sessionTrainingFeatures.registerSensorListener(listener);
					}
					
					return null;
				}
			});
		}
					
		return true;
	}
	
	/**
	 * Finishes the current training session
	 * @return
	 */
	public void closeTrainingSession(){
		synchronized (executorLocker) {
			executorService.submit(new Callable<Void>() {
				@Override
				public Void call(){
					synchronized(trainSessionLocker){
						for(SensorListener listener : sessionTrainingFeatures.getSensorListeners())
							persistentDataManager.saveMultipleSamples(sessionTrainingFeatures.getLastNFeatureList(listener, -1), listener);

						trainAll(sessionTrainingFeatures);
						sessionTrainingFeatures = null;
						sessionEmotion = null;
					}
					return null;
				}
			});
		}
	}
	
	/**
	 * Starts to classify the physiological data session
	 * @return

	 */
	public void openClassifySession(){
		synchronized (executorLocker) {
			executorService.submit(new Callable<Void>() {
				@Override
				public Void call(){
					if(sessionTrainingFeatures!=null)
						return null;
					isClassifySessionOpen = true;
					
					return null;
				}
			});
		}
	}
	
	/**
	 * Stops classifying session
	 * @return
	 * @deprecated 
	 */
	public void stopClassifySession(){
		synchronized (executorLocker) {
			executorService.submit(new Callable<Void>() {
				@Override
				public Void call(){
					isClassifySessionOpen = false;
					return null;
				}
			});
		}
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
						listener = new SensorListenerEEG(comPort);
					
					listener.registerObserver(engine);
					
					synchronized(sensorLocker){
						pendingSensorListeners.add(listener);
					}
					listener.connect();
                                        notifyEngineObservers();

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
					FeatureExtractor extractor;
					synchronized(sensorLocker){
						 extractor = featureExtractors.get(sensorListeners.indexOf(sensor));
					}
					//new epoch
					extractor.reset();
					List<TimestampedRawData> rawDataArray = sensor.getSensorData();
					extractor.appendRawData(rawDataArray);
					
					synchronized(trainSessionLocker){
						if(sessionTrainingFeatures == null && trainingFeatures == null)
							return null;
						
						//get list and add it to test feature list controller
						FeatureList list = extractor.getFeatures();
						list.setTimestamp(new Timestamp(new Date().getTime()));
						
						if(sessionTrainingFeatures!=null){
							System.out.println("train");
							list.setEmotion(sessionEmotion);
							sessionTrainingFeatures.addFeatureList(sensor, list);
							
						}
						else if(testFeatures!=null){
							System.out.println("classify");
							testFeatures.addFeatureList(sensor, list);
							emotionClassifier.classify(testFeatures);
						}
					}
					
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
					if(testFeatures != null)
						testFeatures.unregisterSensorListener(sensor);
					
					if(sessionTrainingFeatures!=null)
						sessionTrainingFeatures.unregisterSensorListener(sensor);
					
					trainFromDataManager();
                                        
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
					                               System.out.println("connection established");
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
						featureExtractors.add(extractor);
					}
					
					//register sensor listener to both training and test feature list controllers
					trainingFeatures.registerSensorListener(sensor);
					if(testFeatures!=null)
						testFeatures.registerSensorListener(sensor);
					trainFromDataManager();
										
					if(sessionTrainingFeatures!=null)
						sessionTrainingFeatures.registerSensorListener(sensor);
					
					
					notifyEngineObservers();
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
		if(engineObservers == null)
			return;
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

	@Override
	public void classifyNotification(EnsembleClassifier classifier) {
		synchronized(executorLocker){
			executorService.submit(new Callable<Void>(){
				@Override
				public Void call(){
					if(isClassifySessionOpen)
						Communicator.provideEmotionalState(classifier.emotion());
					return null;
				}
			});
		}
	}

	@Override
	public void trainNotification(EnsembleClassifier classifier) {
	}
	
}