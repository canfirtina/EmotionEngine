package userinterface;

import javax.swing.JPanel;

import userinterface.EmotionEngineUI.EmotionEngineUIDelegate;

public class EmotionEngineController extends ViewController implements EmotionEngineUIDelegate{
	
	/**
	 * Shows current screen of Emotion Engine
	 */
	private EmotionEngineUI mainView;
	
	/**
	 * Creates EmotionEngineController with the given view
	 * @param contentPanel sets as view of the controller
	 */
	public EmotionEngineController( JPanel contentPanel) {
		super(contentPanel);
		
		loadView();
	}
	
	/**
	 * Tells view that it should load itself
	 */
	@Override
	public void loadView() {
		
		if( mainView != null){
			contentPanel.remove(mainView);
		}
		
		mainView = new EmotionEngineUI();
		mainView.setDelegate( this);
		contentPanel.add( mainView);
	}
	
	/**
	 * Presents view controller onto this view controller. View controllers' views
	 * can be pushed on the screen in a stack manner with this way.
	 * @param viewController handles the screen instead of this view controller
	 */
	@Override
	public void presentViewController(ViewController viewController) {

		contentPanel.removeAll();
		contentPanel.invalidate();
		viewController.loadView();
		contentPanel.revalidate();
		
	}
	
	/**
	 * Dismisses the view controller that is currently shown by
	 * this view controller.
	 */
	@Override
	public void dismissViewController() {
		contentPanel.removeAll();
		contentPanel.add( mainView);
	}
	
	/**
	 * Called when the new user attempts to be created
	 * @param userName is the name of the new user
	 */
	@Override
	public void newUserAdded(String userName) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called when a user created before is selected
	 * to activate its profile
	 * @param userName is the name of the selected user
	 */
	@Override
	public void userSelected(String userName) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called when some of the sensors are selected
	 * to be activated in Emotion Engine.
	 * @param sensorType consists of the sensors activated
	 */
	@Override
	public void sensorsSelected(int[] sensorType) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Called when the user attempts to
	 * play the game
	 */
	@Override
	public void playGame() {
		// TODO Auto-generated method stub
		
	}
}
