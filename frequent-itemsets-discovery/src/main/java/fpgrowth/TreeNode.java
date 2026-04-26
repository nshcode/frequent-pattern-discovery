package fpgrowth;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class TreeNode {

	private Item item;
	private int tnCount;
	private TreeNode parent;
	private TreeNode nextNode;
	private List<TreeNode> children;

	public TreeNode() {
		tnCount = 0;
		children = new ArrayList<>();
	}

	public TreeNode(Item item) {
		this.item = item;
		this.tnCount = 1;
		children = new ArrayList<>();
	}

	public TreeNode(Item item, TreeNode parent) {
		this(item);
		parent.addChild(this);
	}

	public TreeNode(Item item, int count) {
		this.item = item;
		this.tnCount = count;
		children = new ArrayList<>();
	}

	public TreeNode(Item item, int count, TreeNode parent) {
		this(item, count);
		parent.addChild(this);
	}

	public void addChild(TreeNode child) {
		Item item = child.getItem();
		if (children.size() <= item.getIndex())
			for (int i = children.size(); i <= item.getIndex(); i++)
				children.add(null);
		children.set(item.getIndex(), child);
		child.parent = this;
	}

	public boolean hasChild(Item item) {
		int idx = item.getIndex();
		return idx < children.size() && children.get(idx) != null;
	}

	public TreeNode getChild(Item item) {
		int idx = item.getIndex();
		if (!hasChild(item))
			return null;
		return children.get(idx);
	}

	public void clearChildren() {
		children.clear();
		children = null;
	}

	public void increaseTnCountBy(int count) {
		this.tnCount += count;
	}

	public Item getItem() {
		return item;
	}

	public int getTnCount() {
		return tnCount;
	}

	public boolean hasParent() {
		return getParent() != null;
	}

	public void removeParent() {
		parent = null;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setNextNode(TreeNode nextNode) {
		this.nextNode = nextNode;
	}

	public TreeNode getNextNode() {
		return nextNode;
	}

	public boolean isRootNode() {
		return getItem() == null;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		if (hasParent()) {
			sb.append(getItem().getName());
		} else {
			sb.append("Root");
		}

		sb.append(", ");
		sb.append(getTnCount());
		sb.append(")");
		return sb.toString();

	}
}
