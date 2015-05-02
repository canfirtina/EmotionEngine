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
        System.out.println("logging in? = " +usr.login("can@can.com", "b4bed6bc05b52277ef1341dfebca4b"));
        
        DataManager dm = DataManager.getInstance();
        System.out.println(dm.getCurrentUser().getName());
        dm.setCurrentUser("ali@ali.com");        
        User u = dm.getCurrentUser();
        if(u.getName().equals("ali@ali.com"))
            System.out.println("Set current user and get current user works.");
        
        dm.rateTutorial(u.getName(), "asd", 3);
        dm.rateTutorial("can@can.com", "asd", 4);
        if(dm.getAverageRating("asd") == 3.5){
            System.out.println("rateTutorial works");
        }
        
        if(dm.getUserRating(u.getName(), "asd")==3)
            System.out.println("getUserRating works");
        
        u.playedGame("Call of Duty", 456);
        dm.saveUser(u);
        if(dm.getCurrentUser().getGamesPlayed().containsKey("Call of Duty"))
            System.out.println("save user works");
        
        u = dm.getUser("can@can.com");
        if(u!=null && u.getName().equals("can@can.com"))
            System.out.println("get user works");
        
        ArrayList<User> us = dm.getAllUsers();
        if(!dm.checkUserExist("ayhun")){
            System.out.println("check user exists works");
        }
        
    }
}
