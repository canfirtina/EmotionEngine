/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

/**
 *
 * @author CanFirtina
 */
public interface PresentingController {
    
    /**
     * Call it when you want screen with screenName displayed
     * @param screenName name of the screen to be displayed
     * @return true if displaying the specified screen is successful.
     */
    public boolean displayScreen( String screenName);

    /**
     * Call it when you want to add a screen to the controller
     * so that you may want this screen to be displayed afterwards
     * @param screenName name of the screen to be stored. You will use this
     * name to display it again
     * @param resource name of fxml file to get the necessary information
     * about the screen design, controller etc.
     * @return true if the screen is added to the list
     */
    public boolean addScreen( String screenName, String resource);

    /**
     * Removes the previously added screen from the list
     * @param screenName name of the screen to be removed
     * @return true if removal is successful.
     */
    public boolean removeScreen( String screenName);
}
