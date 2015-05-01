/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import sensormanager.SensorListener;

/**
 * Controls the feature list information flow and history
 * @author aliyesilyaprak
 */
public class FeatureListController {
	
	/**
	 * registered sensor listeners
	 */
	private ArrayList<SensorListener> sensorListeners;
	
	/**
	 * all feature lists that are saved
	 */
	public LinkedList<LinkedList<FeatureList>> listOfFeatureLists;
	
	/**
	 * time limit of featureLists(maximum time difference between first and last feature list)
	 */
	private long timeLimit;
	
	/**
	 * FeatureListController without a time limit
	 */
	public FeatureListController(){
		this.sensorListeners = new ArrayList<>();
		this.listOfFeatureLists = new LinkedList<>();
		this.timeLimit = -1;
	}
	
	/**
	 * FeatureListController with a certain time limit in milliseconds
	 * @param timeLimit 
	 */
	public FeatureListController(long timeLimit){
		this();
		this.timeLimit = timeLimit;
	}
	
	/**
	 * register a sensor listener to controller
	 * @param listener 
	 */
	public boolean registerSensorListener(SensorListener listener){
		//if sensor is already registered to controller then ignore it
		if(sensorListeners.contains(listener))
			return false;
		
		sensorListeners.add(listener);
		listOfFeatureLists.add(new LinkedList<FeatureList>());
		return true;
	}
	
	/**
	 * remove a sensorListener 
	 * @return 
	 */
	public boolean unregisterSensorListener(SensorListener listener){
		//if sensor is not in list of sensors
		if(!sensorListeners.contains(listener))
			return false;
		
		listOfFeatureLists.remove(sensorListeners.indexOf(listener));
		sensorListeners.remove(listener);
		
		return true;
	}
	
	/**
	 * returns registered sensorListeners
	 * @return 
	 */
	public List<SensorListener> getSensorListeners(){
		return (List<SensorListener>) sensorListeners.clone();
	}
	
	/**
	 * add featureList which is coming from a specific SensorListener
	 * @param listener
	 * @param list
	 * @return 
	 */
	public boolean addFeatureList(SensorListener listener, FeatureList list){
		//if sensor is not in the list then don't add the featureList
		if(!sensorListeners.contains(listener))
			return false;
		
		
		LinkedList<FeatureList> listenerFeatures = listOfFeatureLists.get(sensorListeners.indexOf(listener));
		listenerFeatures.add(list);
		//if there is a time limit than delete some of them
		if(timeLimit !=-1){
			while(list.getTimestamp().getTime() - listenerFeatures.peekFirst().getTimestamp().getTime() > timeLimit)
				listenerFeatures.removeFirst();
		}
		
		return true;
	}
	
	/**
	 * get last n feature lists saved by a sensorListener (n=-1 for all featureLists of listener)
	 * @param listener
	 * @param n
	 * @return 
	 */
	public List<FeatureList> getLastNFeatureList(SensorListener listener, int n){
		//if there is no such sensor then ignore it
		if(!sensorListeners.contains(listener))
			return null;
		
		LinkedList<FeatureList> listenerFeatures = listOfFeatureLists.get(sensorListeners.indexOf(listener));
		return lastNFeaturesLists(listenerFeatures, n);
	}
	
	/**
	 * get feature lists saved in last n milliseconds (n=-1 for all featureLists of listener)
	 * @param listener
	 * @param timeDifference
	 * @return 
	 */
	public List<FeatureList> getLastFeatureListsInMilliseconds(SensorListener listener, long timeDifference){
		//if there is no such sensor then ignore it
		if(!sensorListeners.contains(listener))
			return null;
		
		LinkedList<FeatureList> listenerFeatures = listOfFeatureLists.get(sensorListeners.indexOf(listener));
		return lastFeatureListsInMilliseconds(listenerFeatures, timeDifference);
	}
	
	/**
	 * gets last n featureLists 
	 * @param listenerFeatures
	 * @param n
	 * @return 
	 */
	private List<FeatureList> lastNFeaturesLists(LinkedList<FeatureList> listenerFeatures, int n){
		listenerFeatures = (LinkedList<FeatureList>) listenerFeatures.clone();
		if(listenerFeatures.size() <= n || n == -1)
			return listenerFeatures;
		
		
		while(listenerFeatures.size() > n)
			listenerFeatures.remove();
		
		return listenerFeatures;
	}
	
	/**
	 * get last featureLists saved in n milliseconds
	 * @param listenerFeatures
	 * @param timeDiff
	 * @return 
	 */
	private List<FeatureList> lastFeatureListsInMilliseconds(LinkedList<FeatureList> listenerFeatures, long timeDiff){
		listenerFeatures = (LinkedList<FeatureList>)listenerFeatures.clone();
		if(timeDiff == -1)
			return listenerFeatures;
		long now = new Date().getTime();
		while(listenerFeatures.peekFirst()!=null && now - listenerFeatures.peekFirst().getTimestamp().getTime() > timeDiff)
			listenerFeatures.removeFirst();
		return listenerFeatures;
	}
	
}
