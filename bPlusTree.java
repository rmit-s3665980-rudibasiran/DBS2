import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class bPlusTree<K extends Comparable<? super K>, V> {



	private int branchingFactor;

	private Node root;

	public bPlusTree() {
		this(dbimpl.DEFAULT_BRANCHING_FACTOR);
	}

	public bPlusTree(int branchingFactor) {
		if (branchingFactor <= 2)
			throw new IllegalArgumentException("Illegal branching factor: "
					+ branchingFactor);
		this.branchingFactor = branchingFactor;
		root = new leafNode();
	}

	@SuppressWarnings("unchecked")
	public V search(K key) {
		return root.getValue(key);
	}

	@SuppressWarnings("unchecked")
	public void insert(K key, V value) {

		root.insertValue(key, value);
	}


	public void delete(K key) {
		root.deleteValue(key);
	}

	public String toString() {
		Queue<List<Node>> queue = new LinkedList<List<Node>>();
		queue.add(Arrays.asList(root));
		StringBuilder sb = new StringBuilder();
		while (!queue.isEmpty()) {
			Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
			while (!queue.isEmpty()) {
				List<Node> nodes = queue.remove();
				sb.append('{');
				Iterator<Node> it = nodes.iterator();
				while (it.hasNext()) {
					Node node = it.next();
					sb.append(node.toString());
					if (it.hasNext())
						sb.append(", ");
					if (node instanceof bPlusTree.intNode)
						nextQueue.add(((intNode) node).children);
				}
				sb.append('}');
				if (!queue.isEmpty())
					sb.append(", ");
				else
					sb.append('\n');
			}
			queue = nextQueue;
		}

		return sb.toString();
	}

	private abstract class Node {
		List<K> keys;

		int keyNumber() {
			return keys.size();
		}

		abstract V getValue(K key);

		abstract void deleteValue(K key);

		abstract void insertValue(K key, V value);

		abstract K getFirstLeafKey();

		abstract void merge(Node sibling);

		abstract Node split();

		abstract boolean isOverflow();

		abstract boolean isUnderflow();

		public String toString() {
			return keys.toString();
		}
	}

	private class intNode extends Node {
		List<Node> children;

		intNode() {
			this.keys = new ArrayList<K>();
			this.children = new ArrayList<Node>();
		}

		@Override
		V getValue(K key) {
			return getChild(key).getValue(key);
		}

		@Override
		void deleteValue(K key) {
			Node child = getChild(key);
			child.deleteValue(key);
			if (child.isUnderflow()) {
				Node childLeftSibling = getChildLeftSibling(key);
				Node childRightSibling = getChildRightSibling(key);
				Node left = childLeftSibling != null ? childLeftSibling : child;
				Node right = childLeftSibling != null ? child
						: childRightSibling;
				left.merge(right);
				deleteChild(right.getFirstLeafKey());
				if (left.isOverflow()) {
					Node sibling = left.split();
					insertChild(sibling.getFirstLeafKey(), sibling);
				}
				if (root.keyNumber() == 0)
					root = left;
			}
		}

		@Override
		void insertValue(K key, V value) {
			Node child = getChild(key);
			System.out.println("Adding key/value in intNode: " + key + "/" + value);
			child.insertValue(key, value);
			if (child.isOverflow()) {
				Node sibling = child.split();
				insertChild(sibling.getFirstLeafKey(), sibling);
			}
			if (root.isOverflow()) {
				Node sibling = split();
				intNode newRoot = new intNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}
		}

		@Override
		K getFirstLeafKey() {
			return children.get(0).getFirstLeafKey();
		}

	
		@Override
		void merge(Node sibling) {
			@SuppressWarnings("unchecked")
			intNode node = (intNode) sibling;
			keys.add(node.getFirstLeafKey());
			keys.addAll(node.keys);
			children.addAll(node.children);

		}

		@Override
		Node split() {
			System.out.println("Splitting intNode");
			int from = keyNumber() / 2 + 1, to = keyNumber();
			intNode sibling = new intNode();
			sibling.keys.addAll(keys.subList(from, to));
			sibling.children.addAll(children.subList(from, to + 1));

			keys.subList(from - 1, to).clear();
			children.subList(from, to + 1).clear();

			return sibling;
		}

		@Override
		boolean isOverflow() {
			return children.size() > branchingFactor;
		}

		@Override
		boolean isUnderflow() {
			return children.size() < (branchingFactor + 1) / 2;
		}

		Node getChild(K key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			return children.get(childIndex);
		}

		void deleteChild(K key) {
			int loc = Collections.binarySearch(keys, key);
			if (loc >= 0) {
				keys.remove(loc);
				children.remove(loc + 1);
			}
		}

		void insertChild(K key, Node child) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (loc >= 0) {
				children.set(childIndex, child);
			} else {
				keys.add(childIndex, key);
				children.add(childIndex + 1, child);
			}
		}

		Node getChildLeftSibling(K key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (childIndex > 0)
				return children.get(childIndex - 1);

			return null;
		}

		Node getChildRightSibling(K key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (childIndex < keyNumber())
				return children.get(childIndex + 1);

			return null;
		}
	}

	private class leafNode extends Node {
		List<V> values;
		leafNode next;

		leafNode() {
			keys = new ArrayList<K>();
			values = new ArrayList<V>();
		}

		@Override
		V getValue(K key) {

			for (int i = 0; i < keys.size(); i++) {
				
				String s = keys.get(i).toString(); 
				String k = key.toString();
				String v = values.get(i).toString();
				
				if (s.toLowerCase().contains(k.toLowerCase()) || v.toLowerCase().contains(k.toLowerCase())) {
					System.out.println("Found [" + k + "]: " + i + " / " + s + " / " + v);
				}
			}

			int loc = Collections.binarySearch(keys, key);
			return loc >= 0 ? values.get(loc) : null;
		}

		@Override
		void deleteValue(K key) {
			int loc = Collections.binarySearch(keys, key);
			if (loc >= 0) {
				keys.remove(loc);
				values.remove(loc);
			}
		}

		@Override
		void insertValue(K key, V value) {
			
			int loc = Collections.binarySearch(keys, key);
			int valueIndex = loc >= 0 ? loc : -loc - 1;
			if (loc >= 0) {
				System.out.println("[i] Adding key/value in leafNode: " + valueIndex + " / " + key + "/" + value);
				values.set(valueIndex, value);
			} else {
				System.out.println("[ii] Adding key/value in leafNode: " + valueIndex + " / " + key + "/" + value);
				keys.add(valueIndex, key);
				values.add(valueIndex, value);
			}
			if (root.isOverflow()) {
				Node sibling = split();
				intNode newRoot = new intNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}
		}

		@Override
		K getFirstLeafKey() {
			return keys.get(0);
		}

		

		@Override
		void merge(Node sibling) {
			@SuppressWarnings("unchecked")
			leafNode node = (leafNode) sibling;
			keys.addAll(node.keys);
			values.addAll(node.values);
			next = node.next;
		}

		@Override
		Node split() {
			System.out.println("Splitting leafNode");
			leafNode sibling = new leafNode();
			int from = (keyNumber() + 1) / 2, to = keyNumber();
			sibling.keys.addAll(keys.subList(from, to));
			sibling.values.addAll(values.subList(from, to));

			keys.subList(from, to).clear();
			values.subList(from, to).clear();

			sibling.next = next;
			next = sibling;
			return sibling;
		}

		@Override
		boolean isOverflow() {
			return values.size() > branchingFactor - 1;
		}

		@Override
		boolean isUnderflow() {
			return values.size() < branchingFactor / 2;
		}
	}
}