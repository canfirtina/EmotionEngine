package persistentdatamanagement;

/**
 * Provides method for notifying registered observes of changes in state.
 */
public interface DataManagerObserver {
	/**
	 * Notifies its observers when the status changes.
	 * @param manager
	 */
	public void notify(DataManager manager);
}
