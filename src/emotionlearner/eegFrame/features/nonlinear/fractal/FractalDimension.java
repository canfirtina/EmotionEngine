package emotionlearner.eegFrame.features.nonlinear.fractal;

/**
 * This abstract class calculates the fractal dimension of EEG signal.
 * A concrete implementation of the fractal dimension is in order
 * 
 * @author Alan Jovic
 *
 */
public abstract class FractalDimension {
	double [] segment;
	
	public FractalDimension(double [] segment){
		this.segment = segment;
	}
}
