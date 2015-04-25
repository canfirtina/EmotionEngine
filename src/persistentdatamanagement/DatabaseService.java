/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistentdatamanagement;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import usermanager.User;

/**
 *
 * @author ayhun
 */
public class DatabaseService {

    private static DatabaseService service = null;
    
    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseService sharedInstance() {
        if (service == null) {
            service = new DatabaseService();
        }
        return service;
    }

    private DatabaseService() {
            con = getConnection();            
    }
    
    private Connection getConnection(){
        try {
            String url = "jdbc:mysql://localhost/emotion_db";
            String user = "root";
            String password = "emotionalpassword";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public User getCurrentUser() {
        con = null;
        st = null;
        rs = null;
        User currUser = null;
        String currentUser = "";
        try {
            con = getConnection();
            st = con.createStatement();
            rs = st.executeQuery("select state_value from app_state where state_name = 'current_user';");

            if (rs.next()) {
                currentUser = rs.getString("state_value");
            } else {
                currentUser = "";
            }

            st = con.createStatement();
            rs = st.executeQuery("select * from users where email = '" + currentUser + "';");
            if (rs.next()) {
                currUser = new User(rs.getString("email"), rs.getString("password"));

                st = con.createStatement();
                rs = st.executeQuery("select * from enabled_sensors where email = '" + currentUser + "' and is_enabled = 'true';");
                while (rs.next()) {
                    currUser.enableSensor(rs.getString("sensor"));
                }

                st = con.createStatement();
                rs = st.executeQuery("select * from games_played where email = '" + currentUser + "';");
                while (rs.next()) {
                    currUser.playedGame(rs.getString("game"), rs.getInt("time"));
                }
                
                st = con.createStatement();
                rs = st.executeQuery("select * from play_count where email = '" + currentUser + "';");
                while (rs.next()) {
                    for(int i = 0; i < rs.getInt("count"); i++)
                        currUser.playedTutorial(new Pair<String,String>(rs.getString("sensor"),rs.getString("tutorial")));
                }
            }
        } catch (SQLException ex) {

        } finally {
            attemptClose(con, st, rs);
        }
        return currUser;
    }
    
    public User getUser(String currentUser) {
        con = null;
        st = null;
        rs = null;
        User currUser = null;
        
        try {
            con = getConnection();

            st = con.createStatement();
            rs = st.executeQuery("select * from users where email = '" + currentUser + "';");
            if (rs.next()) {
                currUser = new User(rs.getString("email"), rs.getString("password"));

                st = con.createStatement();
                rs = st.executeQuery("select * from enabled_sensors where email = '" + currentUser + "' and is_enabled = 'true';");
                while (rs.next()) {
                    currUser.enableSensor(rs.getString("sensor"));
                }

                st = con.createStatement();
                rs = st.executeQuery("select * from games_played where email = '" + currentUser + "';");
                while (rs.next()) {
                    currUser.playedGame(rs.getString("game"), rs.getInt("time"));
                }
                
                st = con.createStatement();
                rs = st.executeQuery("select * from play_count where email = '" + currentUser + "';");
                while (rs.next()) {
                    for(int i = 0; i < rs.getInt("count"); i++)
                        currUser.playedTutorial(new Pair<String,String>(rs.getString("sensor"),rs.getString("tutorial")));
                }
            }
        } catch (SQLException ex) {

        } finally {
            attemptClose(con, st, rs);
        }
        return currUser;
    }

    static void attemptClose(Connection c, Statement s, ResultSet r) {
        attemptClose(r);
        attemptClose(s);
        attemptClose(c);
    }

    static void attemptClose(ResultSet o) {
        try {
            if (o != null) {
                o.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void attemptClose(Statement o) {
        try {
            if (o != null) {
                o.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void attemptClose(Connection o) {
        try {
            if (o != null) {
                o.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
