package test;

import gnu.io.PortInUseException;
import sensormanager.COMPortListener;

/**
 * Created by Mustafa on 22.4.2015.
 */
public class CasePortListener {

    public void runtest1(){
        COMPortListener.getConnectedPorts();
    }
}
