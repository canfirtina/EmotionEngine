package emotionlearner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import sensormanager.SensorListener;
import sensormanager.SensorListenerEEG;
import shared.Emotion;
import shared.FeatureList;
import shared.FeatureListController;
import weka.classifiers.*;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

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
	private final Map<SensorListener, Classifier> classifiers;
	
	/**
	 * All datasets of sensors
	 */
	private final Map<SensorListener, Instances>dataSets;
	
	/**
	 * Single thread executor service
	 */
	private final ExecutorService executorService;
	
	/**
	 * Executor service locker for concurrency issues
	 */
	private final Object executorLocker;
	
	/**
	 * Classifier weights
	 */
	private double[] weights;
	
	/**
	 * Classifier constructor
	 */
	public EnsembleClassifier(){
		classifiers = new HashMap<>();
		dataSets = new HashMap<>();
		executorLocker = new Object();
		executorService = Executors.newSingleThreadExecutor();
	}
	
	
	
	/**
	 * classifies emotional status
	 * @param featureController
	 * @return 
	 */
	public void classify(FeatureListController featureController){
		synchronized(executorLocker){
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					if(trainController == null)
						return;


					double[] resDist = null;
					System.out.println("Test feature controller listener size:" + featureController.getSensorListeners().size());
					for(SensorListener listener : featureController.getSensorListeners()){
						if(!classifiers.containsKey(listener))
							continue;
						List<FeatureList> list = featureController.getLastNFeatureList(listener, 1);
						if(list ==null || list.size()<1)
							continue;

						Instance instance = list.get(0).getInstance();
						if(instance!=null){
							try {
								Classifier classifier = classifiers.get(listener);
								instance.setDataset(dataSets.get(listener));


								double[] dist =  classifier.distributionForInstance(instance);
								double val = classifier.classifyInstance(instance);
								System.out.println(instance.classAttribute().value((int)val));
								if(resDist==null)
									resDist = dist;
								else {
									for(int i=0;i<resDist.length;++i)
										//coefficient must be changed to weight
										resDist[i] += 1*dist[i];
								}
							} catch (Exception ex) {
								System.out.println(ex.getMessage());
								System.out.println(ex.getLocalizedMessage());
							}

						}
					}

					System.out.println("classified");
					for(int i=0;i<resDist.length;++i)
						System.out.println(resDist[i]+ " ");
					System.out.println();
				}
			});
		}
		
		
		
	}
	
	/**
	 * returns classifier for a sensorListener type
	 * @param sensorListener
	 * @return 
	 */
	private Classifier createClassifier(SensorListener sensorListener){
		Classifier model;
		try {
			String classifierClassPath = null;
			String[] options = null;
			
			if(sensorListener.getClass() == SensorListenerEEG.class){
				classifierClassPath = "weka.classifiers.functions.SMO";
				options = new String[]{"-N", "2"};
			}
			
			model = Classifier.forName( classifierClassPath, options);
		} catch (Exception ex) {
			return null;
		}
		return model;
	}
	
	/**
	 * trains data from a specific sensorListener
	 * @param featureController
	 * @param sensorListener 
	 * @return  
	 */
	public void trainOfSensor(FeatureListController featureController, SensorListener sensorListener){
		synchronized(executorLocker){
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println("train");
					trainController = featureController;
					try {
						if(!featureController.getSensorListeners().contains(sensorListener))
							return;
						trainController = featureController;


						//add sensor
						if(!classifiers.containsKey(sensorListener)){
							Classifier model = createClassifier(sensorListener);
							if(model == null)
								return;
							classifiers.put(sensorListener, model);
						}

						Classifier model = classifiers.get(sensorListener);

						List<FeatureList> features = featureController.getLastNFeatureList(sensorListener, -1);

						//no features exists of sensor
						if(features.isEmpty())
							return;

						System.out.println("train inside");
						//create weka Instances
						Instances instances = new Instances("instances "+sensorListener.toString(), features.get(0).getFeatureAttributes(), features.size());
						instances.setClassIndex(instances.numAttributes()-1);
						System.out.println("train inside class set");

						//save instances for the testing
						dataSets.put(sensorListener, instances);

						for(int i=0;i<features.size();++i){
							//create instance
							Instance instance = features.get(i).getInstance();
							instance.setDataset(instances);
							FastVector attributes = features.get(0).getFeatureAttributes();
							instance.setValue((Attribute)attributes.elementAt(attributes.size()-1), features.get(i).getEmotion().name());
							instances.add(instance);
						}
						System.out.println("before built");
						model.buildClassifier(instances);
						System.out.println("built");

					} catch (Exception ex) {
						System.out.println("EX:" + ex.getMessage());
						System.out.println("EX:" + ex.getLocalizedMessage());
					}
				}
			});
		}
		
		
	}
	
	/**
	 * removes classifier of a sensor. should be called before sensor listener is removed from featurelistcontroller
	 * @param listener
	 * @return 
	 */
	public void removeClassifierOfSensor(SensorListener listener){
		synchronized(executorLocker){
			executorService.execute(new Runnable() {
				@Override
				public void run() {
		
					if(classifiers.get(listener)==null)
						return ;
					classifiers.remove(listener);
					dataSets.remove(listener);
				}
			});
		}		
	}
	
	/**
	 * Trains the system with feature set
	 * @param featureController 
	 */
	/*
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
		
	}*/
	
	
}
