package fpgrowth.conf;

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

	private final static String DEFAULT_FPGROWTH_PROPS_FILE_NAME = "conf/fpGrowth.properties";

	private static String fpGrowthPropFileName;
	private static File fpGrowthPropFile;
	private static Properties fpGrowthProps;

	static {
		fpGrowthPropFileName = System.getProperty("fpGrowthPropFileName");

		if (fpGrowthPropFileName == null || fpGrowthPropFileName.isBlank() || !exists(fpGrowthPropFileName))
			fpGrowthPropFileName = DEFAULT_FPGROWTH_PROPS_FILE_NAME;

		fpGrowthPropFile = new File(fpGrowthPropFileName);
		if (!fpGrowthPropFile.exists()) {
			logger.log(Level.SEVERE,
					getMessage("the fpGroth properties file", fpGrowthPropFile.getAbsolutePath(), "does not exist!"));
			System.exit(-1);
		}

		loadConfiguration();
		checkProperties();
	}

	public static void loadConfiguration() {
		fpGrowthProps = new Properties();
		try {
			fpGrowthProps.load(new FileInputStream(fpGrowthPropFile));
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					getMessage("a problem occurs during loding the file", fpGrowthPropFile.getAbsolutePath(), "!"), e);
			System.exit(-1);
		}
	}

	public static String getDatasetName() {
		String key = "datasetName";
		return fpGrowthProps.getProperty(key);
	}

	public static int getRelativeMinimumSupport() {
		String key = "relativeMinSup";
		int minSup = Integer.parseInt(fpGrowthProps.getProperty(key).trim());
		return minSup;
	}

	public static File getInputDataFile() {
		String key = "inputDataFile";
		return new File(fpGrowthProps.getProperty(key));
	}

	public static File getOutputDataDirectory() {
		String key = "outputDataDir";
		return new File(fpGrowthProps.getProperty(key));
	}

	public static boolean outputFilePerFrequentItem() {
		String key = "outputFilePerFrequentItem";
		return "yes".equalsIgnoreCase(fpGrowthProps.getProperty(key).trim());
	}

	public static CSVFormat getCSVFormat() {
		Builder builder = CSVFormat.DEFAULT.builder();

		boolean skipHeade = fpGrowthProps.getProperty("skipHeader").equalsIgnoreCase("yes") ? true : false;
		builder.setSkipHeaderRecord(skipHeade);

		String delimiter = fpGrowthProps.getProperty("delimiter");
		builder.setDelimiter(delimiter.charAt(0));

		String quote = fpGrowthProps.getProperty("quote");
		if (quote == null || quote.isEmpty())
			logger.log(Level.WARNING, "the property qutoe is undefined!");
		else if (quote.length() > 1)
			logger.log(Level.WARNING, "the property qutoe consists of more than one character! It ignored.");
		else
			builder.setQuote(quote.charAt(0));

		String escape = fpGrowthProps.getProperty("escape");
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
		String key = "inputDataDir";
		key = "inputDataFile";
		if (!exists(fpGrowthProps.getProperty(key))) {
			logger.log(Level.SEVERE,
					getMessage("the input dataset", fpGrowthProps.getProperty(key), "does not exist!"));
			error = true;
		}
		key = "outputDataDir";
		if (!exists(fpGrowthProps.getProperty(key))) {
			logger.log(Level.SEVERE,
					getMessage("the output directory", fpGrowthProps.getProperty(key), "does not exist!"));
			error = true;
		}
		key = "relativeMinSup";
		try {
			Integer.parseInt(fpGrowthProps.getProperty(key).trim());
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE,
					"the relative minimum support must be an integer number! E.g. 50 (means minSup = 50%)");
			error = true;
		}
		String delimiter = fpGrowthProps.getProperty("delimiter");
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
