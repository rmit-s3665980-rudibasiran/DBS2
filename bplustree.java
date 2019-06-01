import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class bplustree  {

	private int maxKeys;
	private Node root;

	public bplustree() {
		maxKeys = dbimpl.MAX_NUM_KEYS;
		root = new leafNode();
	}

	public void rangeSearch(String k1, String k2, int searchType) {
		root.rangeSearch(k1, k2, searchType);
	}

	public void search(String key) {
		root.search(key);
	}

	public void traverse(FileOutputStream fos) {
		root.traverse(fos);
	}

	public void insert(String key, String value) {
		root.insert(key, value);
	}

	public abstract class Node {
		List<String> keys;

		int keySize() {
			return keys.size();
		}

		abstract void search(String key);

		abstract void traverse(FileOutputStream fos);

		abstract void rangeSearch(String k1, String k2, int searchType);

		abstract boolean isVisited();

		abstract void insert(String key, String value);

		abstract String getFirstLeafKey();

		abstract Node split();

		abstract boolean isOverflow();
	}

	public class innerNode extends Node {
		List<Node> children;
		Boolean visited = false;
	
		public innerNode() {
			this.keys = new ArrayList<String>();
			this.children = new ArrayList<Node>();
		}

		@Override
		public boolean isVisited() {
			return visited;
		}

		@Override
		public void rangeSearch(String k1, String k2, int searchType) {
			for (int i = 0; i < children.size(); i++) {
				if (!children.get(i).isVisited()) {
					children.get(i).rangeSearch(k1, k2, searchType);
					visited = true;
				}
			}
		}

		@Override
		public void search(String key) {
			for (int i = 0; i < children.size(); i++) {
				if (!children.get(i).isVisited()) {
					children.get(i).search(key);
					visited = true;
				}
			}
		}

		@Override
		public void traverse (FileOutputStream fos) {
			if (dbimpl.SHOW_TREE_KEYS) {
				int numKeys = 0;
				for (int i = 0; i < children.size(); i++) {
					numKeys = 0;
					for (int j = 0; j < children.get(i).keys.size(); j++) {
						if (dbimpl.SHOW_TREE_KEYS) {
							System.out.println("innerNode [" + i + "] key ]" + j + "]:" + children.get(i).keys.get(j));
						}
						numKeys = j;
					}
					System.out.println("innerNode [" + i + "] number of keys [" + numKeys + "]");
				}
			}
			else {
				for (int i = 0; i < children.size(); i++) {
					if (!children.get(i).isVisited()) {
						children.get(i).traverse(fos);
						visited = true;
					}
				}
			}	
		}

		@Override
		public void insert(String key, String value) {
			Node child = getChild(key);

			if (dbimpl.DEBUG_MODE_SHOW_INSERT) {
				// if (key.toLowerCase().contains(dbimpl.DEBUG_MODE_SEARCH_STR))
					System.out.println("[a] Adding key/value in innerNode: " + key + "/" + value);
			}
			child.insert(key, value);
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
			return children.size() > maxKeys;
		}

		public Node getChild(String key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (dbimpl.DEBUG_MODE_SHOW_INSERT) {
				System.out.println("key [" + key + "] keys.size() [" 
					+ children.get(childIndex).keySize() 
					+ "] loc [" + loc + "] childIndex [" + childIndex + "]");
			}
			return children.get(childIndex);
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
		Boolean visited = false;

		public leafNode() {
			keys = new ArrayList<String>();
			values = new ArrayList<String>();
		}

		@Override
		public boolean isVisited() {
			return visited;
		}

		@Override
		public void search(String key) {
			visited = true;
			for (int i = 0; i < keys.size(); i++) {
				String s = keys.get(i); 
				String v = values.get(i);
				String k = key;
				if (s.toLowerCase().contains(k.toLowerCase()) 
					|| v.toLowerCase().contains(k.toLowerCase())) {
					System.out.println("Found in B+ Tree [" + k + "]: " + s + " - " + v);
				}
			}
		}

		@Override
		public void rangeSearch(String k1, String k2, int searchType) {
			visited = true;
			for (int i = 0; i < keys.size(); i++) {
				String s = keys.get(i); 
				String v = values.get(i);
				String str = "";
				if (searchType == dbimpl.RANGE_KEY_DEVICE) {
					str = s.substring(0, dbimpl.DEVICE_ID_SIZE);
				}
				else if (searchType == dbimpl.RANGE_KEY_DATE) {
					str = s.substring(dbimpl.DEVICE_ID_SIZE, dbimpl.DEVICE_ID_SIZE + 10);
				}
				if (str.compareTo(k1) >= 0 && str.compareTo(k2) <= 0) {
					System.out.println("Found in B+ Tree Range [" + k1 + "] " + dbimpl.RANGE_DELIMITER + " [" 
						+ k2 + "]: " + s + " - " + v);
				}
				if (str.contains(k2)) {
					System.out.println("Found in B+ Tree Range [" + k1 + "] " + dbimpl.RANGE_DELIMITER + " [" 
						+ k2 + "]: " + s + " - " + v);
				}
				
			}
		}

		@Override
		public void traverse(FileOutputStream fos) {
			visited = true;
			byte[] record = new byte[dbimpl.TREE_RECORD_SIZE];
			for (int i = 0; i < keys.size(); i++) {
				String s = keys.get(i); 
				String v = values.get(i);
				String out = s + "," + v + ",";
				record = out.getBytes();
				try {
					fos.write(record);
				}
				catch (FileNotFoundException e) {
					System.out.println("File " + dbimpl.BPLUS_TREE_FILE_NAME + " not found.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void insert(String key, String value) {
			
			int loc = Collections.binarySearch(keys, key);
			int valueIndex = loc >= 0 ? loc : -loc - 1;
			if (loc >= 0) {
				if (dbimpl.DEBUG_MODE_SHOW_INSERT) {
					// if (key.toLowerCase().contains(dbimpl.DEBUG_MODE_SEARCH_STR))
						System.out.println("[a] Adding key/value in leafNode: values.size() = " + values.size() + " / " + key + "/" + value);
				}
				values.set(valueIndex, value);
			} else {
				if (dbimpl.DEBUG_MODE_SHOW_INSERT) {
					// if (key.toLowerCase().contains(dbimpl.DEBUG_MODE_SEARCH_STR))
						System.out.println("[b] Adding key/value in leafNode: values.size() = " + values.size() + " / " + key + "/" + value);
				}
				keys.add(valueIndex, key);
				values.add(valueIndex, value);
			}
			if (root.isOverflow()) {
				if (dbimpl.DEBUG_MODE_SHOW_INSERT) {
					// if (key.toLowerCase().contains(dbimpl.DEBUG_MODE_SEARCH_STR))
						System.out.println("[c] Overflow in leafNode: values.size() = " + values.size() + " / " + key + "/" + value);
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
			return values.size() > maxKeys - 1;
		}
	}
}