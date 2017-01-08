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


/***
 * 
 * SimNoMerge is an extension of CustomScoreQuery.
 * This allows us to return our own score.
 *
 */

public class SimNoMerge extends CustomScoreQuery{ 
	private Query query;
	private String field;
	private String[] XMLcontext;
	private IndexReader ir;
	private float idf;
	private float normalization;
	
	
	/**
	 * The constructor takes 4 arguments: 
	 * - a Query: because it is necessary because of the extension
	 * - an IndexReader: necessary to have a reader for the whole index
	 * - a String field: the vocabulary term
	 * - a String context: the XML-context
	 * 
	 * @param query
	 * @param ir
	 * @param field
	 * @param context
	 */
	
	public SimNoMerge(Query query, IndexReader ir, String field, String context){
		super(query);
		this.query = query;
		this.field = field;
		this.XMLcontext = context.toLowerCase().split("//");
		this.ir = ir;
		try {
			Cosine cos = new Cosine();
			this.idf = cos.idf(ir.getDocCount(field), ir.maxDoc());	
			this.normalization = idf *  (float) (1 + Math.log10(ir.getSumTotalTermFreq(field)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * This function looks if the XML-context of the search can
	 * be transformed to given XML-context with only additions.
	 * If this is not possible, 0 will be returned.
	 * Otherwise |Cq| + 1 / |C| + 1 will be returned.
	 * 
	 * @param context
	 * @return
	 */
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
	
	
	/**
	 * 
	 * getCustomScoreProvider is the custom score provider created so that Lucene can score XML files.
	 * The weight is calculated by multiplying idf with the weighted tf.
	 * 
	 */
	
	public CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) {
		return new CustomScoreProvider(context) {
			public float customScore(int doc,
					float subQueryScore,
					float valSrcScore) throws IOException {
				IndexReader cont = context.reader();
				float score = 0;
				Document document = cont.document(doc);
				String[] values = document.getValues(field);
				if(values.length > 0) {
					float wf = (float) (1 + Math.log10(values.length));
					float weight = idf * wf;
					for(int i = 0; i < values.length; i++){
						float cr = contextResemblance(values[i].toLowerCase().split("/"));
						score += cr * weight;
					}
					score = score / normalization;
				}
				
				return score;
				
			}
		};
	}

	

}
