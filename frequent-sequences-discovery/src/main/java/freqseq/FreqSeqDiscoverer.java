package freqseq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import freqseq.conf.Config;
import freqseq.utils.Statistics;
import freqseq.utils.TimeUtils;
import freqseq.utils.Utils;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class FreqSeqDiscoverer {

	private final static String DEFAULT_LOG_PROPS_FILE_NAME = "./conf/logging.properties";
	private static Logger logger;

	static {
		String logPropFileName = System.getProperty("java.util.logging.config.file");
		if (logPropFileName == null || logPropFileName.isBlank() || !(new File(logPropFileName)).exists())
			logPropFileName = DEFAULT_LOG_PROPS_FILE_NAME;
		File logPropFile = new File(logPropFileName);
		if (logPropFile.exists())
			System.setProperty("java.util.logging.config.file", logPropFileName);
		logger = Logger.getLogger(FreqSeqDiscoverer.class.getName());
	}

	public static void main(String[] args) throws Exception {

		String discoveryMode = Config.getDiscoveryMode();
		if ("init".equalsIgnoreCase(discoveryMode)) {
			Statistics.setStartEvNamesExtractTime(System.currentTimeMillis());

			processInitMode();

			Statistics.setEndEvNamesExtractTime(System.currentTimeMillis());
			long execTime = Statistics.getEvNamesExtractDuration();
			logger.log(Level.INFO, "Time duration is {0} ms [{1}]",
					new Object[] { execTime, TimeUtils.toString(execTime) });
		} else if ("discovery".equalsIgnoreCase(discoveryMode)) {
			Statistics.setStartDiscoveryTime(System.currentTimeMillis());

			List<Event> freqEvents = getFreqEvents();
			indexFreqEvents(freqEvents);
			Map<String, Event> evName2evtMap = new HashMap<>();
			for (Event ev : freqEvents) {
				String evName = ev.getName();
				evName2evtMap.put(evName, ev);
			}
			putEventOccurrences(evName2evtMap);
			generateFreqEpisodes(evName2evtMap);

			Statistics.setEndDiscoveryTime(System.currentTimeMillis());
			long execTime = Statistics.getDiscoveryDuration();
			logger.log(Level.INFO, "Time duration is {0} ms [{1}]",
					new Object[] { execTime, TimeUtils.toString(execTime) });
		} else {
			logger.log(Level.SEVERE, "The algorithm has two modes: \"INIT\" or \"DISCOVERY\". The mode {0} is unknown.",
					discoveryMode);
			System.exit(-1);
		}
	}

	private static void generateFreqEpisodes(Map<String, Event> evName2evMap) throws Exception {
		logger.log(Level.INFO, "Generating frequent episodes (subsequences) ... ");

		Map<String, PrintWriter> evName2writerMap = createOutputWriter(evName2evMap);
		File freqEvNameSetsInDir = Config.getFreqEvNameSetsInDir();

		for (String evName : evName2evMap.keySet()) {
			EpisodeTree episodeTree = generateFreqEpisodes(evName, evName2evMap, freqEvNameSetsInDir);
			PrintWriter writer = evName2writerMap.get(evName);
			episodeTree.printEpisodes(writer);
		}

		closeOutputWriter(evName2writerMap);

		logger.log(Level.INFO, "Frquent episodes are discovered and saved in the directory {0}.",
				Config.getFreqEpisodesOutDir());
	}

	private static EpisodeTree generateFreqEpisodes(String evName, Map<String, Event> evName2evMap, File inputDir)
			throws Exception {
		int minSup = getAbsoluteMinSup();
		EpisodeTree episodeTree = new EpisodeTree(minSup);
		BufferedReader reader = new BufferedReader(new FileReader(inputDir + File.separator + evName));

		String line = reader.readLine();
		while (line != null) {
			// int freq = Integer.parseInt(line.split(":")[1]);
			String[] evNames = line.split(":")[0].split(",");
			List<Event> events = new ArrayList<>();
			for (String name : evNames) {
				Event ev = evName2evMap.get(name);
				events.add(ev);
			}
			episodeTree.addEvents(events);
			line = reader.readLine();
		}
		reader.close();
		return episodeTree;
	}

	private static void putEventOccurrences(Map<String, Event> evName2evMap) throws Exception {
		BufferedReader seqReader = new BufferedReader(new FileReader(Config.getSequencesInFile()));

		CSVFormat csvFormat = Config.getCSVFormat();
		Iterable<CSVRecord> records = csvFormat.parse(seqReader);
		int counter = 0;
		for (CSVRecord record : records) {
			String[] evNames = record.values();
			counter += 1;
			putEventOccurrences(counter, evNames, evName2evMap);
		}
		seqReader.close();
		Statistics.setSequenceCount(counter);

		logger.log(Level.INFO, "Frequent events are associated with their occurrences in the sequences.");
	}

	private static void putEventOccurrences(int seqId, String[] evNames, Map<String, Event> evName2evMap) {
		Map<String, Integer> evName2count = new HashMap<>();
		for (int i = 0; i < evNames.length; i++) {
			String nName = Utils.normalizeName(evNames[i]);
			Integer count = evName2count.get(nName);
			if (count == null)
				count = 0;
			evName2count.put(nName, count + 1);
			String evName = Utils.suffix(nName, count);
			if (evName2evMap.containsKey(evName)) {
				Event ev = evName2evMap.get(evName);
				EventOccurrence evOccur = new EventOccurrence(seqId, i);
				ev.addEventOccurrence(evOccur);

				if (count > 0 && !ev.hasPrevRepetition()) {
					String prevEvName = Utils.suffix(nName, count - 1);
					Event prevEv = evName2evMap.get(prevEvName);
					ev.setPrevRepetition(prevEv);
					prevEv.setNextRepetition(ev);
				}
			}
		}
	}

	private static void indexFreqEvents(List<Event> freqEvents) {
		Collections.sort(freqEvents);
		for (int i = 0; i < freqEvents.size(); i++) {
			Event ev = freqEvents.get(i);
			ev.setIndex(i);
		}
	}

	private static List<Event> getFreqEvents() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(Config.getFreqEvNamesInFile()));

		List<Event> freqEvents = new ArrayList<>();
		String line = reader.readLine();
		while (line != null) {
			String[] sLine = line.split(",");
			Event ev = new Event(sLine[0], Integer.parseInt(sLine[1]));
			freqEvents.add(ev);
			line = reader.readLine();
		}

		reader.close();
		logger.log(Level.INFO, "Frequent events are loaded from the file {0}", Config.getFreqEvNamesInFile());
		return freqEvents;
	}

	private static Map<String, PrintWriter> createOutputWriter(Map<String, Event> evName2evtMap) throws Exception {
		File freqEpisodesOutDir = Config.getFreqEpisodesOutDir();

		Map<String, PrintWriter> evName2writerMap = new HashMap<>();
		PrintWriter writer;

		if (Config.outputFilePerFreqEvent() == true) {
			for (String evName : evName2evtMap.keySet()) {
				writer = new PrintWriter(freqEpisodesOutDir + File.separator + evName);
				evName2writerMap.put(evName, writer);
			}
		} else {
			writer = new PrintWriter(freqEpisodesOutDir + File.separator + "freqEpisodes");
			for (String evName : evName2evtMap.keySet())
				evName2writerMap.put(evName, writer);
		}

		return evName2writerMap;
	}

	private static void closeOutputWriter(Map<String, PrintWriter> evName2writerMap) {
		for (PrintWriter writer : evName2writerMap.values())
			writer.close();
	}

	private static void processInitMode() throws Exception {
		BufferedReader seqReader = new BufferedReader(new FileReader(Config.getSequencesInFile()));
		PrintWriter writerOfEvNames = new PrintWriter(Config.getEvNameSetsOutFile());

		CSVFormat csvFormat = Config.getCSVFormat();
		Iterable<CSVRecord> records = csvFormat.parse(seqReader);
		int counter = 0;
		for (CSVRecord record : records) {
			String[] evNames = record.values();
			counter += 1;
			extractAndWriteEvNames(counter, evNames, writerOfEvNames);
		}

		seqReader.close();
		writerOfEvNames.close();
		logger.log(Level.INFO, "Event names are extracted and saved in {0}.", Config.getEvNameSetsOutFile());
	}

	private static void extractAndWriteEvNames(int seqId, String[] evNames, PrintWriter writerOfEvNames) {
		Map<String, Integer> evName2count = new HashMap<>();
		for (int i = 0; i < evNames.length; i++) {
			String nName = Utils.normalizeName(evNames[i]);
			Integer count = evName2count.get(nName);
			if (count == null)
				count = 0;
			evName2count.put(nName, count + 1);
			String evName = Utils.suffix(nName, count);
			writerOfEvNames.print(evName);
			if (i < evNames.length - 1)
				writerOfEvNames.print(",");
			else
				writerOfEvNames.println();
		}
	}

	private static int getAbsoluteMinSup() {
		int minSup = Math.round(Config.getRelativeMinSupport() * Statistics.getSequenceCount() / 100);
		Statistics.setAbsoluteMinimumSupport(minSup);
		return minSup;
	}
}
