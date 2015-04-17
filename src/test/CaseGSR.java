package test;

import sensormanager.SensorListener;
import sensormanager.SensorListenerGSR;

/**
 * Created by Mustafa on 17.4.2015.
 */
public class CaseGSR {
    SensorListener gsr;
    public CaseGSR() {

        gsr = new SensorListenerGSR("COM3");
        gsr.connect();
    }

    public void runtest1() {
        while(!gsr.isConnected()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
