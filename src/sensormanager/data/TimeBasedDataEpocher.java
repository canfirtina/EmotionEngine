package sensormanager.data;

import java.util.Date;

public class TimeBasedDataEpocher extends DataEpocher{

	/**
	 * Constructor with time constraint in milliseconds
	 * @param constraint
	 */
	public TimeBasedDataEpocher(long constraint) {
		super(constraint);
	}

	@Override
	public boolean addData(TimestampedRawData data) {
		if(!isNewDataSuitable(data))
			return false;
		
		allData.add(data);
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

	public boolean readyForEpoch() {
		if(allData.size() == 0)
			return false;
		return System.nanoTime()/1000000 - allData.get(0).getTime() >= constraint;
	}

}
