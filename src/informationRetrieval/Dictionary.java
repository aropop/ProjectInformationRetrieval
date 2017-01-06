package informationRetrieval;

import java.util.ArrayList;

import org.apache.lucene.index.PostingsEnum;

public class Dictionary {
	BTree tree = new BTree();
	BTree reverseTree = new BTree();
	
	private String reverseString(String str){
		return new StringBuilder(str).reverse().toString();
	}
	
	public void add(String key, PostingsEnum val) throws Exception{
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
		
		return firstPart;
	}
}
