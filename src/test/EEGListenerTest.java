package test;

import sensormanager.data.TimeBasedDataEpocher;
import sensormanager.listener.SensorListenerEEG;

import java.util.Scanner;

/**
 * Created by Mustafa on 2.4.2015.
 */
public class EEGListenerTest {

    public static void main(String[] args) throws InterruptedException {
        SensorListenerEEG eeg = new SensorListenerEEG("COM4");
        TimeBasedDataEpocher de = new TimeBasedDataEpocher(4000);
        eeg.setDataEpocher(de);
        eeg.connect();
        while(!eeg.isConnected()) {
            Thread.sleep(1000);
        }
        eeg.startStreaming();
        Thread.sleep(2000);
        eeg.setChannelState(0, false);
        eeg.setChannelState(2, false);
        eeg.setChannelState(4, false);
        eeg.setChannelState(6, false);
        Thread.sleep(4000);
        eeg.setChannelState(0, true);
        eeg.setChannelState(2, true);
        eeg.setChannelState(4, true);
        eeg.setChannelState(6, true);
        Thread.sleep(4000);
        eeg.stopStreaming();


    }
}
