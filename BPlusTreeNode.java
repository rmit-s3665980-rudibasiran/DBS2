import java.io.Serializable;

enum TreeNodeType {
	InnerNode, LeafNode
}

public class BPlusTreeNode implements Serializable {

	protected Object[] keys;
	protected int keyCount;
	protected BPlusTreeNode parentNode;
	protected BPlusTreeNode leftSibling;
	protected BPlusTreeNode rightSibling;

	public BPlusTreeNode() {
		this.keyCount = 0;
		this.parentNode = null;
		this.leftSibling = null;
		this.rightSibling = null;
	}

	public int getKeyCount() {
		return this.keyCount;
	}

}