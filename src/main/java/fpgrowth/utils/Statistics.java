package fpgrowth.utils;

import java.util.List;

import fpgrowth.Item;

/**
 * 
 * @author Nuhad.Shaabani
 *
 */
public class Statistics {

	private static long startExecutionTime;
	private static long endExecutionTime;

	private static int itemCount;
	private static int recordCount;
	private static int absoluteMinSup;
	private static int unfreqItemCount;
	private static int freqItemCount;

	private static String unfrequentItems;
	private static String frequentItems;

	public static void setStartExecutionTime(long startTime) {
		Statistics.startExecutionTime = startTime;
	}

	public static long getStartExecutionTime() {
		return startExecutionTime;
	}

	public static void setEndExecutionTime(long endTime) {
		Statistics.endExecutionTime = endTime;
	}

	public static long getEndExecutionTime() {
		return endExecutionTime;
	}

	public static long getExecutionTime() {
		return getEndExecutionTime() - getStartExecutionTime();
	}

	public static void setItemCount(int itemCount) {
		Statistics.itemCount = itemCount;
	}

	public static int getItemCount() {
		return itemCount;
	}

	public static void setRecordCount(int recordCount) {
		Statistics.recordCount = recordCount;
	}

	public static int getRecordCount() {
		return recordCount;
	}

	public static void setAbsoluteMinimumSupport(int absoluteMinSup) {
		Statistics.absoluteMinSup = absoluteMinSup;
	}

	public static int getAbsoluteMinimumSupport() {
		return absoluteMinSup;
	}

	public static void setUnfreqItemCount(int unfreqItemCount) {
		Statistics.unfreqItemCount = unfreqItemCount;
	}

	public static int getUnfreqItemCount() {
		return unfreqItemCount;
	}

	public static void setFreqItemCount(int freqItemCount) {
		Statistics.freqItemCount = freqItemCount;
	}

	public static int getFreqItemCount() {
		return freqItemCount;
	}

	public static void setUnfrequentItems(List<String> unfrequentItems) {
		StringBuffer sb = new StringBuffer();
		for (String item : unfrequentItems) {
			sb.append(item);
			sb.append(",");
		}
		if (sb.length() > 0)
			sb.replace(sb.length() - 1, sb.length(), "");
		Statistics.unfrequentItems = sb.toString();
	}

	public static String getUnfrequentItems() {
		return unfrequentItems;
	}

	public static void setFrequentItems(List<Item> freqItemList) {
		if (freqItemList.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (Item item : freqItemList) {
				sb.append(item.getName());
				sb.append(",");
			}
			sb.replace(sb.length() - 1, sb.length(), "");
			Statistics.frequentItems = sb.toString();
		}
	}

	public static String getFrequentItems() {
		return frequentItems;
	}
}
