/**
 * Created by Mustafa on 15.2.2015.
 */
package gameadapter;

import shared.Label;

import java.net.ServerSocket;
import java.net.Socket;

public class GameAdapterGeneric {
    private ServerSocket serverSocket;
    private Socket socket;
    private Label activeEmotionalLabel;

    public GameAdapterGeneric() {
        //initialize serversocket and socket
    }

    /**
     * sends a signal to indicate predetermined label of scene
     * @param label
     */
    public void sendEmotionSignal(Label label) {

    }


}
