package PersistentDataManagement;

public interface DataManagerObserver {
	/**
	 * Notifies its observers when the status changes
	 * @param manager
	 */
	public void notify(DataManager manager);
}
