package fpgrowth;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class Item implements Comparable<Item> {

	private String name;
	private int count;
	private int idx;

	public Item(String name) {
		this.name = name;
		this.count = 0;
	}

	public void increaseCountBy(int count) {
		this.count += count;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public void setIndex(int idx) {
		this.idx = idx;
	}

	public int getIndex() {
		return idx;
	}

	@Override
	public int compareTo(Item o) {
		if (Integer.compare(this.getCount(), o.getCount()) != 0)
			return Integer.compare(this.getCount(), o.getCount());
		else
			return getName().compareTo(o.getName());
	}

	@Override
	public String toString() {
		return name;
	}

	public void printItem() {
		System.out.println(getName() + ":" + getCount());
	}
}
