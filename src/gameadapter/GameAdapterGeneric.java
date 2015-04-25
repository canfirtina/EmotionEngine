package gameadapter;

import communicator.Communicator;
import shared.Emotion;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Part of EmotionEngine that is implemented in client game. It provides in game decisions such as speed change etc.
 */
public class GameAdapterGeneric {

    private ServerSocket serverSocket;
    private Socket socket;
    private Emotion activeEmotion;

    public GameAdapterGeneric() {
        //initialize serversocket and socket
        Communicator.startServer();
    }

    /**
     * Sends a signal to indicate predetermined label of scene.
     * @param label
     */
    public void sendEmotionSignal(Emotion label) {

    }


}
