package persistentdatamanagement;

import emotionlearner.feature.FeatureExtractorProperties;
import emotionlearner.feature.FeatureExtractor;
import emotionlearner.feature.FeatureExtractorEEG;
import sensormanager.listener.SensorListener;
import java.awt.image.BufferedImage;
import user.manager.User;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sensormanager.listener.SensorListenerEEG;
import shared.Emotion;
import shared.FeatureList;

/**
 * Responsible for providing other packages with file input output operations.
 */
public class DataManager {
    private static final String GAME_RECORDS_DIRECTORY = "User Data";
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
    private DataManager() {

        File dir = new File(GAME_RECORDS_DIRECTORY);
        // If data directory doesn't exist, create it
        if (!dir.exists()) {
            dir.mkdir();
        }

        executorService = Executors.newSingleThreadExecutor();
        System.out.println("current=" + getCurrentUser().getName());

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
     * Saves given sample to current user. non-blocking.
     *
     * @param list
     * @param sensor
     */
    public void saveSample(final FeatureList list, final SensorListener sensor) {
        //Tasks are guaranteed to execute sequentially, and no more than one task will be active at any given time
        executorService.submit(new Callable() {
            public Object call() {
                String fileName = getUserDirectory(getCurrentUser().getName()) + "/" + sensor.toString() + "_features.ftr";
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
                    out.print(list.getEmotion().toString() + ",");
                    for (int i = 0; i < list.size() - 1; ++i) {
                        out.print(list.get(i) + ",");
                    }
                    out.println(list.get(list.size() - 1));
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Couldn't write to file " + getUserDirectory(getCurrentUser().getName()) + "/" + fileName);
                }
                return null;
            }
        });
    }

    /**
     * saves a batch of samples for a sensor. non-blocking.
     *
     * @param list
     * @param sensor
     */
    public void saveMultipleSamples(List<FeatureList> list_of_lists, SensorListener sensor) {
		executorService.submit(new Callable() {
            public Object call() {
                String fileName = getUserDirectory(getCurrentUser().getName()) + "/" + sensor.toString() + "_features.ftr";
				
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
					for (FeatureList list : list_of_lists) {
						out.print(list.getEmotion().toString() + ",");
						for (int i = 0; i < list.size() - 1; ++i) {
							out.print(list.get(i) + ",");
						}
						out.println(list.get(list.size() - 1));
					}
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Couldn't write to file " + getUserDirectory(getCurrentUser().getName()) + "/" + fileName);
                }
                return null;
            }
        });
    }

    /**
     * Gets all samples for current user and game.
     *
     * @param sensor type of sensor
     * @return User data
     */
    public ArrayList<FeatureList> getGameData(SensorListener sensor) {
        ArrayList<FeatureList> featureLabelPairs = new ArrayList<FeatureList>();
        String fileName = getUserDirectory(getCurrentUser().getName()) + "/" + sensor.toString() + "_features.ftr";
		System.out.println("fileName:" + fileName);
        List<String> lines = new ArrayList<>();

        try {
            lines = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Couldnt find file: " + fileName);
        } catch (IOException ex) {
            //Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IOException while reading: " + fileName);
        }

        for (String line : lines) {
            String[] current = line.split(",");
            Emotion label = Emotion.valueOf(current[0]);
            double[] features = new double[current.length - 1];
            for (int i = 1; i < current.length; i++) {
                features[i - 1] = Double.parseDouble(current[i]);
            }

			FeatureExtractorProperties props=null;
			if(sensor.getClass() == SensorListenerEEG.class)
				props = FeatureExtractorEEG.getProperties();
			
            featureLabelPairs.add(new FeatureList(features, props.getFeatureAttributes(), label));
        }
        return featureLabelPairs;
    }

    /**
     * Updates user information.
     *
     * @param userData serializable user object
     */
    public boolean saveUser(User userData) {
        addUserDirectory(userData.getName());
        DatabaseService ds = DatabaseService.sharedInstance();
        return ds.saveUser(userData);
    }

    /**
     * Gets data of all users.
     *
     * @return array of objects
     */
    public ArrayList<User> getAllUsers() {
        /*File file = new File(GAME_RECORDS_DIRECTORY);
        String[] names = file.list();
        ArrayList<User> users = new ArrayList<User>();

        for (String name : names) {
            if (new File(GAME_RECORDS_DIRECTORY + "/" + name).isDirectory()) {
                users.add(getUser(name));
            }
        }*/
        
        DatabaseService ds = DatabaseService.sharedInstance();
        return ds.getAllUsers();
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
        DatabaseService ds = DatabaseService.sharedInstance();
        User u = ds.getUser(userName);
        if(u != null){
            addUserDirectory(userName);
        }
        return u;
    }

    /**
     * Changes active user to userName.
     *
     * @param userName
     */
    public boolean setCurrentUser(String userName) {
        DatabaseService ds = DatabaseService.sharedInstance();
        if(ds.setCurrentUser(userName)){
            addUserDirectory(userName);
            return true;
        }
        
        return false;
    }

    /**
     * Gets active user.
     *
     * @return user
     */
    public User getCurrentUser() {    
        DatabaseService ds = DatabaseService.sharedInstance();
        User u = ds.getCurrentUser();
        if(u!= null){
            addUserDirectory(u.getName());            
        }
        return u;
    }

    public boolean rateTutorial(String currentUser, String tutorial, int rating) {
        DatabaseService ds = DatabaseService.sharedInstance();
        return ds.rateTutorial(currentUser, tutorial, rating);
    }
    
    public boolean updateRating(String currentUser, String tutorial, int rating) {
        DatabaseService ds = DatabaseService.sharedInstance();
        return ds.updateRating(currentUser, tutorial, rating);
    }

    public double getAverageRating(String tutorial) {
        DatabaseService ds = DatabaseService.sharedInstance();
        return ds.getAverageRating(tutorial);
    }

    public double getUserRating(String user, String tutorial) {
        DatabaseService ds = DatabaseService.sharedInstance();
        return ds.getUserRating(user, tutorial);
    }
}
