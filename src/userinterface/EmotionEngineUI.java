package userinterface;

import javax.swing.JPanel;

public class EmotionEngineUI extends JPanel{
	
	public interface EmotionEngineUIDelegate {
		
		/**
		 * Tells its delegate that the new user attempts to be created
		 * @param userName is the name of the new user
		 */
		public void newUserAdded( String userName);
		
		/**
		 * Tells its delegate that a user created before is selected
		 * to activate its profile
		 * @param userName is the name of the selected user
		 */
		public void userSelected( String userName);
		
		/**
		 * Tells its delegate that some of the sensors are selected
		 * to be activated in Emotion Engine.
		 * @param sensorType consists of the sensors activated
		 */
		public void sensorsSelected( int[] sensorType);
		
		/**
		 * Tells its delegate that the user attempts to
		 * play the game
		 */
		public void playGame();
	}
	
	/**
	 * Delegate of the view. View notify this delegate
	 * as the important events happen
	 */
	private EmotionEngineUIDelegate delegate;
	
	/**
	 * Start up UI is created to display
	 */
	public EmotionEngineUI(){
		
		createStartupScreen();
	}
	
	/**
	 * Creates startup UI
	 */
	public void createStartupScreen(){
		
	}
	
	/**
	 * Creates the UI to show available sensors
	 */
	public void createActiveSensorsListScreen(){
		
		
	}
	
	/**
	 * Creates the UI to add new user
	 */
	public void createNewUserScreen(){
		
		
	}
	
	/**
	 * This method is generally used from outside.
	 * Some EmotionEngineUIDelegate can itself as the delegate of this view
	 * @param delegate is set as the delegate of this view
	 */
	public void setDelegate(EmotionEngineUIDelegate delegate) {

		this.delegate = delegate;
	}
}
