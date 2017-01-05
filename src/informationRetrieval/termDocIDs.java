package informationRetrieval;

import java.util.ArrayList;

public class termDocIDs {
	public String term;
	public ArrayList<Integer> docIDs;

	public termDocIDs(String term, ArrayList<Integer> docIDs){
		this.term = term;
		this.docIDs = docIDs;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {return false;}

		if (!(obj instanceof termDocIDs)){return false;}

		termDocIDs casted = (termDocIDs) obj;

		if(this.docIDs.size() != casted.docIDs.size()){return false;}
		
		ArrayList<Integer> commonIDs = this.docIDs;
		commonIDs.retainAll(casted.docIDs);

		if ((this.term.equals(casted.term)) && (commonIDs.size() == this.docIDs.size())){
			return true;
		}

		return false;
	}

}
