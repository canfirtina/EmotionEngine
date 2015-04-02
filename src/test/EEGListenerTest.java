package test;

import sensormanager.SensorListenerEEG;

/**
 * Created by Mustafa on 2.4.2015.
 */
public class EEGListenerTest {

    public static void main(String[] args) {
        SensorListenerEEG eeg = new SensorListenerEEG("COM4");
        eeg.connect();
        eeg.startStreaming();
    }
}
