package UserInterface;

import javax.swing.JPanel;

public class EmotionEngineUI extends JPanel{
	
	public interface EmotionEngineUIDelegate {
		
		public void newUserAdded( String userName);
		public void userSelected( String userName);
		public void sensorsSelected( int[] sensorType);
		public void playGame();
	}
	
	private EmotionEngineUIDelegate delegate;
	
	public EmotionEngineUI(){
		
		createStartupScreen();
	}
	
	public void createStartupScreen(){
		
	}
	
	public void createActiveSensorsListScreen(){
		
		
	}
	
	public void createNewUserScreen(){
		
		
	}
	
	public void setDelegate(EmotionEngineUIDelegate delegate) {

		this.delegate = delegate;
	}
}
