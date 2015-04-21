package persistentdatamanagement;

import java.awt.image.BufferedImage;
import usermanager.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import shared.Emotion;
import shared.FeatureList;
import shared.Sensor;
import shared.UserData;

/**
 * Responsible for providing other packages with file input output operations.
 */
public class DataManager {

    private static final String GAME_RECORDS_DIRECTORY = "User Data";
    private static final String USER_OBJECT_NAME = "user_object.obj";
    private static final String CURRENT_USER = "curr_user_object.obj";
    private static final String PROFILE_PIC = "profile_picture";

    private static DataManager instance = null;

    private ExecutorService executorService;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }

        return instance;
    }

    /**
     * Keeps the current user information.
     */
    private String currentUser;

    private DataManager() {
        File dir = new File(GAME_RECORDS_DIRECTORY);
        // If data directory doesn't exist, create it
        if (!dir.exists()) {
            dir.mkdir();

            // if directory is newly created, create a default user
            saveUser(new User("default", "default"));
        }

        dir = new File(GAME_RECORDS_DIRECTORY + "/" + CURRENT_USER);
        // If data directory doesn't exist, create it
        if (!dir.exists()) {
            FileOutputStream fout;
            try {
                fout = new FileOutputStream(GAME_RECORDS_DIRECTORY + "/" + CURRENT_USER);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(new User("default", "default"));
                oos.flush();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            };
        }

        currentUser = getCurrentUser().getName();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Gets the location of records for given user.
     *
     * @param username
     * @return
     */
    public String getUserDirectory(String username) {
        return GAME_RECORDS_DIRECTORY + "/" + username;
    }

    /**
     * Creates an directory for user records.
     *
     * @param username
     */
    public void addUserDirectory(String username) {
        File dir = new File(GAME_RECORDS_DIRECTORY + "/" + username);

        // If user directory doesn't exist, create it
        if (!dir.exists()) {
            dir.mkdir();
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File("icon-user-default.png"));
                File outputfile = new File(dir.getAbsolutePath() + "/profile.png");
                ImageIO.write(img, "png", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Couldn't save profile image");
            }
        }
    }

    /**
     * Saves given sample to current user.
     *
     * @param sample
     */
    public boolean saveSample(FeatureList list, Sensor sensor, Emotion label) {
        //Tasks are guaranteed to execute sequentially, and no more than one task will be active at any given time
        executorService.submit(new Callable() {
            public Object call() {
                String fileName = getUserDirectory(currentUser) + "/" + sensor.toString() + "_features.ftr";
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
                    out.print(label.toString() + ",");
                    for (int i = 0; i < list.size() - 1; ++i) {
                        out.print(list.get(i) + ",");
                    }
                    out.println(list.get(list.size() - 1));
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Couldn't write to file " + getUserDirectory(currentUser) + "/" + fileName);
                }
                return null;
            }
        });

        return true;
    }

    /**
     * Gets all samples for current user and game.
     *
     * @param sensor type of sensor
     * @return User data
     */
    public UserData getGameData(Sensor sensor) {
        ArrayList<FeatureList> list = new ArrayList<FeatureList>();
        String fileName = getUserDirectory(currentUser) + "/" + sensor.toString() + "_features.ftr";
        String content = "";

        try {
            content = new Scanner(new File(fileName)).useDelimiter("\\Z").next(); // reads whole file
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new UserData(content);
    }

    /**
     * Updates user information.
     *
     * @param userData serializable user object
     */
    public boolean saveUser(User userData) {
        addUserDirectory(userData.getName());
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(getUserDirectory(userData.getName()) + "/" + USER_OBJECT_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(userData);
            oos.flush();
            oos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets data of all users.
     *
     * @return array of objects
     */
    public ArrayList<User> getAllUsers() {
        File file = new File(GAME_RECORDS_DIRECTORY);
        String[] names = file.list();
        ArrayList<User> users = new ArrayList<User>();

        for (String name : names) {
            if (new File(GAME_RECORDS_DIRECTORY + "/" + name).isDirectory()) {
                users.add(getUser(name));
            }
        }

        return users;
    }

    /**
     * Checks if the user exists
     *
     * @param userName
     * @return
     */
    public boolean checkUserExist(String userName) {
        return !(getUser(userName) == null);
    }

    /**
     * Gets data of a user.
     *
     * @return User object
     */
    public User getUser(String userName) {
        User currUser = null;

        // read current user
        ObjectInputStream ois = null;
        try {
            if(new File(GAME_RECORDS_DIRECTORY + "/" + userName + "/" + USER_OBJECT_NAME).exists())
                ois = new ObjectInputStream(new FileInputStream(GAME_RECORDS_DIRECTORY + "/" + userName + "/" + USER_OBJECT_NAME));
            else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start getting the objects out in the order in which they were written
        try {
            currUser = (User) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return currUser;
    }

    /**
     * Changes active user to userName.
     *
     * @param userName
     */
    public boolean setCurrentUser(String userName) {
        User user = getUser(userName);
        if (user == null) {
            return false;
        }

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(GAME_RECORDS_DIRECTORY + "/" + CURRENT_USER);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(user);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Gets active user.
     *
     * @return user
     */
    public User getCurrentUser() {
        User currUser = null;

        // read current user
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(GAME_RECORDS_DIRECTORY + "/" + CURRENT_USER));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start getting the objects out in the order in which they were written
        try {
            currUser = (User) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return currUser;
    }
}
