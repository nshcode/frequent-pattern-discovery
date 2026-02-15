package fpgrowth;

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

import org.apache.commons.csv.CSVRecord;

import fpgrowth.conf.Config;
import fpgrowth.utils.Statistics;
import fpgrowth.utils.TimeUtil;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class FpGrowth {

	private final static String DEFAULT_LOG_PROPS_FILE_NAME = "./conf/logging.properties";
	private static Logger logger;
	static {
		String logPropFileName = System.getProperty("java.util.logging.config.file");
		if (logPropFileName == null || logPropFileName.isBlank() || !(new File(logPropFileName)).exists())
			logPropFileName = DEFAULT_LOG_PROPS_FILE_NAME;
		File logPropFile = new File(logPropFileName);
		if (logPropFile.exists())
			System.setProperty("java.util.logging.config.file", logPropFileName);
		logger = Logger.getLogger(FpGrowth.class.getName());
	}

	public static void main(String[] args) throws Exception {
		Statistics.setStartExecutionTime(System.currentTimeMillis());

		Map<String, Item> att2ItemMap = createAndCountItems();
		logger.log(Level.INFO, "number of records: {0}", Statistics.getRecordCount());
		logger.log(Level.INFO, "number of items: {0}", Statistics.getItemCount());

		removeUnfrequentItems(att2ItemMap);
		logger.log(Level.INFO, "absolute minimum support: {0}", Statistics.getAbsoluteMinimumSupport());
		logger.log(Level.INFO, "number of unsupported items: {0}", Statistics.getUnfreqItemCount());
		logger.log(Level.INFO, "number of supported items: {0}", Statistics.getFreqItemCount());

		List<Item> freqItemList = indexAndGetFreqItems(att2ItemMap);
		logger.log(Level.INFO, "frequent items: {0}", Statistics.getFrequentItems());

		Map<Item, PrintWriter> item2writerMap = createOutputWriter(freqItemList);
		FpTree fpTree = createFpTree(att2ItemMap, freqItemList);
		generateFreqSets(fpTree, item2writerMap);

		Statistics.setEndExecutionTime(System.currentTimeMillis());
		long execTime = Statistics.getExecutionTime();
		logger.log(Level.INFO, "total execution time: {0} ms [{1}]",
				new Object[] { execTime, TimeUtil.toString(execTime) });
	}

	private static void generateFreqSets(FpTree fpTree, Map<Item, PrintWriter> item2writerMap) throws Exception {
		PrintWriter printer;
		while (fpTree.hasHeaderToProcess()) {
			HeaderNode hNode = fpTree.getNextHeaderNode();
			logger.log(Level.INFO, "Generating sets for the item {0} ...", hNode.getItem().getName());
			printer = item2writerMap.get(hNode.getItem());
			StringBuffer sb = new StringBuffer(hNode.getItem().getName());
			printer.println(sb.toString() + ":" + hNode.getHnCount());
			FpTree condFpTree = fpTree.createNextCondtionalFpTree();
			generateFreqSets(condFpTree, sb, printer);
			printer.flush();
		}
		closeOutputWriter(item2writerMap);
	}

	private static void generateFreqSets(FpTree fpTree, StringBuffer currentSb, PrintWriter printer) {
		StringBuffer prevSb = currentSb;
		while (fpTree.hasHeaderToProcess()) {
			HeaderNode hNode = fpTree.getNextHeaderNode();
			StringBuffer sb = new StringBuffer(currentSb);
			sb.append(",");
			sb.append(hNode.getItem().getName());
			printer.println(sb.toString() + ":" + hNode.getHnCount());
			FpTree condFpTree = fpTree.createNextCondtionalFpTree();
			generateFreqSets(condFpTree, sb, printer);
			currentSb = prevSb;
		}
	}

	private static FpTree createFpTree(Map<String, Item> att2ItemMap, List<Item> freqItemList) throws Exception {
		int minSup = getAbsoluteMinSup();
		FpTree fpTree = new FpTree(minSup, freqItemList);
		FileReader reader = new FileReader(Config.getInputDataFile());
		Iterable<CSVRecord> records = Config.getCSVFormat().parse(reader);
		for (CSVRecord record : records) {
			List<Item> items = new ArrayList<>();
			for (String attValue : record) {
				if (att2ItemMap.containsKey(attValue)) {
					Item item = att2ItemMap.get(attValue);
					if (items.size() <= item.getIndex())
						for (int i = items.size(); i <= item.getIndex(); i++)
							items.add(null);
					items.set(item.getIndex(), item);
				}
			}
			fpTree.addRecord(items);
		}
		reader.close();
		return fpTree;
	}

	private static List<Item> indexAndGetFreqItems(Map<String, Item> attValue2ItemMap) {
		List<Item> freqItemList = new ArrayList<>(attValue2ItemMap.values());
		Collections.sort(freqItemList);
		for (int i = 0; i < freqItemList.size(); i++) {
			Item item = freqItemList.get(i);
			item.setIndex(freqItemList.size() - 1 - i);
		}
		Statistics.setFrequentItems(freqItemList);
		return freqItemList;
	}

	private static void removeUnfrequentItems(Map<String, Item> att2ItemMap) {
		List<String> unfreqAtts = new ArrayList<>();
		int minSup = getAbsoluteMinSup();
		for (String att : att2ItemMap.keySet()) {
			Item item = att2ItemMap.get(att);
			if (item.getCount() < minSup)
				unfreqAtts.add(att);
		}
		for (String att : unfreqAtts)
			att2ItemMap.remove(att);

		Statistics.setFreqItemCount(att2ItemMap.size());
		Statistics.setUnfreqItemCount(unfreqAtts.size());
		Statistics.setUnfrequentItems(unfreqAtts);
	}

	private static Map<String, Item> createAndCountItems() throws Exception {
		Map<String, Item> att2ItemMap = new HashMap<>();
		FileReader reader = new FileReader(Config.getInputDataFile());
		Iterable<CSVRecord> records = Config.getCSVFormat().parse(reader);
		int recordCount = 0;
		for (CSVRecord record : records) {
			recordCount += 1;
			for (String att : record) {
				Item item = att2ItemMap.get(att);
				if (item == null) {
					item = new Item(att);
					att2ItemMap.put(att, item);
				}
				item.increaseCountBy(1);
			}
		}
		reader.close();
		Statistics.setRecordCount(recordCount);
		Statistics.setItemCount(att2ItemMap.size());
		return att2ItemMap;
	}

	private static int getAbsoluteMinSup() {
		int minSup = Math.round(Config.getRelativeMinimumSupport() * Statistics.getRecordCount() / 100);
		Statistics.setAbsoluteMinimumSupport(minSup);
		return minSup;
	}

	private static Map<Item, PrintWriter> createOutputWriter(List<Item> freqItemList) throws Exception {
		Map<Item, PrintWriter> item2writerMap = new HashMap<>();
		PrintWriter writer;
		File outputDir = Config.getOutputDataDirectory();
		if (Config.outputFilePerFrequentItem() == true)
			for (Item item : freqItemList) {
				writer = new PrintWriter(outputDir + File.separator + new File(item.getName()));
				item2writerMap.put(item, writer);
			}
		else {
			writer = new PrintWriter(outputDir + File.separator + new File(Config.getDatasetName() + "FreqSets"));
			for (Item item : freqItemList)
				item2writerMap.put(item, writer);
		}
		return item2writerMap;
	}

	private static void closeOutputWriter(Map<Item, PrintWriter> item2writerMap) {
		for (PrintWriter writer : item2writerMap.values())
			writer.close();
	}
}
