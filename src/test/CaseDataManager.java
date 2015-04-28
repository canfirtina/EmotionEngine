/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import persistentdatamanagement.DataManager;
import usermanager.User;

/**
 *
 * @author ayhun
 */
public class CaseDataManager {

    private static final String URL = "jdbc:mysql://localhost/emotion_db";
    private static final String USER = "root";
    private static final String PASSWORD = "emotionalpassword";

    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;
    static String currentUser;

    public static void main(String[] args) {
        testDataManager();

    }

    public static void testDataManager() {
        DataManager dm = DataManager.getInstance();
        System.out.println(dm.getCurrentUser().getName());
        dm.setCurrentUser("ayhun");        
        User u = dm.getCurrentUser();
        u = dm.getUser("emcail");
        ArrayList<User> us = dm.getAllUsers();
        if(dm.checkUserExist("ayhun")){
            dm.setCurrentUser("emcail");
        }
        u=dm.getCurrentUser();
    }
}
