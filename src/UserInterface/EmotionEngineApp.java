package UserInterface;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class EmotionEngineApp {

	public static JFrame mainFrame;
	private static JPanel mainPanel;
	public static ViewController initialViewController;
	
	public static void setInitialViewController( ViewController viewController){		
		initialViewController = viewController;
	}
	
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
	
	public static void main(String[] args) {
		
		loadWindow();
	}
}
