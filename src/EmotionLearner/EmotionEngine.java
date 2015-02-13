package EmotionLearner;
import java.util.ArrayList;

import SensorManager.*;
import SharedSensorData.*;

public class EmotionEngine implements SensorObserver,SensorFactory {
	private ArrayList<SensorListener> sensorListeners;
	private ArrayList<>
	
	public static EmotionEngine sharedInstance(){
		return null;
	}
	
	


	@Override
	public void createSensorListener(int portNumber, 
			Class<SensorListener> sensorType) {
	}
	
	
	
	
	
}