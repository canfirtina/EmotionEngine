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
public interface PresentedScreen {
    
    /**
     * Tells this presentingController that it is presented by this
     * presentingController
     * @param presentingController is responsible for displaying this screen
     */
    void setPresentingScreen( PresentingController presentingController);
}
