package freqseq.utils;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class Statistics {

	private static int sequenceCount;
	private static int absoluteMinSup;

	private static long startEvNamesExtractTime;
	private static long endEvNamesExtractTime;

	private static long startDiscoveryTime;
	private static long endDiscoveryTime;

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

	public static void setStartEvNamesExtractTime(long startTime) {
		startEvNamesExtractTime = startTime;
	}

	public static long getStartEvNamesExtractTime() {
		return startEvNamesExtractTime;
	}

	public static void setEndEvNamesExtractTime(long endTime) {
		startEvNamesExtractTime = endTime;
	}

	public static long getEndEvNamesExtractTime() {
		return endEvNamesExtractTime;
	}

	public static long getEvNamesExtractDuration() {
		return getEndEvNamesExtractTime() - getStartEvNamesExtractTime();
	}

	public static void setStartDiscoveryTime(long startTime) {
		startDiscoveryTime = startTime;
	}

	public static long getStartDiscoveryTime() {
		return startDiscoveryTime;
	}

	public static void setEndDiscoveryTime(long endTime) {
		endDiscoveryTime = endTime;
	}

	public static long getEndDiscoveryTime() {
		return endDiscoveryTime;
	}

	public static long getDiscoveryDuration() {
		return getStartDiscoveryTime() - getEndDiscoveryTime();
	}
}
