package freqseq.utils;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class Utils {

	public static String normalizeName(String name) {
		return name.trim().replaceAll("\s+", "_");
	}

	public static String suffix(String nName, Integer count) {
		return String.join("_", nName, String.valueOf(count));
	}
	
	public static String removeSuffix(String name) {
		int i = name.lastIndexOf('_');
		return name.substring(0, i);
	}
	
	
}
