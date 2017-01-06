package informationRetrieval;

import org.apache.lucene.index.PostingsEnum;

public class termDocIDs {
	public String term;
	public PostingsEnum docIDs;

	public termDocIDs(String term, PostingsEnum docIDs){
		this.term = term;
		this.docIDs = docIDs;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {return false;}

		if (!(obj instanceof termDocIDs)){return false;}

		termDocIDs casted = (termDocIDs) obj;

		if (this.term.equals(casted.term)){
			return true;
		}

		return false;
	}

}
