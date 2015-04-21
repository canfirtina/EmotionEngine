/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

/**
 *
 * @author CanFirtina
 */
public enum ScreenInfo {
    
    LoginScreen( "login", "LoginScreen.fxml"),
    SignUpScreen( "signUp", "SignUpScreen.fxml"),
    ForgotPasswordScreen( "forgotPassword", "ForgotPasswordScreen.fxml"),
    ProfileScreen( "profile", "ProfileScreen.fxml");
    
    private final String screenID;
    private final String screenFileName;

    private ScreenInfo( String screenID, String screenFileName){
        
        this.screenID = screenID;
        this.screenFileName = screenFileName;
    }
    
    public String screenId(){
        
        return screenID;
    }
    
    public String screenFileName(){
        
        return screenFileName;
    }
}
