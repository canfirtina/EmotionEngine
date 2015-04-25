package test;

import gameadapter.GameAdapterGeneric;
import gameadapter.GameAdapterObserver;

/**
 * Created by Mustafa on 25.4.2015.
 */
public class CaseGameAdapter {
    public static void runtest1() {
        GameAdapterObserver obs = new GameAdapterObserver() {
            @Override
            public void dataArrived(GameAdapterGeneric adapter) {
                System.out.println(adapter.getStoredEmotion());
            }

            @Override
            public void connectionError(GameAdapterGeneric adapter) {
                System.out.println("connection error on " + adapter.toString());
            }

            @Override
            public void connectionEstablished(GameAdapterGeneric adapter) {
                System.out.println("connection established on " + adapter.toString());
            }

            @Override
            public void connectionFailed(GameAdapterGeneric adapter) {
                System.out.println("connection failed on " + adapter.toString());
            }
        };
        GameAdapterGeneric ga = new GameAdapterGeneric("127.0.0.1",9999);
        ga.registerObserver(obs);
        ga.connectToServer();


    }

}
