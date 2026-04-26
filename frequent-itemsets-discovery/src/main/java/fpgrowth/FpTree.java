package fpgrowth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Nuhad.Shaabani
 * 
 */
public class FpTree {

	private int minSup;
	private TreeNode treeRoot;
	private LinkedList<HeaderNode> headerList;

	private boolean cleared;

	public FpTree(int minSup, List<Item> freqItemsList) {
		this.minSup = minSup;
		cleared = false;
		treeRoot = new TreeNode();
		headerList = new LinkedList<>();
		for (int i = freqItemsList.size() - 1; i >= 0; i--)
			headerList.add(new HeaderNode(freqItemsList.get(i)));
	}

	public void addRecord(List<Item> record) {
		addRecord(record, 0, treeRoot);
	}

	private void addRecord(List<Item> record, int currentIdx, TreeNode currentNode) {
		if (currentIdx < record.size()) {
			Item item = record.get(currentIdx);
			if (item == null)
				addRecord(record, ++currentIdx, currentNode);
			else {
				if (currentNode.hasChild(item)) {
					currentNode = currentNode.getChild(item);
					addRecord(record, currentIdx, currentNode);
				} else if (item.equals(currentNode.getItem())) {
					currentNode.increaseTnCountBy(1);
					addRecord(record, ++currentIdx, currentNode);
				} else {
					currentNode = new TreeNode(item, currentNode);
					HeaderNode hNode = headerList.get(currentIdx);
					hNode.addTreeNode(currentNode);
					addRecord(record, ++currentIdx, currentNode);
				}
			}
		}
	}

	public FpTree createNextCondtionalFpTree() {
		clear();
		FpTree nextFpTree = new FpTree(this);
		clearLastHeader();
		nextFpTree.clearEmptyHeader();
		return nextFpTree;
	}

	private FpTree(FpTree fpTree) {
		minSup = fpTree.getMinSup();
		treeRoot = new TreeNode();
		List<HeaderNode> previousHList = fpTree.getHeaderList();
		headerList = new LinkedList<>();
		for (int i = 0; i < previousHList.size() - 1; i++) {
			HeaderNode prevHNode = previousHList.get(i);
			HeaderNode hNode = new HeaderNode(prevHNode.getItem());
			hNode.increaseHnCountBy(-1 * prevHNode.getItem().getCount());
			headerList.add(hNode);
		}
		fpTree.computeCondItemCounts(headerList);
		fpTree.addCondRecords(this);
	}

	private boolean isCleared() {
		return cleared;
	}

	private void clear() {
		if (isCleared() == false) {
			for (HeaderNode hNode : headerList) {
				List<TreeNode> tNodes = hNode.getTreeNodes();
				for (TreeNode tNode : tNodes)
					tNode.clearChildren();
			}
			cleared = true;
		}
	}

	private void clearLastHeader() {
		HeaderNode lastHNode = headerList.removeLast();
		lastHNode.clearTreeNodes();
		clearEmptyHeader();
	}

	private void clearEmptyHeader() {
		while (headerList.size() > 0 && headerList.getLast().isEmpty())
			headerList.removeLast();
	}

	private void computeCondItemCounts(List<HeaderNode> nextHeaderList) {
		HeaderNode lastHNode = headerList.getLast();
		List<TreeNode> tNodes = lastHNode.getTreeNodes();
		for (TreeNode tNode : tNodes) {
			TreeNode currentTNode = tNode;
			while (currentTNode.hasParent()) {
				currentTNode = currentTNode.getParent();
				if (!currentTNode.isRootNode()) {
					Item currentItem = currentTNode.getItem();
					HeaderNode hNodeInNext = nextHeaderList.get(currentItem.getIndex());
					hNodeInNext.increaseHnCountBy(tNode.getTnCount());
				}
			}
		}
	}

	private void addCondRecords(FpTree nextTree) {
		List<HeaderNode> nextHList = nextTree.getHeaderList();
		HeaderNode lastHNode = headerList.getLast();
		List<TreeNode> tNodes = lastHNode.getTreeNodes();
		for (TreeNode tNode : tNodes) {
			TreeNode currentTNode = tNode;
			List<Item> record = new ArrayList<>();
			while (currentTNode.hasParent()) {
				currentTNode = currentTNode.getParent();
				if (currentTNode.isRootNode() == false) {
					Item currentItem = currentTNode.getItem();
					HeaderNode currentHNode = nextHList.get(currentItem.getIndex());
					int itemCount = currentHNode.getHnCount();
					if (itemCount >= minSup)
						record.add(currentItem);
				}
			}
			addCondRecord(nextTree, record, tNode.getTnCount());
		}
	}

	private void addCondRecord(FpTree nextTree, List<Item> record, int count) {
		List<HeaderNode> nextHList = nextTree.getHeaderList();
		TreeNode currentNode = nextTree.treeRoot;
		for (int i = record.size() - 1; i >= 0; i--) {
			Item currentItem = record.get(i);
			if (currentItem == currentNode.getItem()) {
				currentNode.increaseTnCountBy(count);
			} else if (currentNode.hasChild(currentItem)) {
				currentNode = currentNode.getChild(currentItem);
				currentNode.increaseTnCountBy(count);
			} else {
				currentNode = new TreeNode(currentItem, count, currentNode);
				HeaderNode currentHNode = nextHList.get(currentItem.getIndex());
				currentHNode.addTreeNode(currentNode);
			}
		}
	}

	public boolean hasHeaderToProcess() {
		return headerList.size() > 0;
	}

	public HeaderNode getNextHeaderNode() {
		return headerList.getLast();
	}

	public boolean isSinglePath() {
		for (HeaderNode headerNode : headerList)
			if (headerNode.isTreeNodeCountEqualOne() == false)
				return false;
		return true;
	}

	public List<TreeNode> getSinglePath() {
		if (!isSinglePath())
			return null;
		List<TreeNode> singlePath = new ArrayList<>();
		for (HeaderNode hNode : headerList) {
			TreeNode tNode = hNode.getFirstTreeNode();
			singlePath.add(new TreeNode(tNode.getItem(), tNode.getTnCount()));
		}
		return singlePath;
	}

	public int getMinSup() {
		return minSup;
	}

	public List<HeaderNode> getHeaderList() {
		return headerList;
	}

	public boolean isEmptyTree() {
		return isHeaderEmpty();
	}

	public boolean isHeaderEmpty() {
		return headerList.size() == 0;
	}
	
	public void printHeaderList() {
		System.out.println(headerList);
	}

	public void printHeader() {
		if (isHeaderEmpty()) {
			System.out.println("The header is empty.");
		}
		for (HeaderNode headerNode : headerList) {
			if (!headerNode.isEmpty())
				System.out.println(headerNode);
		}
	}

	public void printLeafToRootPathes() {
		for (HeaderNode headerNode : headerList) {
			List<TreeNode> treeNodes = headerNode.getTreeNodes();
			for (TreeNode node : treeNodes) {
				TreeNode currentNode = node;
				StringBuffer sb = new StringBuffer();
				while (currentNode.hasParent()) {
					sb.append(currentNode.toString());
					if (currentNode.hasParent()) {
						sb.append(" -> ");
					}
					currentNode = currentNode.getParent();
				}
				sb.append(currentNode.toString());
				System.out.println(sb.toString());
			}
		}
	}
}
