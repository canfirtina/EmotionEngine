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

    HashMap<String, Class> connectedPorts = new HashMap<>();

    public static HashMap<String, Class> getConnectedPorts(){
        Enumeration identifiers = CommPortIdentifier.getPortIdentifiers();

        while(identifiers.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier)identifiers.nextElement();

            if(port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                try {
                    SerialPort ppp = (SerialPort) port.open("AAA",11000);
                    System.out.println(port.getName() + " " + port.getPortType() + " " + ppp.getBaudRate());
                } catch (PortInUseException e) {
                    e.printStackTrace();
                }
            }
        }

        HashMap<String, Class> testPorts = new HashMap<String,Class>();

        testPorts.put("COM4", sensormanager.SensorListenerEEG.class);
        testPorts.put("COM3", sensormanager.SensorListenerGSR.class);

        return testPorts;
    }
}
