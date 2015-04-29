/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensormanager.util;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import gnu.io.*;

/**
 *
 * @author CanFirtina
 */
public class SerialPortUtilities {

    /**
     * Finds a list of serial ports in computer.
     * @return Access names of ports
     */
    public static String[] getConnectedPorts(){
        Enumeration identifiers = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> serialPortList = new ArrayList<>();
        while(identifiers.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier)identifiers.nextElement();
            if(port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                serialPortList.add(port.getName());
            }
        }
        return serialPortList.toArray(new String[serialPortList.size()]);
    }
}
