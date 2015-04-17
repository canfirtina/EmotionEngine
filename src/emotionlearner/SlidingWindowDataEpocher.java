package emotionlearner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import shared.TimestampedRawData;

public class SlidingWindowDataEpocher extends DataEpocher {

	/**
	 * sliding period
	 */
	private long period;
	
	/**
	 * last epoch timestamp for sliding
	 */
	private Timestamp lastEpochTimestamp;
	
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
		this.lastEpochTimestamp = null;
	}

	@Override
	public boolean addData(TimestampedRawData rawData) {
		if(this.lastEpochTimestamp == null)
			this.lastEpochTimestamp = rawData.getTimestamp();
		
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
		if(data.getTimestamp().getTime() - allData.get(0).getTimestamp().getTime()>= constraint)
			return false;
		return true;
	}
	
	@Override
	public boolean readyForEpoch() {
		if(allData.size() == 0)
			return false;
		
		LinkedList<TimestampedRawData> list = (LinkedList<TimestampedRawData>)this.allData;
		
		
		if(list.getLast().getTimestamp().getTime() - lastEpochTimestamp.getTime() >= this.period)
			return true;
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public List<TimestampedRawData> getEpoch() {
		this.lastEpochTimestamp = new Timestamp(new Date().getTime());
		return super.getEpoch();
	}

}
