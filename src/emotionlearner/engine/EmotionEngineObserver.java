/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emotionlearner.engine;

/**
 * EmotionEngineObserver is used for the communication between EmotionEngine and UserInterface 
 * for sensor connection purposes
 * @author aliyesilyaprak
 */
public interface EmotionEngineObserver {
	
	/**
	 * notify is called when sensor statuses are updated
	 * @param engine 
	 */
	public void notify(EmotionEngine engine);		
}
