package informationRetrieval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;

public class SimNoMerge extends CustomScoreQuery{ 
	private Query query;
	private String field;
	private String[] XMLcontext;
	private IndexReader ir;
	
	
	public SimNoMerge(Query query, IndexReader ir, String field, String context){
		super(query);
		this.query = query;
		this.field = field;
		this.XMLcontext = context.toLowerCase().split("//");
		this.ir = ir;
	}
	
	private float contextResemblance(String[] context){
		ArrayList<String> contextClone = new ArrayList<String>(Arrays.asList(context.clone()));
		if(context.length >= XMLcontext.length) {
			for(int i = 0; i < XMLcontext.length; i++){
				boolean elFound = false;
				if(XMLcontext[i].equals("")){
					elFound = true;
				} else {
					for(int j = 0; j < contextClone.size(); j++){
						if(XMLcontext[i].equals(contextClone.get(j))){
							elFound = true;
							contextClone.remove(j);
							break;
						}
					}
				}
				
				if(!elFound){
					return 0;
				}
			}
			float returnValue = (float) (XMLcontext.length + 1) / (context.length + 1);
			return returnValue;
		} else {
			return 0;
		}
	}
	
	public CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) {
		return new CustomScoreProvider(context) {
			public float customScore(int doc,
					float subQueryScore,
					float valSrcScore) throws IOException {
				IndexReader cont = context.reader();
				Cosine cos = new Cosine();
				float score = 0;
				int tmp = ir.getDocCount(field);
				float idf = cos.idf(ir.getDocCount(field), ir.maxDoc());
				Document document = cont.document(doc);
				String[] values = document.getValues(field);
				if(values.length > 0) {
					float wf = (float) (1 + Math.log10(values.length));
					float weight = idf * wf;
					for(int i = 0; i < values.length; i++){
						float cr = contextResemblance(values[i].toLowerCase().split("/"));
						score += cr * weight;
					}
					@SuppressWarnings("unused")
					long termfreq = ir.getSumTotalTermFreq(field);
					score = score / (idf *  (float) (1 + Math.log10(ir.getSumTotalTermFreq(field))));
				}
				
				return score;
				
			}
		};
	}

	

}
