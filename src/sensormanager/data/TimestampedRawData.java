package sensormanager.data;

import java.sql.Timestamp;



/**
 * Raw data with timestamp information
 * @author aliyesilyaprak
 *
 */
public class TimestampedRawData {
	/**
	 * Time of the raw data taken from sensor
	 */
	private long time;
	
	/**
	 * Raw row data coming from sensor
	 */
	private double[] rawData;

	/**
	 * Initialized with raw data and milliseconds
	 * @param rawData
	 * @param time
	 */
	public TimestampedRawData(double[] rawData, long milliSeconds){
		this.rawData = rawData.clone();
		this.time = milliSeconds;
	}
	
	/**
	 * Initialized with raw data and current time milliseconds
	 * @param rawData
	 */
	public TimestampedRawData(double[] rawData){
		this(rawData, java.lang.System.nanoTime()/1000000);
	}
	
	/**
	 * get the time of raw data
	 * @return
	 */
	public long getTime(){
		return time;
	}
	
	/**
	 * get the data
	 * @return
	 */
	public double[] getData(){
		return rawData;
	}
	
}
