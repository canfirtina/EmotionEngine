/*
 * Created on 2006.05.10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package emotionlearner.eegFrame.features.nonlinear.phaseSpace;
/**
 * This class is used to calculate central tendency measure for the second order difference graph
 * 
 * @author Alan Jovic
 *
 */
//PROVJERITI JOŠ
public class CTMPhaseSpacePoints {
	/**
	 *
	 * This method calculates CTM for the second order difference graph 
	 *
	 * @param values array of values containing differences between points
	 * @param r data radius for the CTM calculation, by default it should be set 1/8 of the difference between longest and shortest point differences 
	 * @param dimen second order difference plot dimension
	 * @param interv interval T between consecutive points
	 * @return Central tendency measure
	 * 
	 * M. E. Cohen, D. L. Hudson, and P. C. Deedwania, “Applying continuous chaotic modeling to cardiac signal analysis,” IEEE Eng. Med. Biol. Mag., vol. 15, no. 5, pp. 97–102, Sep./Oct. 1996.
	 */
	public static double calculateCTM (double[] values, double r, int dimen, int interv){
		double ctm = 0.0;
		double sqr = 0.0;
		double temp = 0.0;
		for (int i=0; i<values.length-((dimen*interv+1)); i++){ //-((dimen-1)*interv+2)
			sqr = 0.0;
			for (int j=1; j<=dimen; j++){
				temp = values[i+j*interv]-values[i+(j-1)*interv];
				sqr += temp*temp;
			}
			if (Math.sqrt(sqr)<r){
				ctm++;
			}
		}
		ctm /= (values.length-(dimen*interv));
		return ctm;
	}

	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 20;
}
