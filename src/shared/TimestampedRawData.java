package shared;

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
	private Timestamp time;
	
	/**
	 * Raw row data coming from sensor
	 */
	private double[] rawData;
	
	/**
	 * Initialized with raw data and time
	 * @param rawData
	 * @param time
	 */
	public TimestampedRawData(double[] rawData, Timestamp timeStamp){
		this.rawData = rawData;
		this.time = timeStamp;
	}
	
	/**
	 * Initialized with raw data and milliseconds
	 * @param rawData
	 * @param time
	 */
	public TimestampedRawData(double[] rawData, long milliSeconds){
		this(rawData, new Timestamp(milliSeconds));
	}
	
	/**
	 * Initialized with raw data and current time milliseconds
	 * @param rawData
	 */
	public TimestampedRawData(double[] rawData){
		this(rawData, new Timestamp(java.lang.System.currentTimeMillis()));
	}
	
	/**
	 * get the time of raw data
	 * @return
	 */
	public Timestamp getTimestamp(){
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
