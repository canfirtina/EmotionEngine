/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensormanager;

import java.util.HashMap;

/**
 *
 * @author CanFirtina
 */
public class COMPortListener {
    
    public static HashMap<String, Class> getConnectedPorts(){
        
        HashMap<String, Class> testPorts = new HashMap<String,Class>();
        testPorts.put("COM4", sensormanager.SensorListenerEEG.class);
        testPorts.put("COM3", sensormanager.SensorListenerGSR.class);
        
        return testPorts;
    }
}
