package test;

import gnu.io.PortInUseException;
import sensormanager.COMPortListener;

import java.util.HashMap;

/**
 * Created by Mustafa on 22.4.2015.
 */
public class CasePortListener {

    public void runtest1(){
        HashMap<String, Class> m = COMPortListener.getConnectedPorts();
        for (String key : m.keySet())
            System.out.println(key + " " + m.get(key));
    }
}
