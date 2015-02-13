package EmotionLearner;
import java.util.ArrayList;

import SensorManager.*;
import SharedSensorData.*;

public class EmotionEngine implements SensorObserver,SensorFactory {
	private ArrayList<SensorListener> sensorListeners;
	
	public EmotionEngine(){
		
	}
	
	
	public static EmotionEngine sharedInstance(){
		return null;
	}
	
	
	
	
	//SENSOR 
	public void dataArrived(SensorListener listener){
		
	}
	
	
	
}