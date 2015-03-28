/**
 * 
 */
package emotionlearner.eegFrame.features.nonlinear.other;

/**
 * @author lsuc
 *Second Order Difference Plot from time series verzija
 */
public class CTMSecondOrderDifferencePlot {
	public static double calculateCTM (double[] values, double r){
		double ctm = 0.0;
		double temp = 0.0;
		for (int i=0; i<values.length-2; i++){ 
			temp = Math.pow(values[i+2]-values[i+1], 2) + Math.pow(values[i+1]-values[i], 2);
			if(temp*temp < r){
				ctm++;
			}
		}
		ctm /= values.length-2;
		return ctm;
	}

}
