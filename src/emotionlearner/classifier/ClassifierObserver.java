package emotionlearner.classifier;

/**
 * Classifier uses this observer to notify 
 * @author aliyesilyaprak
 */
public interface ClassifierObserver {
	/**
	 * used when ensemble classifier classifies an instance
	 * @param classifier 
	 */
	public void classifyNotification(EnsembleClassifier classifier);
	
	/**
	 * used when ensemble classifier trains instances
	 * @param classifier 
	 */
	public void trainNotification(EnsembleClassifier classifier);
}
