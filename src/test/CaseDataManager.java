/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import persistentdatamanagement.DataManager;
import usermanager.User;
import usermanager.UserManager;

/**
 *
 * @author ayhun
 */
public class CaseDataManager {
    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;
    static String currentUser;

    public static void main(String[] args) {
        testDataManager();

    }

    public static void testDataManager() {
        UserManager usr = UserManager.getInstance();
        System.out.println(usr.login("ayhun", "93"));
        /*
        DataManager dm = DataManager.getInstance();
        System.out.println(dm.getCurrentUser().getName());
        dm.setCurrentUser("ayhun");        
        User u = dm.getCurrentUser();
        u.enableSensor("GSR");
        dm.saveUser(u);
        System.out.println("çalıştımı?=" + dm.getCurrentUser().getEnabledSensors().containsKey("GSR"));
        u = dm.getUser("emcail");
        ArrayList<User> us = dm.getAllUsers();
        if(dm.checkUserExist("ayhun")){
            dm.setCurrentUser("emcail");
        }
        u=dm.getCurrentUser();*/
    }
}
