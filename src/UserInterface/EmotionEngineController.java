package UserInterface;

import javax.swing.JPanel;

import UserInterface.EmotionEngineUI.EmotionEngineUIDelegate;

public class EmotionEngineController extends ViewController implements EmotionEngineUIDelegate{
	
	private EmotionEngineUI mainView;
	
	public EmotionEngineController( JPanel contentPanel) {
		super(contentPanel);
		
		loadView();
	}
	
	@Override
	public void loadView() {
		
		if( mainView != null){
			contentPanel.remove(mainView);
		}
		
		mainView = new EmotionEngineUI();
		mainView.setDelegate( this);
		contentPanel.add( mainView);
	}
	
	@Override
	public void presentViewController(ViewController viewController) {

		contentPanel.removeAll();
		contentPanel.invalidate();
		viewController.loadView();
		contentPanel.revalidate();
		
	}

	@Override
	public void dismissViewController() {
		contentPanel.removeAll();
		contentPanel.add( mainView);
	}

	@Override
	public void newUserAdded(String userName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userSelected(String userName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sensorsSelected(int[] sensorType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playGame() {
		// TODO Auto-generated method stub
		
	}
}
