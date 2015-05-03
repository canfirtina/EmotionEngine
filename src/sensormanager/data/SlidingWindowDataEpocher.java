package sensormanager.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class SlidingWindowDataEpocher extends DataEpocher {

	/**
	 * sliding period
	 */
	private long period;
	
	/**
	 * last epoch timestamp for sliding
	 */
	private long lastEpochTime;

	private boolean isSetTime;
	
	public SlidingWindowDataEpocher(long interval, long period ){
		this(interval);
		this.period = period;
		
	}
	
	/**
	 * constraint is used for interval and default period is 1000
	 * @param constraint
	 */
	public SlidingWindowDataEpocher(long constraint) {
		super(constraint);
		this.allData = new LinkedList<TimestampedRawData>();
		this.period = 1000;

	}

	@Override
	public boolean addData(TimestampedRawData rawData) {
		if(this.isSetTime == false) {
			this.lastEpochTime = rawData.getTime();
			this.isSetTime = true;
		}
		
		LinkedList<TimestampedRawData> list = (LinkedList<TimestampedRawData>)this.allData;
		while(!isNewDataSuitable(rawData))
			list.remove();
		list.add(rawData);
		
		return true;
	}

	/**
	 * Is time constraint is suitable for new data
	 * @param data
	 * @return
	 */
	public boolean isNewDataSuitable(TimestampedRawData data){
		if(allData.size() == 0)
			return true;
		if(data.getTime() - allData.get(0).getTime()>= constraint)
			return false;
		return true;
	}
	
	@Override
	public boolean readyForEpoch() {
		if(allData.size() == 0)
			return false;
		
		LinkedList<TimestampedRawData> list = (LinkedList<TimestampedRawData>)this.allData;
		
		
		if(list.getLast().getTime() - lastEpochTime >= this.period)
			return true;
		return false;
	}
	
	@Override
	public List<TimestampedRawData> getEpoch() {
		this.lastEpochTime = allData.getLast().getTime();
		this.isSetTime = true;
		return super.getEpoch();
	}
	
	@Override
	public void reset(){
		
	}

}
