package freqepisodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class Episode {

	private List<Event> events;
	private Set<Integer> sequenceIds;
	private Set<Event> evRepetiTracking;

	public Episode(Event ev) {
		events = new ArrayList<>();
		events.add(ev);
		sequenceIds = new HashSet<>(ev.getSequences());
		evRepetiTracking = new HashSet<>();
		addToEvRepetiTracking(ev);
	}

	private Episode(List<Event> events, Set<Integer> sequenceIds, Set<Event> prevTracking) {
		this.events = events;
		this.sequenceIds = sequenceIds;
		this.evRepetiTracking = new HashSet<>(prevTracking);
	}

	private void addToEvRepetiTracking(Event ev) {
		if (!ev.hasNextRepetition() && ev.hasPrevRepetition()) {
			evRepetiTracking.add(ev);
		} else if (ev.hasNextRepetition() && ev.hasPrevRepetition()) {
			Event nEv = ev.getNextRepetition();
			evRepetiTracking.remove(nEv);
			evRepetiTracking.add(ev);
		} else if (ev.hasNextRepetition() && !ev.hasPrevRepetition()) {
			Event nEv = ev.getNextRepetition();
			evRepetiTracking.remove(nEv);
		}
	}

	public boolean isValid() {
		return evRepetiTracking.isEmpty();
	}

	public Set<Integer> getSequencesIds() {
		return sequenceIds;
	}

	public List<Event> getEvents() {
		return events;
	}

	public int getFreq() {
		return sequenceIds.size();
	}

	public int getNumberOfEvents() {
		return events.size();
	}

	public Event getEvent(int index) {
		return events.get(index);
	}

	public List<Episode> generateFreqEpisodes(Event ev, int minSup) {
		List<Episode> genEpisodes = new ArrayList<>();
		Set<Integer> commonSequences = new HashSet<>(ev.getSequences());
		commonSequences.retainAll(this.sequenceIds);
		if (commonSequences.size() < minSup)
			return genEpisodes;
		generateFreqEpisodeForFirst(ev, genEpisodes, minSup);
		for (int i = 1; i < events.size(); i++)
			generateFreqEpisode(i - 1, ev, i, genEpisodes, minSup);
		generateFreqEpisodeForLast(ev, genEpisodes, minSup);
		return genEpisodes;
	}

	private void generateFreqEpisode(int leftIdx, Event ev, int rightIdx, List<Episode> genEpisodes, int minSup) {
		Event leftEv = getEvent(leftIdx);
		Event rightEv = getEvent(rightIdx);
		Set<Integer> commonSeqIds = new HashSet<>(sequenceIds);
		commonSeqIds.retainAll(ev.getSequences());
		if (commonSeqIds.size() >= minSup) {
			Set<Integer> discardedSeqIds = new HashSet<>();
			for (Integer seqId : commonSeqIds) {
				EventOccurrence leftEvOccur = leftEv.getEventOccurrence(seqId);
				EventOccurrence rightEvOccur = rightEv.getEventOccurrence(seqId);
				EventOccurrence evOccur = ev.getEventOccurrence(seqId);
				if (!evOccur.isAfter(leftEvOccur) || !evOccur.isBefore(rightEvOccur))
					discardedSeqIds.add(seqId);
				if (commonSeqIds.size() - discardedSeqIds.size() < minSup)
					break;
			}
			if (commonSeqIds.size() - discardedSeqIds.size() >= minSup) {
				commonSeqIds.removeAll(discardedSeqIds);
				List<Event> nextEvents = new ArrayList<>();
				nextEvents.addAll(events.subList(0, rightIdx));
				nextEvents.add(ev);
				nextEvents.addAll(rightIdx + 1, events.subList(rightIdx, events.size()));
				Episode newEpi = new Episode(nextEvents, commonSeqIds, this.evRepetiTracking);
				newEpi.addToEvRepetiTracking(ev);
				genEpisodes.add(newEpi);
			}
		}
	}

	private void generateFreqEpisodeForFirst(Event ev, List<Episode> genEpisodes, int minSup) {
		Event firstEv = getEvent(0);
		Set<Integer> commonSeqIds = new HashSet<>(sequenceIds);
		commonSeqIds.retainAll(ev.getSequences());
		if (commonSeqIds.size() >= minSup) {
			Set<Integer> discardedSeqIds = new HashSet<>();
			for (Integer seqId : commonSeqIds) {
				EventOccurrence firstEvOccur = firstEv.getEventOccurrence(seqId);
				EventOccurrence evOccur = ev.getEventOccurrence(seqId);
				if (!evOccur.isBefore(firstEvOccur))
					discardedSeqIds.add(seqId);
				if (commonSeqIds.size() - discardedSeqIds.size() < minSup)
					break;
			}
			if (commonSeqIds.size() - discardedSeqIds.size() >= minSup) {
				commonSeqIds.removeAll(discardedSeqIds);
				List<Event> nextEvents = new ArrayList<>();
				nextEvents.add(ev);
				nextEvents.addAll(1, events);
				Episode newEpi = new Episode(nextEvents, commonSeqIds, this.evRepetiTracking);
				newEpi.addToEvRepetiTracking(ev);
				genEpisodes.add(newEpi);
			}
		}
	}

	private void generateFreqEpisodeForLast(Event ev, List<Episode> genEpisodes, int minSup) {
		Event lastEv = getEvent(getNumberOfEvents() - 1);
		Set<Integer> commonSeqIds = new HashSet<>(sequenceIds);
		commonSeqIds.retainAll(ev.getSequences());
		if (commonSeqIds.size() >= minSup) {
			Set<Integer> discardedSeqIds = new HashSet<>();
			for (Integer seqId : commonSeqIds) {
				EventOccurrence lastEvOccur = lastEv.getEventOccurrence(seqId);
				EventOccurrence evOccur = ev.getEventOccurrence(seqId);
				if (!evOccur.isAfter(lastEvOccur))
					discardedSeqIds.add(seqId);
				if (commonSeqIds.size() - discardedSeqIds.size() < minSup)
					break;
			}
			if (commonSeqIds.size() - discardedSeqIds.size() >= minSup) {
				commonSeqIds.removeAll(discardedSeqIds);
				List<Event> nextEvents = new ArrayList<>();
				nextEvents.addAll(events);
				nextEvents.add(ev);
				Episode newEpi = new Episode(nextEvents, commonSeqIds, this.evRepetiTracking);
				newEpi.addToEvRepetiTracking(ev);
				genEpisodes.add(newEpi);
			}
		}
	}

	public String getName() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < events.size() - 1; i++) {
			Event ev = events.get(i);
			sb.append(ev.toString());
			sb.append(",");
		}
		sb.append(events.get(events.size() - 1));
		sb.append(":");
		sb.append(getFreq());
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < events.size() - 1; i++) {
			Event ev = events.get(i);
			sb.append(ev.getPrimeName());
			sb.append(",");
		}
		sb.append(events.get(events.size() - 1).getPrimeName());
		sb.append(":");
		sb.append(getFreq());
		return sb.toString();
	}
}
