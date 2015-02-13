package EmotionLearner;
import java.util.ArrayList;

import PersistentDataManagement.DataManager;
import PersistentDataManagement.DataManagerObserver;
import SensorManager.*;
import SharedSensorData.*;

public class EmotionEngine implements SensorObserver,SensorFactory, DataManagerObserver {
	private ArrayList<SensorListener> sensorListeners;
	private ArrayList<FeatureExtractor> featureExtractors;
	private DataManager persistentDataManager;
	
	public static EmotionEngine sharedInstance(DataManager persistentDataManager){
		return null;
	}
	
	//SESSION
	public boolean openTrainingSession(Label label){
		return true;
	}
	
	public boolean closeTrainingSession(){
		return true;
	}
	
	public boolean openClassifySession(){
		return true;
	}
	
	public boolean stopClassifySession(){
		return true;
	}
	
	public Label currentEmotion(){
		return null;
	}
	
	//SENSOR FACTORY
	@Override
	public void createSensorListener(int portNumber, 
			Class<SensorListener> sensorType) {
	}
	
	//SENSOR OBSERVER
	@Override
	public void dataArrived(SensorListener sensor) {
		
	}

	@Override
	public void connectionError(SensorListener sensor) {
		
	}

	@Override
	public void connectionEstablished(SensorListener sensor) {
		
	}

	@Override
	public void notify(DataManager manager) {
		
	}
	
}