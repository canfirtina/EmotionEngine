package communicator;

import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import emotionlearner.engine.EmotionEngine;
import shared.Emotion;

/**
 * Handles communication between client and server processes
 */
public class Communicator {
    public static final int port = 9997;
    private static final int READ_TIMEOUT = 2000;
    private static final String HEART_BEAT = "HB";

    /**
     * Executor for sending non-blocking message to client
     */
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Server socket
     */
    private static ServerSocket server;
    /**
     * Adapter connected to this EmotionEngine
     */
    private static Socket adapter;


    /**
     * InputStream from adapter to Communicator
     */
    private static InputStream inputStream;

    /**
     * BufferedReader to wrap InputStream object
     */
    private static BufferedReader bufferedReader;

    /**
     * Sends messages to adapter
     */
    private static PrintStream os;


    /**
     * EmotionEngine reference
     */
    private static EmotionEngine ee;

    /**
     * Starts a server that constantly listens for requests coming from clients
     */
    public static void startServer() {

        ee = EmotionEngine.sharedInstance(null);

        // Try to open a server socket on port specified port
        // Note that we can't choose a port less than 1023 if we are not
        // privileged users (root)
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void waitClient() {
        // Create a socket object from the ServerSocket to listen and accept
        // connections.
        // Open input and output streams
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Waiting for client");

                    adapter = server.accept();
                    adapter.setKeepAlive(true);

                    System.out.println("Client connected");
                    inputStream = adapter.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    os = new PrintStream(adapter.getOutputStream());

                    //Get message, parse it and do what it requires
                    while (true) {

                        try {
                            String line = bufferedReader.readLine();

                            //if end of stream
                            if (line == null)
                                throw new IOException();

                            //echo back what is told
                            os.println(line);

                            parseAndExecuteRequest(line);


                        } catch (IOException e) {
                            onClientDisconnected();
                            return;
                        }
                    }
                } catch (IOException e) {
                    onClientDisconnected();
                }
            }
        }).start();
    }

    /**
     * Parse the message
     * type <name>: adapter provides its type
     * startclassification adapter wants to know emotional state starting from now
     * stopclassification adapter no longer wants to know emotional state
     * getemotion: adapter wants to know current emotion
     * train <Emotion>: adapter advises EmotionEngine to open training session for <Emotion>
     * stop: adapter advises to close current training session
     * cancel: adapter requests cancellation of current training session if there is any
     * ntrain <Emotion> <Milliseconds>: adapter indicates that <Emotion> should have been triggered <Milliseconds> ago
     *
     * @param line
     */
    private static void parseAndExecuteRequest(final String line) {
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                String request[] = line.split(" ");

                if (request[0].equals("getemotion")) {
                    Emotion em = ee.currentEmotion();
                    if (em != null)
                        provideEmotionalState(em);
                } else if (request[0].equals("train")) {
                    //Find the emotion in request and start training with that
                    for (Emotion em : Emotion.values()) {
                        if (em.name().equals(request[1])) {
                            ee.openTrainingSession(em);
                            break;
                        }
                    }
                } else if( request[0].equals("stop")) {
                    ee.closeTrainingSession();
                } else if (request[0].equals("ntrain")) {
                    //find the emotion in enum
                    for (Emotion em : Emotion.values()) {
                        if (em.name().equals(request[1])) {
                            ee.trainLastNMilliseconds(Integer.parseInt(request[2]), em);
                        }
                    }
                } else if (request[0].equals("cancel")) {
                    ee.cancelTrainingSession();
                } else if (request[0].equals("startclassification")) {
                    ee.openClassifySession();
                } else if (request[0].equals("stopclassification")) {
                    ee.closeClassifySession();
                } else if (request[0].equals("type")) {
                    //TODO there is no method provided in emotion engine for this
                }
                return null;
            }
        });

    }

    /**
     * Serializes and sends the given label to the client
     *
     * @param emotion The label containing the information about the user
     */
    public static void provideEmotionalState(final Emotion emotion) {
        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                sendMessage("emotion " + emotion.name());
                return null;
            }
        });

    }

    private static void sendMessage(final String s) {
        os.println(s);
    }

    /**
     * Makes thread wait for another client
     */
    private static void onClientDisconnected() {
        ee.stopClassifySession();
        ee.closeTrainingSession();
        waitClient();
    }
}
