package test;

import gnu.io.PortInUseException;
import sensormanager.util.SerialPortUtilities;

import java.util.HashMap;

/**
 * Created by Mustafa on 22.4.2015.
 */
public class CasePortListener {

    public void runtest1(){
        String[] m = SerialPortUtilities.getConnectedPorts();
        for (String key : m)
            System.out.println(key);
    }
}
