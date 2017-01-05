package informationRetrieval;

import java.util.ArrayList;

public class Dictionary {
	BTree tree = new BTree();
	BTree reverseTree = new BTree();
	
	private String reverseString(String str){
		return new StringBuilder(str).reverse().toString();
	}
	
	public void add(String key, int val){
		String reverseKey = reverseString(key);
		this.tree.put(key, val);
		this.reverseTree.put(reverseKey, val);
	}
	
	public ArrayList<termDocIDs> get(String str){
		Parse parse = new Parse(str);
		
		if(parse.left.isEmpty()){
			return this.reverseTree.wildcard(reverseString(parse.right));
		}
		if(parse.right.isEmpty()){
			return this.tree.wildcard(parse.left);
		}
		
		ArrayList<termDocIDs> firstPart = tree.wildcard(parse.left);
		ArrayList<termDocIDs> secondPart = reverseTree.wildcard(reverseString(parse.right));
		firstPart.retainAll(secondPart);
		
		System.out.println(firstPart.size());
		
		return firstPart;
	}
	
	public static void main(String[] args) {
		Dictionary dict = new Dictionary();

		dict.add("1", 1);
		dict.add("2", 1);
		dict.add("3", 1);
		dict.add("4", 1);
		dict.add("5", 1);
		dict.add("aa", 1);
		dict.add("ab", 3);
		dict.add("ac", 3);
		dict.add("ac", 3);
		dict.add("ad", 3);
		dict.add("ae", 3);
		dict.add("bb", 1);
		dict.add("cc", 1);
		dict.add("dd", 2);
		dict.add("ee", 3);
		dict.add("ff", 4);
		dict.add("gg", 5);
		dict.add("hh", 6);
		dict.add("ii", 7);
		dict.add("jj", 8);
		dict.add("kk", 2);
		dict.add("ll", 3);
		dict.add("mm", 4);
		dict.add("nn", 5);
		dict.add("oo", 6);
		dict.add("pp", 7);
		dict.add("qq", 8);
		dict.add("rr", 2);
		dict.add("ss", 3);
		dict.add("tt", 4);
		dict.add("uu", 5);
		dict.add("vv", 6);
		dict.add("ww", 7);
		dict.add("xx", 8);
		dict.add("yy", 8);
		dict.add("zzz", 8);

		String str = "a*";
		ArrayList<termDocIDs> res = dict.get(str);
		for(int i = 0; i < res.size(); i++){
			termDocIDs curr = res.get(i);
			System.out.println(curr.term);
			System.out.println(curr.docIDs);
		}
	}
}
