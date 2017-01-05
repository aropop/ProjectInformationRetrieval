package informationRetrieval;

public class Parse{
	public String left;
	public String right;

	public Parse(String input){
		String output = "";
		for(int i = 0; i < input.length(); i++){
			char curr = input.charAt(i);
			if(curr == '*'){
				this.left = output;
				this.right = input.substring(i+1);
			}
			output += curr;
		}
	}
}
