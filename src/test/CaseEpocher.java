package test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import sensormanager.data.TimestampedRawData;
import sensormanager.data.SlidingWindowDataEpocher;

public class CaseEpocher {
	
	public void runSlidingWindowDataEpocherTest(){
		SlidingWindowDataEpocher epocher = new SlidingWindowDataEpocher(10, 2);
		Timestamp first = new Timestamp(new Date().getTime());
		System.out.println("XXX");
		for(int i=0;i<200;++i){
			if(epocher.addData(new TimestampedRawData(null, new Timestamp(new Date().getTime())))){
				System.out.println("ADD");
				if(epocher.readyForEpoch()){
					List<TimestampedRawData> list = epocher.getEpoch();
					
					System.out.print("Epoch: ");
					for(TimestampedRawData data : list)
						System.out.print(data.getTimestamp().getTime() + " ");
					System.out.println();
				}
			}
				
		}
	}

}
