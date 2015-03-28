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
 * @version Razred obavlja odabir slu�ajnih faza u intervalu [0,2*pi] za polje kompleksnih brojeva, ali takvo da je
 * sa�uvana njihova amplituda.
 * 
 */
public class RandomPhases {
	public RandomPhases(){}
	/**
	 * Ra�una se niz kompleksnih brojeva sa fazama raspore�enima na slu�ajni na�in za niz kompleksnih brojeva sa�uvaju�i amplitudne vrijednosti 
	 * @param compl Niz kompleksnih brojeva
	 * @param preserve Ako je true, postupak sa�uva originalni niz kompleksnih brojeva (preporu�ljivo)
	 * @return	Niz kompleksnih brojeva sa fazama izra�unatima slu�ajno
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
