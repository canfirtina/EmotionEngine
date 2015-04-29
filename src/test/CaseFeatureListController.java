/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sensormanager.SensorListener;
import sensormanager.SensorObserver;
import shared.FeatureList;
import shared.TimestampedRawData;
import shared.FeatureListController;

/**
 * 
 * @author aliyesilyaprak
 */
public class CaseFeatureListController {
	
	/**
	 * test last n milliseconds featureListController
	 */
	public void runLastTime(){
		/*try {
			long timeDiff = 100;
			
			FeatureListController controller = new FeatureListController(timeDiff);
			TestSensorListener listener1 = new TestSensorListener();
			controller.registerSensorListener(listener1);
			TestSensorListener listener2 = new TestSensorListener();
			controller.registerSensorListener(listener2);
			
			
			//20 samples in total
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			Thread.sleep(10);
			controller.addFeatureList(listener2, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			controller.addFeatureList(listener1, new FeatureList(new double[10], new Timestamp(new Date().getTime())));
			
			
			for(int i=0;i<2;++i){
				SensorListener listener;
				if(i==0)
					listener = listener1;
				else 
					listener = listener2;
				
				System.out.println("LISTENER" + (i+1));
				
				System.out.println("Now:" + new Date().getTime());
				List<FeatureList> featureLists = controller.getLastFeatureListsInMilliseconds(listener, -1);
				
				System.out.println("All feature lists with milliseconds method");
				for(FeatureList l : featureLists)
					System.out.println(l.getTimestamp().getTime());
				
				System.out.println("All feature lists with n method");
				List<FeatureList> featureLists2 = controller.getLastNFeatureList(listener, -1);
				for(FeatureList l : featureLists)
					System.out.println(l.getTimestamp().getTime());
				
				System.out.println("Last milliseconds " + 40);
				List<FeatureList> featureLists3 = controller.getLastFeatureListsInMilliseconds(listener, 40);
				for(FeatureList l : featureLists3)
					System.out.println(l.getTimestamp().getTime());
				
				System.out.println("Last N Feature Lists " + 6);
				List<FeatureList> featureLists4 = controller.getLastNFeatureList(listener, 6);
				for(FeatureList l : featureLists4)
					System.out.println(l.getTimestamp().getTime());
			}
			

		} catch (InterruptedException ex) {
			System.out.println(ex.getLocalizedMessage());
//Logger.getLogger(CaseFeatureListController.class.getName()).log(Level.SEVERE, new double[10], ex);
		}*/
		
	}
	
	public static void main(String[] args){
		new CaseFeatureListController().runLastTime();
	}
}


class TestSensorListener extends sensormanager.SensorListener{

	@Override
	public boolean connect() {
		return true;
	}

	@Override
	public boolean disconnect() {
		return true;
	}

	@Override
	public List<TimestampedRawData> getSensorData() {
		return null;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void startStreaming() {
		
	}

	@Override
	public void stopStreaming() {
	}

	@Override
	protected void notifyObservers() {
	}

	@Override
	public boolean registerObserver(SensorObserver observer) {
		return true;
	}

	@Override
	public boolean removeObserver(SensorObserver observer) {
		return true;
	}
	
}