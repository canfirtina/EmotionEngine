package gameadapter;

/**
 * Created by Mustafa on 25.4.2015.
 */
public interface GameAdapterObserver {
    /**
     * Called when new emotion data is available for client to use
     * @param adapter is the GameAdapter that has new data
     */
    public void dataArrived( GameAdapterGeneric adapter);

    /**
     * Called when a connection error occurs
     * @param adapter is the GameAdapter which has the connection error
     */
    public void connectionError( GameAdapterGeneric adapter);

    /**
     * Called when the connection of an adapter is established
     * @param adapter is the GameAdapter whose connection is established
     */
    public void connectionEstablished( GameAdapterGeneric adapter);

    /**
     * Called when the pending connection is failed
     * @param adapter is the GameAdapter whose connection is failed
     */
    public void connectionFailed(GameAdapterGeneric adapter);
}
