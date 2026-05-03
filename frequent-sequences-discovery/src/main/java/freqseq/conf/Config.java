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

	private static String freqSeqDiscPropFileName;
	private static File freqSeqDiscPropFile;
	private static Properties freqSeqDiscProps;
	
	static {
		freqSeqDiscPropFileName = DEFAULT_FREQ_SEQ_PROPS_FNAME;
		freqSeqDiscPropFile = new File(freqSeqDiscPropFileName);
		loadConfiguration();
	}
	
	public static void loadConfiguration() {
		freqSeqDiscProps = new Properties();
		
		try {
			freqSeqDiscProps.load(new FileInputStream(freqSeqDiscPropFile));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}

	public static int getRelativeMinimumSupport() {
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

}
