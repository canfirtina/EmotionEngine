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
import user.manager.User;
import user.manager.UserManager;

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
        System.out.println("logging in? = " +usr.login("ayhun", "1993"));
        
        DataManager dm = DataManager.getInstance();
        System.out.println(dm.getCurrentUser().getName());
        dm.setCurrentUser("ayhun");        
        User u = dm.getCurrentUser();
        if(u.getName().equals("ayhun"))
            System.out.println("Set current user and get current user works.");
        u.enableSensor("GSR");
        dm.saveUser(u);
        if(dm.getCurrentUser().getEnabledSensors().containsKey("GSR"))
            System.out.println("save user works");
        
        u = dm.getUser("emcail");
        if(u!=null && u.getName().equals("emcail"))
            System.out.println("get user works");
        
        ArrayList<User> us = dm.getAllUsers();
        if(dm.checkUserExist("ayhun")){
            System.out.println("check user exists works");
        }
        
    }
}
