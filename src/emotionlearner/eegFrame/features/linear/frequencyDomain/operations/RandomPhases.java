/*
 * Created on 2006.05.20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package emotionlearner.eegFrame.features.linear.frequencyDomain.operations;

/**
 * @author Jovic
 *
 * @version Razred obavlja odabir sluèajnih faza u intervalu [0,2*pi] za polje kompleksnih brojeva, ali takvo da je
 * saèuvana njihova amplituda.
 * 
 */
public class RandomPhases {
	public RandomPhases(){}
	/**
	 * Raèuna se niz kompleksnih brojeva sa fazama rasporeðenima na sluèajni naèin za niz kompleksnih brojeva saèuvajuæi amplitudne vrijednosti 
	 * @param compl Niz kompleksnih brojeva
	 * @param preserve Ako je true, postupak saèuva originalni niz kompleksnih brojeva (preporuèljivo)
	 * @return	Niz kompleksnih brojeva sa fazama izraèunatima sluèajno
	 */
	public static Complex[] doRandomPhases(Complex[] compl, boolean preserve){
		double ampl, ran, d, x, y;
		if (preserve){
			Complex [] ret = new Complex[compl.length];
			for (int i=0; i<compl.length; i++){
				ampl = compl[i].abs();
				ran = Math.random()*360;
				d = Math.tan(2*Math.PI*ran/360);
				x = ampl*Math.sqrt(1/1+d*d);
				y = d*x;
				ret[i] = new Complex(x,y);
			}
			return ret;
		}
		else {
			for (int i=0; i<compl.length; i++){
				ampl = compl[i].abs();
				ran = Math.random()*360;
				d = Math.tan(2*Math.PI*ran/360);
				x = ampl*Math.sqrt(1/(1+d*d));
				y = d*x;
				compl[i] = new Complex(x,y);
			}
			return compl;
		}
	}
}
