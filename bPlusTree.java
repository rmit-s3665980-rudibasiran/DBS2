import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class bPlusTree  {

	private int branchingFactor;
	private Node root;

	public bPlusTree() {
		branchingFactor = dbimpl.DEFAULT_BRANCHING_FACTOR;
		root = new leafNode();
	}

	// @SuppressWarnings("unchecked")
	public String search(String key) {
		return root.getValue(key);
	}


	// @SuppressWarnings("unchecked")
	public void insert(String key, String value) {
		root.insertValue(key, value);
	}

	public void delete(String key) {
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
					if (node instanceof bPlusTree.innerNode)
						nextQueue.add(((innerNode) node).children);
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

	public abstract class Node {
		List<String> keys;

		int keySize() {
			return keys.size();
		}

		abstract String getValue(String key);

		abstract void deleteValue(String key);

		abstract void insertValue(String key, String value);

		abstract String getFirstLeafKey();

		abstract void merge(Node sibling);

		abstract Node split();

		abstract boolean isOverflow();

		abstract boolean isUnderflow();

		public String toString() {
			return keys.toString();
		}
	}

	public class innerNode extends Node {
		List<Node> children;

		public innerNode() {
			this.keys = new ArrayList<String>();
			this.children = new ArrayList<Node>();
		}

		@Override
		public String getValue(String key) {

			// traversing
			for (int i = 0; i < children.size(); i++) {
				for (int j = 0; j < children.get(i).keys.size(); j++) {
				
					String s = children.get(i).keys.get(j).toString(); 
					String k = key.toString();
					
					if (s.toLowerCase().contains(k.toLowerCase())) {
						System.out.println("Found in innerNode " + i + " [" + k + "]: " + j + " / " + s);
					}
				}
			}
			


			return getChild(key).getValue(key);
		}


		@Override
		public void deleteValue(String key) {
			Node child = getChild(key);
			child.deleteValue(key);
			if (child.isUnderflow()) {
				Node childLeftSibling = getChildLeftSibling(key);
				Node childRightSibling = getChildRightSibling(key);
				Node left = childLeftSibling != null ? childLeftSibling : child;
				Node right = childLeftSibling != null ? child : childRightSibling;
				left.merge(right);
				deleteChild(right.getFirstLeafKey());
				if (left.isOverflow()) {
					Node sibling = left.split();
					insertChild(sibling.getFirstLeafKey(), sibling);
				}
				if (root.keySize() == 0)
					root = left;
			}
		}

		@Override
		public void insertValue(String key, String value) {
			Node child = getChild(key);

			if (dbimpl.DEBUG_MODE_SHOW_INSERT) {
				if (key.toLowerCase().contains(dbimpl.DEBUG_MODE_SEARCH_STR))
					System.out.println("[a] Adding key/value in innerNode: " + key + "/" + value);
			}

				
			child.insertValue(key, value);
			if (child.isOverflow()) {
				Node sibling = child.split();
				insertChild(sibling.getFirstLeafKey(), sibling);
			}
			if (root.isOverflow()) {
				Node sibling = split();
				innerNode newRoot = new innerNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}
		}

		@Override
		public String getFirstLeafKey() {
			return children.get(0).getFirstLeafKey();
		}

	
		@Override
		public void merge(Node sibling) {
			// @SuppressWarnings("unchecked")
			innerNode node = (innerNode) sibling;
			keys.add(node.getFirstLeafKey());
			keys.addAll(node.keys);
			children.addAll(node.children);

		}

		@Override
		public Node split() {
			if (dbimpl.DEBUG_MODE_SHOW_INSERT)
				System.out.println("[a] Splitting innerNode");
			int from = keySize() / 2 + 1;
			int to = keySize();
			innerNode sibling = new innerNode();
			// subList(int fromIndex, int toIndex)
			sibling.keys.addAll(keys.subList(from, to));
			sibling.children.addAll(children.subList(from, to + 1));

			keys.subList(from - 1, to).clear();
			children.subList(from, to + 1).clear();

			return sibling;
		}

		@Override
		public boolean isOverflow() {
			return children.size() > branchingFactor;
		}

		@Override
		public boolean isUnderflow() {
			return children.size() < (branchingFactor + 1) / 2;
		}

		public Node getChild(String key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			return children.get(childIndex);
		}

		public void deleteChild(String key) {
			int loc = Collections.binarySearch(keys, key);
			if (loc >= 0) {
				keys.remove(loc);
				children.remove(loc + 1);
			}
		}

		public void insertChild(String key, Node child) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (loc >= 0) {
				children.set(childIndex, child);
			} else {
				keys.add(childIndex, key);
				children.add(childIndex + 1, child);
			}
		}

		public Node getChildLeftSibling(String key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (childIndex > 0)
				return children.get(childIndex - 1);

			return null;
		}

		public Node getChildRightSibling(String key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (childIndex < keySize())
				return children.get(childIndex + 1);

			return null;
		}
	}

	public class leafNode extends Node {
		List<String> values;
		leafNode next;

		public leafNode() {
			keys = new ArrayList<String>();
			values = new ArrayList<String>();
		}


		@Override
		public String getValue(String key) {

			for (int i = 0; i < keys.size(); i++) {
				
				String s = keys.get(i).toString(); 
				String k = key.toString();
				String v = values.get(i).toString();
				
				if (s.toLowerCase().contains(k.toLowerCase()) 
						|| v.toLowerCase().contains(k.toLowerCase())) {
					System.out.println("Found in leafNode [" + k + "]: " + i + " / " + s + " / " + v);
				}
			}

			int loc = Collections.binarySearch(keys, key);
			return loc >= 0 ? values.get(loc) : null;
		}

		@Override
		public void deleteValue(String key) {
			int loc = Collections.binarySearch(keys, key);
			if (loc >= 0) {
				keys.remove(loc);
				values.remove(loc);
			}
		}

		@Override
		public void insertValue(String key, String value) {
			
			int loc = Collections.binarySearch(keys, key);
			int valueIndex = loc >= 0 ? loc : -loc - 1;
			if (loc >= 0) {
				if (dbimpl.DEBUG_MODE_SHOW_INSERT) {
					if (key.toLowerCase().contains(dbimpl.DEBUG_MODE_SEARCH_STR))
						System.out.println("[a] Adding key/value in leafNode: " + valueIndex + " / " + key + "/" + value);
				}
		
				values.set(valueIndex, value);
			} else {
				if (dbimpl.DEBUG_MODE_SHOW_INSERT) {
					if (key.toLowerCase().contains(dbimpl.DEBUG_MODE_SEARCH_STR))
						System.out.println("[b] Adding key/value in leafNode: " + valueIndex + " / " + key + "/" + value);
				}
					
				keys.add(valueIndex, key);
				values.add(valueIndex, value);
			}
			if (root.isOverflow()) {
				if (dbimpl.DEBUG_MODE_SHOW_INSERT) {
					if (key.toLowerCase().contains(dbimpl.DEBUG_MODE_SEARCH_STR))
						System.out.println("[c] Overflow in leafNode: " + valueIndex + " / " + key + "/" + value);
				}
					
				Node sibling = split();
				innerNode newRoot = new innerNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}
		}

		@Override
		public String getFirstLeafKey() {
			return keys.get(0);
		}


		@Override
		public void merge(Node sibling) {
			// @SuppressWarnings("unchecked")
			leafNode node = (leafNode) sibling;
			keys.addAll(node.keys);
			values.addAll(node.values);
			next = node.next;
		}

		@Override
		public Node split() {
			if (dbimpl.DEBUG_MODE_SHOW_INSERT)
				System.out.println("[b] Splitting leafNode");
			leafNode sibling = new leafNode();
			int from = (keySize() + 1) / 2;
			int to = keySize();
			sibling.keys.addAll(keys.subList(from, to));
			sibling.values.addAll(values.subList(from, to));

			keys.subList(from, to).clear();
			values.subList(from, to).clear();

			sibling.next = next;
			next = sibling;
			return sibling;
		}

		@Override
		public boolean isOverflow() {
			return values.size() > branchingFactor - 1;
		}

		@Override
		public boolean isUnderflow() {
			return values.size() < branchingFactor / 2;
		}
	}
}