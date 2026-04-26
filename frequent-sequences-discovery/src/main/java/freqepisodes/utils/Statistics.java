package freqepisodes.utils;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class Statistics {
	
	private static int sequenceCount;
	private static int absoluteMinSup;
	
	public static void setSequenceCount(int sequenceCount) {
		Statistics.sequenceCount = sequenceCount;
	}
	
	public static int getSequenceCount() {
		return sequenceCount;
	}
	
	public static void setAbsoluteMinimumSupport(int absoluteMinSup) {
		Statistics.absoluteMinSup = absoluteMinSup;
	}

	public static int getAbsoluteMinimumSupport() {
		return absoluteMinSup;
	}

}
