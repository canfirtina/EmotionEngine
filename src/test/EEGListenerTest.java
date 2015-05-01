package test;

import sensormanager.data.TimeBasedDataEpocher;
import sensormanager.data.TimestampedRawData;
import sensormanager.listener.SensorListener;
import sensormanager.listener.SensorListenerEEG;
import sensormanager.listener.SensorObserver;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Mustafa on 2.4.2015.
 */
public class EEGListenerTest {

    public static void main(String[] args) {
        try {
            SensorListenerEEG eeg = new SensorListenerEEG("COM4");
            TimeBasedDataEpocher de = new TimeBasedDataEpocher(4000);
            eeg.setDataEpocher(de);
            eeg.registerObserver(new SensorObserver() {
                @Override
                public void dataArrived(SensorListener sensor) {
                    List<TimestampedRawData> s = sensor.getSensorData();
                    for (TimestampedRawData t : s)
                        System.out.println(Arrays.toString(t.getData()));
                    System.out.println(s.size());
                }

                @Override
                public void connectionError(SensorListener sensor) {
                    System.out.println("connection error");
                }

                @Override
                public void connectionEstablished(SensorListener sensor) {
                    System.out.println("connection established");
                }

                @Override
                public void connectionFailed(SensorListener sensor) {
                    System.out.println("connection failed");
                }
            });
            eeg.connect();
            while (!eeg.isConnected()) {
                Thread.sleep(1000);
            }
            eeg.startStreaming();
        } catch(Exception e) {
            e.printStackTrace();
        }


    }
}
