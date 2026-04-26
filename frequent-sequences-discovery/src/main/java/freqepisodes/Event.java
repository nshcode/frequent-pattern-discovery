package freqepisodes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import freqepisodes.utils.Utils;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class Event implements Comparable<Event> {

	private String name;
	private int idx;
	private int freq;
	private Map<Integer, EventOccurrence> occurrences;
	private Set<Integer> sequences;

	private Event prevRepetition;
	private Event nextRepetition;

	public Event(String name, int freq) {
		this.name = name;
		this.freq = freq;
		occurrences = new HashMap<>();
		sequences = new HashSet<>();
	}

	public void setPrevRepetition(Event prevRepetition) {
		this.prevRepetition = prevRepetition;
	}

	public void setNextRepetition(Event nextRepetition) {
		this.nextRepetition = nextRepetition;
	}

	public void setIndex(int idx) {
		this.idx = idx;
	}

	public void addEventOccurrence(EventOccurrence occurrnce) {
		sequences.add(occurrnce.getSeqId());
		occurrences.put(occurrnce.getSeqId(), occurrnce);
	}

	public int getFreq() {
		return freq;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return idx;
	}

	public Event getPrevRepetition() {
		return prevRepetition;
	}

	public boolean hasPrevRepetition() {
		return prevRepetition != null;
	}

	public boolean hasNextRepetition() {
		return nextRepetition != null;
	}

	public Event getNextRepetition() {
		return nextRepetition;
	}

	public EventOccurrence getEventOccurrence(Integer seqId) {
		return occurrences.get(seqId);
	}

	public Set<Integer> getSequences() {
		return sequences;
	}

	public Map<Integer, EventOccurrence> getEventOccurrences() {
		return occurrences;
	}
	
	public String getPrimeName() {
		return Utils.removeSuffix(name);
	}

	@Override
	public int compareTo(Event o) {
		if (Integer.compare(this.getFreq(), o.getFreq()) != 0)
			return Integer.compare(this.getFreq(), o.getFreq());
		else
			return getName().compareTo(o.getName());
	}

	@Override
	public String toString() {
		return name;
	}
}
