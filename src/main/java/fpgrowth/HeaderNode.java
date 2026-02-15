package fpgrowth;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Nuhad.Shaabani
 * 
 */
public class HeaderNode {

	private Item item;
	private int hnCount;
	private TreeNode firstNode;
	private TreeNode lastNode;

	public HeaderNode(Item item) {
		this.item = item;
		hnCount = item.getCount();
		//hnCount = 0;
	}

	public void addTreeNode(TreeNode treeNode) {
		if (firstNode == null) {
			firstNode = treeNode;
			lastNode = firstNode;
		} else {
			lastNode.setNextNode(treeNode);
			lastNode = treeNode;
		}
	}

	public void increaseHnCountBy(int count) {
		this.hnCount += count;
	}

	public Item getItem() {
		return item;
	}

	public int getHnCount() {
		return hnCount;
	}

	public TreeNode getFirstTreeNode() {
		return firstNode;
	}

	public TreeNode getLastTreeNode() {
		return lastNode;
	}

	public List<TreeNode> getTreeNodes() {
		List<TreeNode> treeNodes = new ArrayList<>();
		TreeNode currentNode = getFirstTreeNode();
		while (currentNode != null) {
			treeNodes.add(currentNode);
			currentNode = currentNode.getNextNode();
		}
		return treeNodes;
	}

	public void removeTreeNodes() {
		firstNode = null;
		lastNode = null;
	}

	public boolean isTreeNodeCountEqualOne() {
		return getFirstTreeNode() == getLastTreeNode();

	}
	
	public void clearTreeNodes() {
		TreeNode currentNode = getFirstTreeNode();
		while (currentNode != null) {
			currentNode.removeParent();
			currentNode = currentNode.getNextNode();
		}
		firstNode = null;
		lastNode = null;
	}
	
	public boolean isEmpty() {
		return firstNode == null;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(getItem().getName());
		sb.append(", ");
		sb.append(getHnCount());
		sb.append("] -> ");
		TreeNode currentNode = getFirstTreeNode();
		while (currentNode != null) {
			sb.append(currentNode.toString());
			currentNode = currentNode.getNextNode();
			if (currentNode != null) {
				sb.append(" -> ");
			}
		}
		return sb.toString();
	}
}
