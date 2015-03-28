/*
 * Created on 2006.04.22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package emotionlearner.eegFrame.features.nonlinear.phaseSpace;

/**
 * @author Jovic
 *
 * @version Ovaj razred sadri tzv. "Takensove parametre" (iako kao takvi oni ne postoje)
 * nune za atraktor. Nuni su za definiranje atraktora: rekonstrukcijska dimenzija, 
 * broj toèaka za koji se crta atraktor, interval T izmeðu toèaka vremenske serije 
 * (dan u broju toèaka, ne u sekundama) te poèetna toèka vremenske serije za koju se 
 * crta atraktor. 
 * 
 */
public class ParametersTakens {
	private int dimension;	// Rekonstrukcijska (embedded) dimenzija atraktora.
	private int timeSeriesLength; // Broj toèaka vremenske serije za koje se crta atraktor.
	private int interval; // Interval T izmeðu toèaka vremenske serije (dan u broju toèaka, ne u sekundama).
	private int startPoint;	// Poèetna toèka vremenske serije za koju se crta atraktor.
	
	/**
	 * Defaultni konstruktor sadri defaultne postavke za dimenziju, duljinu serije, interval T i 
	 * poèetnu toèku.
	 */
	public ParametersTakens(){
		this.dimension = ParametersTakens.DEFAULT_DIMENSION;
		this.timeSeriesLength = ParametersTakens.DEFAULT_TIME_SERIES_LENGTH;
		this.interval = ParametersTakens.DEFAULT_INTERVAL;
		this.startPoint = ParametersTakens.DEFAULT_START_POINT;
	}
	/**
	 * Ovaj konstruktor pravi objekt ParametersTakens od zadanih mu parametara.
	 * @param dimension Rekonstrukcijska (embedded) dimenzija atraktora.
	 * @param timeSeriesLength Broj toèaka vremenske serije za koje se crta atraktor.
	 * @param interval Interval T izmeðu toèaka vremenske serije (dan u broju toèaka, ne u sekundama).
	 * @param startPoint Poèetna toèka vremenske serije za koju se crta atraktor.
	 * @throws IndexOutOfBoundsException Ako je neki od danih parametara izvan dozvoljenih granica.
	 */
	public ParametersTakens(int dimension, int timeSeriesLength, int interval, int startPoint) throws IndexOutOfBoundsException{
		if (dimension >=2 && dimension <= ParametersTakens.MAX_DIMENSION){
			this.dimension = dimension;
		}
		else {
			throw new IndexOutOfBoundsException("Given dimension is out of bounds");
		}
		if (timeSeriesLength >= ParametersTakens.MIN_TIME_SERIES_LENGTH && timeSeriesLength <= ParametersTakens.MAX_TIME_SERIES_LENGTH){
			this.timeSeriesLength = timeSeriesLength;
		}
		else {
			throw new IndexOutOfBoundsException("Given time series length is out of bounds");
		}
		if (interval >= ParametersTakens.DEFAULT_INTERVAL){
			this.interval = interval;
		}
		else {
			throw new IndexOutOfBoundsException("Given interval is out of bounds");
		}
		if (startPoint >= 0){
			this.startPoint = startPoint;
		}
		else {
			throw new IndexOutOfBoundsException("Given start point is out of bounds");
		}
		
	}
	/**
	 * 
	 * @return Rekonstrukcijsku dimenziju atraktora.
	 */
	public int getDimension(){
		return this.dimension;
	}
	/**
	 * 
	 * @return Broj toèaka vremenske serije za koje se crta atraktor.
	 */
	public int getTimeSeriesLength(){
		return this.timeSeriesLength;
	}
	/**
	 * 
	 * @return Interval T izmeðu toèaka vremenske serije (dan u broju toèaka, ne u sekundama).
	 */
	public int getInterval(){
		return this.interval;
	}
	/**
	 * 
	 * @return Poèetnu toèku vremenske serije za koju se crta atraktor.
	 */
	public int getStartPoint(){
		return this.startPoint;
	}
	/**
	 * 
	 * @return Zadnju toèku na atraktoru.
	 */
	public int getEndPoint(){
		return this.startPoint+this.timeSeriesLength;
	}
	/**
	 * 
	 * @param point Postavlja poèetnu toèku atraktora na vrijednost point.
	 * @throws IndexOutOfBoundsException Ako je izvan opsega, baca se iznimka.
	 */
	public void setStartPoint(int point) throws IndexOutOfBoundsException{
		if (point >= 0){
			this.startPoint = point;
		}
		else {
			throw new IndexOutOfBoundsException("Given start point is < 0 ?");
		}
	}
	/**
	 * 
	 * @param dimension Postavlja rekonstrukcijsku dimenziju atraktora na dimension. 
	 * @throws IndexOutOfBoundsException Ako je izvan opsega, baca se iznimka.
	 */
	public void setDimension(int dimension) throws IndexOutOfBoundsException{
		if (dimension >=2 && dimension <= ParametersTakens.MAX_DIMENSION){
			this.dimension = dimension;
		}
		else {
			throw new IndexOutOfBoundsException("Given dimension is out of bounds. Dimension has to be in interval: 2-"+Integer.toString(MAX_DIMENSION)+".");
		}
	}
	/**
	 * 
	 * @param interval Postavlja Interval T izmeðu toèaka vremenske serije (dan u broju toèaka, ne u sekundama).
	 * @throws IndexOutOfBoundsException Ako je izvan opsega, baca se iznimka.
	 */
	public void setInterval(int interval) throws IndexOutOfBoundsException{
		if (interval >= ParametersTakens.DEFAULT_INTERVAL){
			this.interval = interval;
		}
		else {
			throw new IndexOutOfBoundsException("Given interval is out of bounds");
		}
	}
	/**
	 * 
	 * @param length Broj toèaka vremenske serije za koje se crta atraktor.
	 * @throws IndexOutOfBoundsException Ako je izvan opsega, baca se iznimka.
	 */
	public void setTimeSeriesLength(int length) throws IndexOutOfBoundsException{
		if (length >= ParametersTakens.MIN_TIME_SERIES_LENGTH && length <= ParametersTakens.MAX_TIME_SERIES_LENGTH){
			this.timeSeriesLength = length;
		}
		else {
			throw new IndexOutOfBoundsException("Given time series length is out of bounds. Time seires length has to be in interval: "+Integer.toString(MIN_TIME_SERIES_LENGTH)+"-"+Integer.toString(MAX_TIME_SERIES_LENGTH)+".");
		}
	}
	// Neke defaultne postavke i ogranièenja
	public static final int DEFAULT_START_POINT = 0;
	public static final int DEFAULT_DIMENSION = 2;
	public static final int DEFAULT_TIME_SERIES_LENGTH = 300;
	public static final int DEFAULT_INTERVAL = 1;
	public static final int MAX_DIMENSION = 100;
	public static final int MAX_TIME_SERIES_LENGTH = 15000;
	public static final int MIN_TIME_SERIES_LENGTH = 100;
}
