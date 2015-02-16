package userinterface;

import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class ViewController {
	
	
	/**
	 * View of controller
	 */
	public JPanel contentPanel;
	
	/**
	 * The controller that presents this viewController
	 */
	public ViewController presentingViewController;
	
	/**
	 *  The controller that is presented by this viewController
	 */
	public ViewController presentedViewController;
	
	/**
	 * Creates ViewController with the given view
	 * @param contentPanel sets as view of the controller
	 */
	public ViewController( JPanel contentPanel){
		
		this.contentPanel = contentPanel;
		if(contentPanel==null)
			throw new NullPointerException("contentPanel cannot be NULL");
	}
	
	/**
	 * Tells view that it should load itself
	 */
	public abstract void loadView();
	
	/**
	 * Presents view controller onto this view controller. View controllers' views
	 * can be pushed on the screen in a stack manner with this way.
	 * @param viewController handles the screen instead of this view controller
	 */
	public void presentViewController( ViewController viewController){
		
		contentPanel.removeAll();
		contentPanel.invalidate();
		viewController.loadView();
		contentPanel.revalidate();
	}
	
	/**
	 * Dismisses the view controller that is currently shown by
	 * this view controller.
	 */
	public abstract void dismissViewController();
}