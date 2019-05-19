import java.io.Serializable;

/*
Title: RMIT Database Systems Assignment 2
Developer(s): 
- Rudi Basiran <s3665980@student.rmit.edu.au> 
Date Created: 19 May 2019 
Description: BTree Class
Notes: B+ Tree has internal and external nodes
Change History:
 */

public class BTree<TKey extends Comparable<TKey>, TValue> implements Serializable {
	private BTreeNode<TKey> root;

	public BTree() {
		this.root = new BTreeLeafNode<TKey, TValue>();
	}

	// insert key & value
	public void insert(TKey key, TValue value) {

		System.out.println("Adding key/value: " + key + "/" + value);
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		leaf.insertKey(key, value);

		if (leaf.isOverflow()) {
			BTreeNode<TKey> n = leaf.dealOverflow();
			if (n != null)
				this.root = n;
		}
	}

	// search whether key exists
	public TValue search(TKey key) {

		System.out.println("Searching key: " + key);
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);

		int index = leaf.search(key);
		return (index == -1) ? null : leaf.getValue(index);
	}

	// delete key & valueif exists
	public void delete(TKey key) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);

		if (leaf.delete(key) && leaf.isUnderflow()) {
			BTreeNode<TKey> n = leaf.dealUnderflow();
			if (n != null)
				this.root = n;
		}
	}

	// search leaf node to see whether key exists

	@SuppressWarnings("unchecked")
	private BTreeLeafNode<TKey, TValue> findLeafNodeShouldContainKey(TKey key) {
		BTreeNode<TKey> node = this.root;
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((BTreeInnerNode<TKey>) node).getChild(node.search(key));
		}

		return (BTreeLeafNode<TKey, TValue>) node;
	}
}