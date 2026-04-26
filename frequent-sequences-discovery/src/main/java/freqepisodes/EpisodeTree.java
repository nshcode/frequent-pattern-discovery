package freqepisodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class EpisodeTree {

	private static int minSup;

	private EpisodeTree root;
	private Event event;
	private List<EpisodeTree> children;
	private List<Episode> episodes;

	public EpisodeTree(int minFreq) {
		EpisodeTree.minSup = minFreq;
	}

	public void addEvents(List<Event> events) {
		// System.out.println(events);
		// sortEvents(events);
		if (root == null) {
			root = new EpisodeTree(events.get(0));
			addEvents(events, 1, root);
			// root.prinEpisodes();
		} else {
			addEvents(events, 0, root);
		}
	}
	
	private void addEvents(List<Event> events, int currentIdx, EpisodeTree currentNode) {
		if (currentIdx < events.size() && currentNode.hasEpisodes()) {
			Event ev = events.get(currentIdx);
			if (ev.equals(currentNode.getEvent())) {
				addEvents(events, ++currentIdx, currentNode);
			} else if (currentNode.hasChild(ev)) {
				currentNode = currentNode.getChild(ev);
				addEvents(events, currentIdx, currentNode);
			} else {
				EpisodeTree newNode = currentNode.makeChild(ev);
				addEvents(events, ++currentIdx, newNode);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void sortEvents(List<Event> events) {
		// Collections.sort(events);
	}

	private EpisodeTree(Event ev) {
		event = ev;
		children = new ArrayList<>();
		episodes = new ArrayList<>();
		episodes.add(new Episode(ev));
	}

	private EpisodeTree() {
		children = new ArrayList<>();
	}

	private void setEvent(Event ev) {
		event = ev;
	}

	private void setEpisodes(List<Episode> allGeneratedEpisodes) {
		episodes = allGeneratedEpisodes;
	}

	private void addChild(EpisodeTree child, Event ev) {
		if (children.size() <= ev.getIndex())
			for (int i = children.size(); i <= ev.getIndex(); i++)
				children.add(null);
		children.set(ev.getIndex(), child);
	}

	private EpisodeTree makeChild(Event ev) {
		EpisodeTree newNode = new EpisodeTree();
		newNode.setEvent(ev);
		List<Episode> allGeneratedEpis = new ArrayList<>();
		for (Episode epi : getEpisodes()) {
			List<Episode> freqEpis = epi.generateFreqEpisodes(ev, minSup);
			allGeneratedEpis.addAll(freqEpis);
		}
		if (allGeneratedEpis.size() > 0) {
			newNode.setEpisodes(allGeneratedEpis);
		}
		addChild(newNode, ev);
		return newNode;
	}

	private List<Episode> getEpisodes() {
		return episodes;
	}

	private boolean hasEpisodes() {
		return episodes != null;
	}

	private boolean hasChild(Event ev) {
		int idx = ev.getIndex();
		return idx < children.size() && children.get(idx) != null;
	}

	private EpisodeTree getChild(Event ev) {
		int idx = ev.getIndex();
		if (hasChild(ev) == false)
			return null;
		return children.get(idx);
	}

	private Event getEvent() {
		return event;
	}

	private List<EpisodeTree> getChildren() {
		List<EpisodeTree> childrenListWithoutNulls = new ArrayList<>();
		for (int i = 0; i < children.size(); i++) {
			EpisodeTree child = children.get(i);
			if (child != null)
				childrenListWithoutNulls.add(child);
		}
		return childrenListWithoutNulls;
	}

	public List<Episode> collectEpisodes() {
		List<Episode> episodes = new ArrayList<>();
		LinkedList<EpisodeTree> nodesToProcess = new LinkedList<>();
		nodesToProcess.addLast(root);
		while (!nodesToProcess.isEmpty()) {
			EpisodeTree nextNode = nodesToProcess.removeFirst();
			if (nextNode.hasEpisodes()) {
				List<Episode> nextEpisodes = nextNode.getEpisodes();
				for (Episode epi : nextEpisodes)
					if (epi.isValid())
						episodes.add(epi);
				nodesToProcess.addAll(0, nextNode.getChildren());
			}
		}
		return episodes;
	}

	public void printEpisodes(PrintWriter writer) {
		LinkedList<EpisodeTree> nodesToProcess = new LinkedList<>();
		nodesToProcess.addLast(root);
		while (!nodesToProcess.isEmpty()) {
			EpisodeTree nextNode = nodesToProcess.removeFirst();
			if (nextNode.hasEpisodes()) {
				List<Episode> nextEpisodes = nextNode.getEpisodes();
				for (Episode epi : nextEpisodes)
					if (epi.isValid())
						writer.println(epi);
				nodesToProcess.addAll(0, nextNode.getChildren());
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Episode epi : episodes) {
			sb.append(epi.toString());
			sb.append(',');
		}
		return sb.toString();
	}

	public void prinEpisodes() {
		System.out.println(this);
	}
}
