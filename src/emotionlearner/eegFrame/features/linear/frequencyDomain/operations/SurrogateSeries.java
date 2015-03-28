/*
 * Created on 2006.05.20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package emotionlearner.eegFrame.features.linear.frequencyDomain.operations;
import java.util.*;

/**
 * @author Jovic
 *
 * @version Razred koji raèuna surogat seriju zadane liste podataka koja morati imati velièinu jednaku
 * nekoj potenciji broja 2.
 * 
 */
public class SurrogateSeries {
	/**
	 * Prazan konstruktor, moe se zanemariti i koristiti statièke postupke
	 *
	 */
	public SurrogateSeries(){}
	/**
	 * Raèuna surogat listu originalnoj listi podataka
	 * @param data Originalna lista  podataka
	 * @return Surogat lista
	 * @throws NumberFormatException Ako duljina originalne liste nije višekratnik broja 2
	 */
	public static List<Double> calculateSurrogateSeries(List<Double> data) throws NumberFormatException{
		if (data.size() % 2!= 0){
			throw new NumberFormatException("Data length is not a power of 2");
		}
		Complex [] c = new Complex[data.size()];
		for (int i=0; i<data.size(); i++){
			c[i] = new Complex(data.get(i).doubleValue(),0.0);
		}
		c = FFT.ifft(RandomPhases.doRandomPhases(FFT.fft(c),false));
		List<Double> d = new ArrayList<Double>();
		for (int i=0; i<data.size(); i++){
			d.add(Double.valueOf(c[i].abs()));
		}
		return d;
	}
	/**
	 * Raèuna surogat niz originalnom nizu podataka
	 * @param data Originalni niz podataka
	 * @return Surogat niz
	 * @throws NumberFormatException Ako duljina originalnog niza nije višekratnik broja 2
	 */
	public static double[] calculateSurrogateSeries(double[] data) throws NumberFormatException{
		if (data.length % 2!= 0){
			throw new NumberFormatException("Data length is not a power of 2");
		}
		Complex [] c = new Complex[data.length];
		for (int i=0; i<data.length; i++){
			c[i] = new Complex(data[i],0.0);
		}
		c = FFT.ifft(RandomPhases.doRandomPhases(FFT.fft(c),false));
		double [] d = new double[data.length];
		for (int i=0; i<data.length; i++){
			d[i] = c[i].abs();
		}
		return d;
	}
}
