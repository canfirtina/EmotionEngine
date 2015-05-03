/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistentdatamanagement;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import shared.TutorialInfo;
import user.manager.User;

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

    private Connection getConnection() {
        try {
            String url = "jdbc:mysql://104.131.97.74/emotion_db";
            String user = "root";
            String password = "ayhuntekat93";
            if (!(con != null && con.isValid(1))) {
                con = DriverManager.getConnection(url, user, password);
            }
            return con;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    private Statement getStatement() {
        try {
            if (con != null && con.isValid(1)) {
                if (st != null && !st.isClosed()) {
                    return st;
                } else {
                    return con.createStatement();
                }
            } else {
                return getConnection().createStatement();
            }
        } catch (SQLException ex) {
            System.out.println("");
        }
        return null;
    }

    public User getCurrentUser() {
        rs = null;
        User currUser = null;
        String currentUser = "";
        try {
            st = getStatement();
            rs = st.executeQuery("select state_value from app_state where state_name = 'current_user';");

            if (rs.next()) {
                currentUser = rs.getString("state_value");
            } else {
                currentUser = "";
            }

            rs = st.executeQuery("select * from users where email = '" + currentUser + "';");
            if (rs.next()) {
                currUser = new User(rs.getString("email"), rs.getString("password"));

                /*st = con.createStatement();
                 rs = st.executeQuery("select * from enabled_sensors where email = '" + currentUser + "' and is_enabled = '1';");
                 while (rs.next()) {
                 currUser.enableSensor(rs.getString("sensor"));
                 }*/
                rs = st.executeQuery("select * from software_used where email = '" + currentUser + "';");
                while (rs.next()) {
                    currUser.usedSoftware(rs.getString("software"), rs.getInt("time"));
                }

                rs = st.executeQuery("select * from play_count where email = '" + currentUser + "';");
                while (rs.next()) {
                    for (int i = 0; i < rs.getInt("count"); i++) {
                        currUser.playedTutorial(new Pair<String, String>(rs.getString("sensor"), rs.getString("tutorial")));
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        } finally {
            attemptClose(con, st, rs);
        }
        return currUser;
    }

    public User getUser(String currentUser) {
        st = null;
        rs = null;
        User currUser = null;

        try {
            st = getStatement();
            rs = st.executeQuery("select * from users where email = '" + currentUser + "';");
            if (rs.next()) {
                currUser = new User(rs.getString("email"), rs.getString("password"));

                /*st = con.createStatement();
                 rs = st.executeQuery("select * from enabled_sensors where email = '" + currentUser + "' and is_enabled = '1';");
                 while (rs.next()) {
                 currUser.enableSensor(rs.getString("sensor"));
                 }*/
                rs = st.executeQuery("select * from software_used where email = '" + currentUser + "';");
                while (rs.next()) {
                    currUser.usedSoftware(rs.getString("software"), rs.getInt("time"));
                }

                rs = st.executeQuery("select * from play_count where email = '" + currentUser + "';");
                while (rs.next()) {
                    for (int i = 0; i < rs.getInt("count"); i++) {
                        currUser.playedTutorial(new Pair<String, String>(rs.getString("sensor"), rs.getString("tutorial")));
                    }
                }
            }
        } catch (SQLException ex) {
            return null;
        } finally {
            attemptClose(con, st, rs);
        }
        return currUser;
    }

    public ArrayList<User> getAllUsers() {
        rs = null;
        ArrayList<User> all = new ArrayList<>();
        ArrayList<String> userNames = new ArrayList<>();

        try {
            st = getStatement();
            rs = st.executeQuery("select email from users;");
            while (rs.next()) {
                userNames.add(rs.getString("email"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return null;
        } finally {
            attemptClose(con, st, rs);
        }

        for (String user : userNames) {
            all.add(getUser(user));
        }

        return all;
    }

    boolean rateTutorial(String user, String tutorial, int rating) {
        if (rating > 5) {
            rating = 5;
        } else if (rating < 0) {
            rating = 0;
        }

        try {
            st = getStatement();

            st.executeUpdate("INSERT INTO `emotion_db`.`user_ratings` VALUES('" + user + "','" + tutorial + "','" + rating + "');");
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return false;
        } finally {
            attemptClose(con, st, rs);

        }
    }

    boolean updateRating(String user, String tutorial, int rating) {
        if (rating > 5) {
            rating = 5;
        } else if (rating < 0) {
            rating = 0;
        }

        try {
            st = getStatement();

            st.executeUpdate("UPDATE `emotion_db`.`user_ratings` SET `star` = '" + rating + "' WHERE `email` = '" + user + "' and tutorial = '" + tutorial + "';");
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return false;
        } finally {
            attemptClose(con, st, rs);
        }
    }

    double getAverageRating(String tutorial) {
        try {
            st = getStatement();

            rs = st.executeQuery("SELECT AVG(`star`) as star FROM `emotion_db`.`user_ratings` WHERE `tutorial`='" + tutorial + "' GROUP BY `tutorial`;");
            if (rs.next()) {
                return rs.getDouble("star");
            }
            return -1;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return -1;
        } finally {
            attemptClose(con, st, rs);
        }
    }

    double getUserRating(String user, String tutorial) {
        try {
            st = getStatement();

            rs = st.executeQuery("SELECT `star` FROM `emotion_db`.`user_ratings` WHERE `email`='" + user + "' AND `tutorial` = '" + tutorial + "';");
            if (rs.next()) {
                return rs.getDouble("star");
            }
            return -1;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return -1;
        } finally {
            attemptClose(con, st, rs);
        }
    }

    boolean setCurrentUser(String userName) {
        rs = null;

        try {
            st = getStatement();
            st.executeUpdate("UPDATE `emotion_db`.`app_state` SET `state_name` = 'current_user', `state_value` = '" + userName + "' WHERE `state_name` = 'current_user'");
        } catch (SQLException ex) {
            return false;
        } finally {
            attemptClose(con, st, rs);
        }
        return true;
    }

    public boolean saveUser(User u) {
        rs = null;
        User currUser = null;
        String currentUser = "";
        try {
            con = getConnection();
            con.setAutoCommit(false); //transaction block start            

            st = con.createStatement();
            st.executeUpdate("INSERT INTO users (email, password) VALUES ('" + u.getName() + "', '" + u.getPass() + "') ON DUPLICATE KEY UPDATE password = '" + u.getPass() + "';");

            /*if (!u.getEnabledSensors().isEmpty()) {
             st.executeUpdate("DELETE FROM `emotion_db`.`enabled_sensors` WHERE email = '"+u.getName()+"';");
             for (String key : u.getEnabledSensors().keySet()) {
             if(u.getEnabledSensors().get(key))
             st.executeUpdate("INSERT INTO `emotion_db`.`enabled_sensors` VALUES ('" + u.getName() + "','" + key + "','1');");
             else
             st.executeUpdate("INSERT INTO `emotion_db`.`enabled_sensors` VALUES ('" + u.getName() + "','" + key + "','0');");
             }
             }*/
            if (!u.getSoftwareUsed().isEmpty()) {
                st.executeUpdate("DELETE FROM `emotion_db`.`software_used` WHERE email = '" + u.getName() + "';");
                for (String key : u.getSoftwareUsed().keySet()) {
                    st.executeUpdate("INSERT INTO `emotion_db`.`software_used` VALUES('" + u.getName() + "','" + key + "'," + u.getSoftwareUsed().get(key) + ");");
                }
            }

            if (!u.getPlayCount().isEmpty()) {
                st.executeUpdate("DELETE FROM `emotion_db`.`play_count` WHERE email = '" + u.getName() + "';");
                for (Pair<String, String> key : u.getPlayCount().keySet()) {
                    st.executeUpdate("INSERT INTO `emotion_db`.`play_count` VALUES('" + u.getName() + "','" + key.getKey() + "','" + key.getValue() + "','" + u.getPlayCount().get(key) + "');");
                }
            }

            con.commit();
            con.setAutoCommit(true); //transaction block end
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return false;
        } finally {
            attemptClose(con, st, rs);
        }
        return true;
    }

    ArrayList<TutorialInfo> getAllTutorials() {
        ArrayList<TutorialInfo> listOfTutorials = new ArrayList<>();
        try {
            st = getStatement();

            rs = st.executeQuery("SELECT * FROM `emotion_db`.`tutorials`;");
            while (rs.next()) {
                listOfTutorials.add(new TutorialInfo(rs.getString("tutorial_name"), rs.getString("tutorial_description"), rs.getString("tutorial_link"), rs.getString("image_path")));
            }
            return listOfTutorials;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return listOfTutorials;
        } finally {
            attemptClose(con, st, rs);
        }
    }

    TutorialInfo getTutorial(String tutorialName) {
        try {
            st = getStatement();

            rs = st.executeQuery("SELECT * FROM `emotion_db`.`tutorials` WHERE `tutorial_name` = '" + tutorialName + "';");
            if (rs.next()) {
                return new TutorialInfo(rs.getString("tutorial_name"), rs.getString("tutorial_description"), rs.getString("tutorial_link"), rs.getString("image_path"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        } finally {
            attemptClose(con, st, rs);
        }
        return null;
    }

    boolean addTutorial(TutorialInfo tutorial) {
        try {
            st = getStatement();

            st.executeUpdate("INSERT INTO `emotion_db`.`tutorials` VALUES('" + tutorial.getName() + "','" + tutorial.getDescription() + "','" + tutorial.getLink()+ "','" + tutorial.getImagePath() + "');");
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return false;
        } finally {
            attemptClose(con, st, rs);

        }
    }

    static void attemptClose(Connection c, Statement s, ResultSet r) {
        attemptClose(r);
        /*attemptClose(s);
         attemptClose(c);*/
    }

    static void attemptClose(ResultSet o) {
        try {
            if (o != null) {
                o.close();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    static void attemptClose(Statement o) {
        try {
            if (o != null) {
                o.close();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    static void attemptClose(Connection o) {
        try {
            if (o != null) {
                o.close();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
