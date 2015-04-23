/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensormanager;


import java.util.Enumeration;
import java.util.HashMap;
import gnu.io.*;

/**
 *
 * @author CanFirtina
 */
public class COMPortListener {

    private static HashMap<String, Class> connectedPorts = new HashMap<>();

    public static HashMap<String, Class> getConnectedPorts(){
        Enumeration identifiers = CommPortIdentifier.getPortIdentifiers();

        while(identifiers.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier)identifiers.nextElement();
            if(port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                //Class types does not mean anything, they are here just for if we could add this functionality later
                if(port.getName().equals("COM3"))
                    connectedPorts.put(port.getName(), SensorListenerGSR.class);
                else
                    connectedPorts.put(port.getName(), SensorListenerEEG.class);

            }
        }


        return connectedPorts;
    }
}
