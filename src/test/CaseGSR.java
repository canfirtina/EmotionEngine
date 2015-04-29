package test;

import sensormanager.data.DataEpocher;
import sensormanager.data.TimeBasedDataEpocher;
import sensormanager.listener.SensorListener;
import sensormanager.listener.SensorListenerGSR;

/**
 * Created by Mustafa on 17.4.2015.
 */
public class CaseGSR {
    SensorListener gsr;
    public CaseGSR() {

        gsr = new SensorListenerGSR("COM3");
        DataEpocher de = new TimeBasedDataEpocher(4000);
        gsr.connect();
        gsr.setDataEpocher(de);
    }

    public void runtest1() {

        gsr.startStreaming();
        System.out.println("streeeaming");

    }
}
