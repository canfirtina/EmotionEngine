package communicator;

import java.io.*;
import java.net.*;

import EmotionLearner.EmotionEngine;
import SharedSensorData.Label;

public class Communicator {
	public static final int port = 9999;

	// grafikte ��ks�n diye
	EmotionEngine ee = new EmotionEngine();


	public static void startServer() {
		/*
		 * Buralar internetten. d�zelt.
		 */

		// declaration section:
		// declare a server socket and a client socket for the server
		// declare an input and an output stream
		ServerSocket echoServer = null;
		String line;
		DataInputStream is;
		PrintStream os;
		Socket clientSocket = null;

		// Try to open a server socket on port 9999
		// Note that we can't choose a port less than 1023 if we are not
		// privileged users (root)
		try {
			echoServer = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println(e);
		}

		// Create a socket object from the ServerSocket to listen and accept
		// connections.
		// Open input and output streams
		try {
			clientSocket = echoServer.accept();
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			// As long as we receive data, echo that data back to the client.
			while (true) {
				line = is.readLine();
				os.println(line);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void checkForRequest() {
		String request = "gelen request";

		switch (request) {
		case "start training":
			ee.openTrainingSession(null); break;
		case "stop training":
			ee.closeTrainingSession();
		case "start classifying":
			ee.openClassifySession();
		case "stop classifying":
			ee.stopClassifySession();
		case "get emotional status":
			provideEmotionalState(ee.currentEmotion());
		}
	}
	
	public void provideEmotionalState(Label label){
		//serialize and send the label to the client.
	}
}