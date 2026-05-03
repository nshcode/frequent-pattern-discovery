package freqseq.conf;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class Config {

	private static Logger logger = Logger.getLogger(Config.class.getName());

	private final static String DEFAULT_FREQ_SEQ_PROPS_FNAME = "conf/freqSeqDiscovery.properties";

	private static String freqSeqDiscPropFName;
	private static File freqSeqDiscPropFile;
	private static Properties freqSeqDiscProps;

	static {
		freqSeqDiscPropFName = System.getProperty("freqSeqDiscPropFileName");
		if (freqSeqDiscPropFName == null || freqSeqDiscPropFName.isBlank() || !exists(freqSeqDiscPropFName))
			freqSeqDiscPropFName = DEFAULT_FREQ_SEQ_PROPS_FNAME;

		freqSeqDiscPropFile = new File(freqSeqDiscPropFName);
		if (!freqSeqDiscPropFile.exists()) {
			logger.log(Level.SEVERE,
					getMessage("the properties file", freqSeqDiscPropFile.getAbsolutePath(), "does not exist!"));
			System.exit(-1);
		}

		loadConfiguration();
		checkProperties();
	}

	public static void loadConfiguration() {
		freqSeqDiscProps = new Properties();
		try {
			freqSeqDiscProps.load(new FileInputStream(freqSeqDiscPropFile));
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					getMessage("a problem occurs during loding", freqSeqDiscPropFile.getAbsolutePath(), "!"), e);
			System.exit(-1);
		}
	}

	public static int getRelativeMinSupport() {
		String key = "relativeMinSup";
		int minSup = Integer.parseInt(freqSeqDiscProps.getProperty(key).trim());
		return minSup;
	}

	public static String getDiscoveryMode() {
		String key = "discoveryMode";
		return freqSeqDiscProps.getProperty(key);
	}

	public static File getSequencesInFile() {
		String key = "sequencesInputFile";
		return new File(freqSeqDiscProps.getProperty(key));
	}

	public static File getEvNameSetsOutFile() {
		String key = "eventNameSetsOutputFile";
		return new File(freqSeqDiscProps.getProperty(key));
	}

	public static File getFreqEvNamesInFile() {
		String key = "freqEventNamesInputFile";
		return new File(freqSeqDiscProps.getProperty(key));
	}

	public static File getFreqEvNameSetsInDir() {
		String key = "freqEventNameSetsInputDir";
		return new File(freqSeqDiscProps.getProperty(key));
	}

	public static File getFreqEpisodesOutDir() {
		String key = "freqEpisodesOutputDir";
		return new File(freqSeqDiscProps.getProperty(key));
	}

	public static boolean outputFilePerFreqEvent() {
		String key = "outputFilePerFreqEvent";
		return "yes".equalsIgnoreCase(freqSeqDiscProps.getProperty(key).trim());
	}

	public static CSVFormat getCSVFormat() {
		Builder builder = CSVFormat.DEFAULT.builder();

		boolean skipHeade = freqSeqDiscProps.getProperty("skipHeader").equalsIgnoreCase("yes") ? true : false;
		builder.setSkipHeaderRecord(skipHeade);

		String delimiter = freqSeqDiscProps.getProperty("delimiter");
		builder.setDelimiter(delimiter.charAt(0));

		String quote = freqSeqDiscProps.getProperty("quote");
		if (quote == null || quote.isEmpty())
			logger.log(Level.WARNING, "the property qutoe is undefined!");
		else if (quote.length() > 1)
			logger.log(Level.WARNING, "the property qutoe consists of more than one character! It ignored.");
		else
			builder.setQuote(quote.charAt(0));

		String escape = freqSeqDiscProps.getProperty("escape");
		if (escape == null || escape.isEmpty())
			logger.log(Level.WARNING, "the property escape is undefined!");
		else if (escape.length() > 1)
			logger.log(Level.WARNING, "the property escape consists of more than one character! It ignored.");
		else
			builder.setEscape(escape.charAt(0));

		CSVFormat csvFormat = builder.get();
		return csvFormat;
	}

	private static void checkProperties() {
		boolean error = false;

		String key = "discoveryMode";
		String mode = freqSeqDiscProps.getProperty(key);
		if (mode == null || mode.isEmpty()) {
			logger.log(Level.SEVERE, " the algorithm mode is not defined.");
			error = true;
		}

		if (!"init".equalsIgnoreCase(mode) && !"discovery".equalsIgnoreCase(mode)) {
			logger.log(Level.SEVERE, "the algorithm mode must \"either\" init or \"discovery\".");
			error = true;
		}

		key = "sequencesInputFile";
		if (!exists(freqSeqDiscProps.getProperty(key))) {
			logger.log(Level.SEVERE,
					getMessage("the input dataset", freqSeqDiscProps.getProperty(key), "does not exist!"));
			error = true;
		}
		key = "freqEpisodesOutputDir";
		if (!exists(freqSeqDiscProps.getProperty(key))) {
			logger.log(Level.SEVERE,
					getMessage("the output directory", freqSeqDiscProps.getProperty(key), "does not exist!"));
			error = true;
		}
		key = "relativeMinSup";
		try {
			Integer.parseInt(freqSeqDiscProps.getProperty(key).trim());
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE,
					"the relative minimum support must be an integer number! E.g. 50 (means minSup = 50%)");
			error = true;
		}
		String delimiter = freqSeqDiscProps.getProperty("delimiter");
		if (delimiter == null || delimiter.isEmpty() || delimiter.length() != 1) {
			logger.log(Level.SEVERE, "the delimiter is undefined or consists of more than one char!");
			error = true;
		}
		if (error)
			System.exit(-1);
	}

	private static boolean exists(String fileName) {
		return (new File(fileName)).exists();
	}

	private static String getMessage(String s, String value, String e) {
		StringBuffer sb = new StringBuffer();
		sb.append(s);
		sb.append(" ");
		sb.append(value);
		sb.append(" ");
		sb.append(e);
		return sb.toString();
	}

}
