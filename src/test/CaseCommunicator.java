package test;

import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.net.Socket;

/**
 * Created by Mustafa on 23.4.2015.
 */
public class CaseCommunicator {
    public static void run1() {
        try {
            Socket socket = new Socket("127.0.0.1",9999);
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            dos.writeUTF("ssssss\n");
            System.out.println(dis.readLine());

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
