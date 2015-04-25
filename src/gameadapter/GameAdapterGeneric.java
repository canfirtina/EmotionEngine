package gameadapter;

import shared.Emotion;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Part of EmotionEngine that is implemented in client game. It provides in game decisions such as speed change etc.
 */
public class GameAdapterGeneric {

    private ArrayList<GameAdapterObserver> observerCollection = new ArrayList<>(1);

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Port number that Communicator works on.
     */
    private int port;

    /**
     * Host server name. This is address of computer on which EmotionEngine runs.
     */
    private String host;

    private PrintStream outputStream;

    private BufferedReader bufferedReader;



    private Socket socket;


    private Emotion activeEmotion;


    public GameAdapterGeneric(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connectToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(host, port);
                    socket.setKeepAlive(true);
                    outputStream = new PrintStream(socket.getOutputStream());
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    notifyObserversConnectionEstablished();
                    while(true) {
                        String line = null;
                        try {
                            line = bufferedReader.readLine();
                            interpretResponse(line);
                        } catch (IOException e) {
                            notifyObserversConnectionError();
                        }
                    }
                } catch (IOException e) {
                    notifyObserversConnectionFailed();
                }
            }
        }).start();
    }

    /**
     * Decomposes and interprets server messages.
     * HB: heartbeat
     * emotion <Emotion>: user feels <Emotion>
     * @param response
     */
    private void interpretResponse(String response) {
        //EmotionEngine sends heartbeat packages just to check client is still there. Ignore them
        if(response.equals("HB"))
            return;
        else if(response.startsWith("emotion")) {
            String[] responseSplitted = response.split(" ");
            for( Emotion emotion : Emotion.values()) {
                if( responseSplitted[1].equals(emotion.name())) {
                    activeEmotion = emotion;
                    notifyObserversDataArrived();
                    break;
                }
            }
        }
    }

    /**
     * TODO Since we are planning to use training sessions I do not think this method is neccessary. Use openTrainingSession instead
     * Sends a signal to indicate predetermined label of scene.
     * @param emotion
     */
    public void sendEmotionSignal(Emotion emotion) {
        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                outputStream.println("getemotion");
                return null;
            }
        });
    }

    /**
     * Suggests openning a training session for specified emotion.
     * This call must be followed by closeTrainingSession to indicate the end of this session
     * @param emotion
     */
    public void openTrainingSession(final Emotion emotion) {
        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                outputStream.println("train " + emotion.name());
                return null;
            }
        });
    }

    /**
     * Suggests closing current training session.
     * This call must be preceeded by openTrainingSession
     */
    public void closeTrainingSession() {
        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                outputStream.println("stop");
                return null;
            }
        });
    }

    /**
     * Indicates that specified emotion should have been stimulated n milliseconds ago.
     * @param emotion
     * @param n time elapsed since stimulation in milliseconds
     */
    public void trainLastNMilliseconds(final Emotion emotion, final int n) {
        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                outputStream.println("ntrain " + emotion.name() + " " + n);
                return null;
            }
        });
    }

    /**
     * Requests current emotion and returns immediately. When a response from Emotion Engine arrives, observers are notified.
     */
    public void requestEmotion() {
        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                outputStream.println("getemotion");
                return null;
            }
        });
    }

    /**
     *
     * @return Returns the most recently received emotion from Emotion Engine
     */
    public Emotion getStoredEmotion() {
        return activeEmotion;
    }

    public void registerObserver(GameAdapterObserver observer) {
        observerCollection.add(observer);
    }

    public void notifyObserversConnectionEstablished() {
        System.out.println("Connection Established");
        for( GameAdapterObserver observer : observerCollection)
            observer.connectionEstablished(this);
    }

    public void notifyObserversConnectionFailed() {
        System.out.println("Connection Failed");
        for( GameAdapterObserver observer : observerCollection)
            observer.connectionFailed(this);
    }

    public void notifyObserversConnectionError() {
        System.out.println("Connection error");
        for( GameAdapterObserver observer : observerCollection)
            observer.connectionError(this);
    }

    public void notifyObserversDataArrived() {
        System.out.println("Data arrived");
        for( GameAdapterObserver observer : observerCollection)
            observer.dataArrived(this);
    }


}
