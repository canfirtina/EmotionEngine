package emotionlearner;

import weka.core.FastVector;


/**
* class for properties of a feature extractor
*/
public class FeatureExtractorProperties{
   /**
	* number of channels 
	*/
   private final int numChannels;

   /**
	* number of features for each channel
	*/
   private final int numFeaturesEachChannel;

   /**
	* weka feature attributes
	*/
   private FastVector featureAttributes;

   /**
	* constructor
	* @param numChannels
	* @param numFeaturesEachChannel
	* @param featureAttributes 
	*/
   public FeatureExtractorProperties(int numChannelss, int numFeaturesEachChannels, FastVector featureAttributess) {
	   numChannels = numChannelss;
	   numFeaturesEachChannel = numFeaturesEachChannels;
	   featureAttributes = featureAttributess;
   }


   public int getNumChannels() {
	   return numChannels;
   }

   public int getNumFeaturesEachChannel() {
	   return numFeaturesEachChannel;
   }

   public FastVector getFeatureAttributes() {
	   return featureAttributes;
   }
   
   public void setFeatureAttributes(FastVector attr){
	   featureAttributes = attr;
   }

}
