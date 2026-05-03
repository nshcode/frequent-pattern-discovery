package freqseq;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class EventOccurrence {

	private int seqId;
	private int position;

	public EventOccurrence(int seqId, int position) {
		this.seqId = seqId;
		this.position = position;
	}

	public int getSeqId() {
		return seqId;
	}

	public int getPosition() {
		return position;
	}

	public boolean hasSameSeq(EventOccurrence occurrence) {
		return this.getSeqId() == occurrence.getSeqId();
	}

	public boolean hasSamePosition(EventOccurrence occurrence) {
		return this.getPosition() == occurrence.getPosition();
	}

	public boolean isBefore(EventOccurrence occurrence) {
		return hasSameSeq(occurrence) && getPosition() < occurrence.getPosition();
	}

	public boolean isAfter(EventOccurrence occurrence) {
		return hasSameSeq(occurrence) && getPosition() > occurrence.getPosition();
	}

	public boolean hasSameOccurrence(EventOccurrence occurrence) {
		return hasSameSeq(occurrence) && hasSamePosition(occurrence);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(getSeqId());
		sb.append(",");
		sb.append(getPosition());
		sb.append(")");
		return sb.toString();
	}
}
