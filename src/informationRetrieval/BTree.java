package informationRetrieval;

import java.util.ArrayList;

import org.apache.lucene.index.PostingsEnum;

/******************************************************************************
 Source: http://algs4.cs.princeton.edu/62btree/BTree.java
 Modified for the Information Retrieval Course, Group 3
 */

/**
 * Class for BTree with int key and ArrayList<String> value.
 * Every key has thus an ArrayList of String values associated with it
 */
public class BTree {
	// max children per B-tree node = M-1
	// (must be even and greater than 2)
	private static final int M = 4;

	private Node root;       // root of the B-tree
	private int height;      // height of the B-tree
	private int n;           // number of key-value pairs in the B-tree

	// helper B-tree node data type
	private static final class Node {
		private int m;                             // number of children
		private String biggest = "";               //biggest value in the subTree
		private Entry[] children = new Entry[M];   // the array of children

		// create a node with k children
		private Node(int k) {
			m = k;
		}

		//Get the smallest child of the subTree
		private String getSmallestChild(){
			return children[0].key;
		}
	}

	// internal nodes: only use key and next
	// external nodes: only use key and value
	private static class Entry {
		private String key;
		private PostingsEnum val = null;
		private Node next;     // helper field to iterate over array entries
		public Entry(String key, PostingsEnum val, Node next) {
			this.key  = key;
			this.val = (val);
			this.next = next;
		}
	}

	/**
	 * Initializes an empty B-tree.
	 */
	public BTree() {
		root = new Node(0);
	}

	/**
	 * Returns true if this symbol table is empty.
	 * @return {@code true} if this symbol table is empty; {@code false} otherwise
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns the number of key-value pairs in this symbol table.
	 * @return the number of key-value pairs in this symbol table
	 */
	public int size() {
		return n;
	}

	/**
	 * Returns the height of this B-tree (for debugging).
	 *
	 * @return the height of this B-tree
	 */
	public int height() {
		return height;
	}


	/**
	 * Returns the value associated with the given key.
	 *
	 * @param  key the key
	 * @return the value associated with the given key if the key is in the symbol table
	 *         and {@code null} if the key is not in the symbol table
	 * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public PostingsEnum get(String key) {
		if (key == null) throw new IllegalArgumentException("argument to get() is null");
		return search(root, key, height);
	}

	private PostingsEnum search(Node x, String key, int ht) {
		Entry[] children = x.children;

		// external node
		if (ht == 0) {
			for (int j = 0; j < x.m; j++) {
				if (eq(key, children[j].key)) return children[j].val;
			}
		}

		// internal node
		else {
			for (int j = 0; j < x.m; j++) {
				if (j+1 == x.m || less(key, children[j+1].key))
					return search(children[j].next, key, ht-1);
			}
		}
		return null;
	}

	/**
	 * Inserts the key-value pair into the symbol table, overwriting the old value
	 * with the new value if the key is already in the symbol table.
	 * If the value is {@code null}, this effectively deletes the key from the symbol table.
	 *
	 * @param  key the key
	 * @param  val the value
	 * @throws Exception 
	 * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public void put(String key, PostingsEnum val) throws Exception {
		if (key == null) throw new IllegalArgumentException("argument key to put() is null");
		Node u = insert(root, key, val, height); 
		n++;
		if (u == null) return;

		// need to split root
		Node t = new Node(2);
		t.children[0] = new Entry(root.children[0].key, null, root);
		t.children[1] = new Entry(u.children[0].key, null, u);
		root = t;
		height++;
		
		t.biggest = t.children[t.m-1].next.biggest;
	}

	private Node insert(Node h, String key, PostingsEnum val, int ht) throws Exception{
		int j;
		Entry t = new Entry(key, val, null);

		//If the key inserted in the subtree is bigger as the current biggest
		//set the current biggest on the inserted key. 
		if(less(h.biggest, key)){h.biggest = key;}

		// external node
		if (ht == 0) {
			for (j = 0; j < h.m; j++) {
				if (less(key, h.children[j].key)){
					break;
				}
				if (eq(key, h.children[j].key)){
					throw new Exception("Dictionary already contains Term");
				}
			}
		}

		// internal node
		else {
			for (j = 0; j < h.m; j++) {
				if ((j+1 == h.m) || less(key, h.children[j+1].key)) {
					Node u = insert(h.children[j++].next, key, val, ht-1);
					if (u == null) return null;
					t.key = u.children[0].key;
					t.next = u;
					break;
				}
			}
		}

		for (int i = h.m; i > j; i--)
			h.children[i] = h.children[i-1];
		h.children[j] = t;	
		h.m++;
		if (h.m < M) return null;
		else         return split(h);
	}

	// split node in half
	private Node split(Node h) {
		Node t = new Node(M/2);
		h.m = M/2;	

		//If node splitted biggest should be replaced by biggest of subtree of new node (first part of splitted node)
		h.biggest = (h.children[(M/2)-1].next != null) ? h.children[(M/2)-1].next.biggest : h.children[(M/2)-1].key;
		//If node splitted biggest of new node should be biggest of subtree second part of old node
		t.biggest = (h.children[M-1].next != null) ? h.children[M-1].next.biggest : h.children[M-1].key;
		
		for (int j = 0; j < M/2; j++)
			t.children[j] = h.children[M/2+j]; 
		
		return t;    
	}

	// comparison functions - make Comparable instead of Key to avoid casts
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean less(Comparable k1, Comparable k2) {
		return k1.compareTo(k2) < 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public static boolean bigger(Comparable k1, Comparable k2){
		return k1.compareTo(k2) > 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean eq(Comparable k1, Comparable k2) {
		return k1.compareTo(k2) == 0;
	}

	private String reverseString(String str){
		return new StringBuilder(str).reverse().toString();
	}
	
	//Loops over the BTree to find Key-Value pairs where Key matches given wildcard
	private void loop(String lower, String upper, Node n, int ht, ArrayList<termDocIDs> res, Boolean reverse){
		if(ht == 0){
			// Search in children what matches
			for(int i = 0; i < n.m; i++){
				String curr = n.children[i].key;
				if(eq(curr, lower) || (bigger(curr, lower) && less(curr, upper))){
					if(reverse){
						res.add(new termDocIDs(reverseString(n.children[i].key), n.children[i].val));
					}else{
						res.add(new termDocIDs(n.children[i].key, n.children[i].val));
					}
				}
			}
			return;
		}

		//Two conditions to not investigate subTree
		if(bigger(n.getSmallestChild(), upper)){return;}
		if(less(n.biggest, lower)){return;}
		
		//Investigate all subTrees
		for(int i = 0; i < n.m; i++){
			loop(lower, upper, n.children[i].next, (ht -1), res, reverse);
		}

		return;
	}

	//Calculates the next string, with a value of 1 bigger as the given one
	//Used to create an upper bound
	private static String nextString(String input){
		int val = input.charAt(input.length() - 1);
		++val;
		return input.substring(0, input.length()-1) + String.valueOf((char) val);
	}

	//Finds the answers for the given wildcard
	//Wildcard should be of the form x* where x is a string
	public ArrayList<termDocIDs> wildcard(String str, Boolean reverse){
		ArrayList<termDocIDs> res = new ArrayList<termDocIDs>();
		String upper = nextString(str);
		loop(str, upper, root, height, res, reverse);
		return res;
	};

}