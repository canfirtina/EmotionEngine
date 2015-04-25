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
    
    public ArrayList<User> getAllUsers() {
        con = null;
        st = null;
        rs = null;
        User currUser = null;
        ArrayList<User> all = new ArrayList<>();
        
        try {
            con = getConnection();

            st = con.createStatement();
            rs = st.executeQuery("select email from users;");
            while (rs.next()) {
                String currentUser = rs.getString("email");
                currUser = new User(currentUser, rs.getString("password"));

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
                all.add(currUser);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {
            attemptClose(con, st, rs);
        }
        
        return all;
    }
    
    public boolean saveUser(User u){
        con = null;
        st = null;
        rs = null;
        User currUser = null;
        String currentUser = "";
        try {
            con = getConnection();
            st = con.createStatement();
            rs = st.executeQuery("select * from users where email = '"+u.getName()+"';");

            if (rs.next()) {
                currentUser = rs.getString("state_value");
            } else {
                currentUser = "insert into users values ('"+ u.getName() +"','"+ u.getPass() +"');";
            }
            //TODO: devamını getir
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
        return false;
    }
/*
    public Accept getAcceptWithAcceptIdAndUserId(int acceptId, int personId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Accept accept = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement("SELECT Accept.id, Accept.post_id, Post.name AS post_name, Accept.person_id, p.user_name AS person_user_name, Post.person_id AS post_person_id, pp.user_name AS post_person_user_name, Accept.create_date, Accept.active FROM Accept "
                    + "JOIN Post ON Accept.post_id = Post.id "
                    + "JOIN Person p ON Accept.person_id = p.id "
                    + "JOIN Person pp ON Post.person_id = pp.id "
                    + "WHERE Accept.id=? AND (Accept.person_id=?  OR Post.person_id=?) AND Post.finish_date > NOW()");

            stmt.setInt(1, acceptId);
            stmt.setInt(2, personId);
            stmt.setInt(3, personId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Post post = new Post(rs.getInt("post_id"), rs.getString("post_name"), rs.getInt("post_person_id"), rs.getString("post_person_user_name"));
                accept = new Accept(rs.getInt("id"), post, rs.getInt("person_id"), rs.getString("person_user_name"), rs.getInt("active"), rs.getTimestamp("create_date"));
            }
        } catch (SQLException ex) {
        } finally {
            attemptClose(conn, stmt, rs);
        }
        return accept;
    }

    public boolean addMessage(Message message) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement("INSERT INTO `Message` (`accept_id`, `from_id`, `to_id`, `text`, `read`, `create_date`) VALUES(?,?,?,?,?,?)");

            stmt.setInt(1, message.getAcceptId());
            stmt.setInt(2, message.getFromId());
            stmt.setInt(3, message.getToId());
            stmt.setString(4, message.getText());
            stmt.setBoolean(5, message.isRead());
            stmt.setTimestamp(6, message.getCreateDate());
            int msgId = stmt.executeUpdate();
            if (msgId != 0) {
                success = true;
                message.setId(msgId);
            }
        } catch (SQLException ex) {

        } finally {
            attemptClose(stmt);
            attemptClose(conn);
        }
        return success;
    }

    public ArrayList<Post> getExpiredActivePosts() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Post> posts = null;
        try {
            conn = dataSource.getConnection();
            //change finish_date property
            stmt = conn.prepareStatement("SELECT id FROM `Post` WHERE active=1 AND finish_date < NOW()");

            rs = stmt.executeQuery();
            if (rs.next()) {
                posts = new ArrayList<Post>();
                do {
                    Post p = new Post(rs.getInt("id"), null, 0, null);
                    posts.add(p);
                } while (rs.next());
            }
        } catch (SQLException ex) {

        } finally {
            attemptClose(conn, stmt, rs);
        }
        return posts;
    }

    public ArrayList<Accept> getAcceptsOfPost(Post post) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Accept> accepts = null;
        try {
            conn = dataSource.getConnection();
            //change finish_date property
            stmt = conn.prepareStatement("SELECT Accept.id AS accept_id, Person.id AS person_id "
                    + "FROM Accept "
                    + "JOIN Post ON Accept.post_id = Post.id "
                    + "JOIN Person ON Accept.person_id = Person.id "
                    + "WHERE Post.id =?");
            stmt.setInt(1, post.getId());

            rs = stmt.executeQuery();
            if (rs.next()) {
                accepts = new ArrayList<Accept>();
                do {
                    Accept a = new Accept(rs.getInt("accept_id"), post, rs.getInt("person_id"), null, 1, null);
                    accepts.add(a);
                } while (rs.next());
            }
        } catch (SQLException ex) {

        } finally {
            attemptClose(conn, stmt, rs);
        }
        return accepts;
    }

    public void addActivity(int personId, int acceptId, int postId, int type, int active) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            //change finish_date property
            stmt = conn.prepareStatement("INSERT INTO Activity2 (`person_id`, `accept_id`, `post_id`, `type`, `active`, `create_date`) "
                    + "VALUES(?,?,?,?,?,NOW())");
            stmt.setInt(1, personId);
            stmt.setInt(2, acceptId);
            stmt.setInt(3, postId);
            stmt.setInt(4, type);
            stmt.setInt(5, active);

            stmt.execute();

        } catch (SQLException ex) {
        } finally {
            attemptClose(stmt);
            attemptClose(conn);
        }
    }

    public void deactivatePost(Post p) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            //change finish_date property
            stmt = conn.prepareStatement("UPDATE Post SET active = 0 WHERE id = ?");
            stmt.setInt(1, p.getId());

            stmt.execute();

        } catch (SQLException ex) {
        } finally {
            attemptClose(stmt);
            attemptClose(conn);
        }
    }
*/

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
