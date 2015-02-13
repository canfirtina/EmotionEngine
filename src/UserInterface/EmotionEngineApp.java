package UserInterface;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class EmotionEngineApp {
	
	/**
	 * Frame of Emotion Engine. All UI is built upon it.
	 */
	public static JFrame mainFrame;
	
	/**
	 * This panel is modified as the screen changes.
	 * Shows currently what is on the screen.
	 */
	private static JPanel mainPanel;
	
	/**
	 * View Controller that handles the connection between model (Emotion Engine) and UI
	 */
	public static ViewController initialViewController;
	
	/**
	 * Initializes the initialViewController.
	 * @param viewController is used as initialViewController
	 */
	public static void setInitialViewController( ViewController viewController){		
		initialViewController = viewController;
	}
	
	/**
	 * Initializes the current screen and shows it
	 */
	public static void loadWindow() {

		// Initialize frame
		JFrame frame = new JFrame("Emotion Engine");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(800, 800));
		frame.setResizable(true);

		mainPanel = new JPanel();
		frame.getContentPane().add(mainPanel);

		setInitialViewController(new EmotionEngineController( mainPanel));

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
			}
		});

		frame.setVisible(true);
		frame.pack();
		frame.repaint();
	}
	
	/**
	 * Runs Emotion Engine
	 */
	public static void main(String[] args) {
		
		loadWindow();
	}
}
