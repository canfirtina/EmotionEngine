package communicator;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import emotionlearner.EmotionEngine;
import shared.Emotion;
import shared.Label;

/**
 * Handles communication between client and server processes
 */
public class Communicator {
	public static final int port = 9999;

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
	 * Gets messages from adapter
	 */
	private static DataInputStream is;

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
		/*
		 * Buralar internetten. duzelt.
		 */

		ee= EmotionEngine.sharedInstance(null);
		// declaration section:
		// declare a server socket and a client socket for the server
		// declare an input and an output stream


		Socket clientSocket = null;

		// Try to open a server socket on port 9999
		// Note that we can't choose a port less than 1023 if we are not
		// privileged users (root)
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println(e);
		}

		// Create a socket object from the ServerSocket to listen and accept
		// connections.
		// Open input and output streams
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("waiitng for client");
					adapter = server.accept();
					System.out.println("client connected");
					is = new DataInputStream(adapter.getInputStream());
					os = new PrintStream(adapter.getOutputStream());

					//Get message, parse it and do what it requires
					while (true) {
						String line = is.readLine();
						os.println(line);
						parseAndExecuteRequest(line);
					}
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}).start();

	}

	/**
	 * Parse the message
	 * getemotion: adapter wants to know current emotion
	 * train <Emotion>: adapter advises EmotionEngine to open training session for <Emotion>
	 * stop: adapter advises to close current training session
	 * ntrain <Emotion> <Milliseconds>: adapter indicates that <Emotion> should have been triggered <Milliseconds> ago
	 * @param line
	 */
	private static void parseAndExecuteRequest(final String line) {
		executorService.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				String request[] = line.split(" ");

				if(request[0].equals("getemotion")) {
					sendEmotionToClient(ee.currentEmotion());

				} else if(request[0].equals("train")) {

					//Find the emotion in request and start training with that
					for(Emotion em : Emotion.values()) {
						if(em.name().equals(request[1])) {
							ee.openTrainingSession(em);
							break;
						}
					}

				} else if(request[0].equals("ntrain")) {
					//find the emotion in enum
					for(Emotion em: Emotion.values()) {
						if(em.name().equals(request[1])) {
							ee.trainLastNMilliseconds(Integer.parseInt(request[2]),em);
						}
					}
				}
				return null;
			}
		});

	}

	/**
	 * Convert Emotion object to string message and send it to client
	 * @param emotion
	 */
	private static void sendEmotionToClient(final Emotion emotion) {

		sendMessage("emotion " + emotion.name());

	}

	/**
	 * Send message to client (blocking)
	 * @param str message to be sent
	 */
	private static synchronized void sendMessage(final String str) {
		os.println(str);
	}

	/**
	 * Listens to the port for new requests
	 */
	public static void checkForRequest() {
		int request = 1;

		switch (request) {
		case 1:
			ee.openTrainingSession(null); break;
		default: break;
		}
	}
	
	/**
	 * Serializes and sends the given label to the client
	 * @param emotion The label containing the information about the user
	 */
	public static void provideEmotionalState(final Emotion emotion){
		executorService.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				sendEmotionToClient(emotion);
				return null;
			}
		});

	}




}
