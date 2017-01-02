package informationRetrieval;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;

public class Cosine extends TFIDFSimilarity {

	/** Cache of decoded bytes. */
	private static final float[] NORM_TABLE = new float[256];

	static {
		for (int i = 0; i < 256; i++) {
			NORM_TABLE[i] = SmallFloat.byte315ToFloat((byte) i);
		}
	}
	 // Lucene encodes norm values into single bytes to lower the index size
	@Override
	public final long encodeNormValue(float f) {
		return SmallFloat.floatToByte315(f);
	}
	
	// Decode norm value
	@Override
	public final float decodeNormValue(long norm) {
		return NORM_TABLE[(int) (norm & 0xFF)]; // & 0xFF maps negative bytes to
		// positive above 127
	}

	public Cosine() {
	}

	@Override
	public float coord(int overlap, int maxOverlap) {
		return overlap / (float) maxOverlap;
	}

	@Override
	public float queryNorm(float sumOfSquaredWeights) {
		return (float) (1.0 / Math.sqrt(sumOfSquaredWeights)); // |q| * |d|
	}


	@Override
	public float lengthNorm(FieldInvertState state) {
		return state.getBoost() * ((float) (1.0 / Math.sqrt(state.getLength())));
	}

	/** Implemented as <code>sqrt(freq)</code>. */
	@Override
	public float tf(float freq) {
		return (float) Math.sqrt(freq); // Already weighted
	}
	
	/** Implemented as <code>log((docCount+1)/(docFreq+1)) + 1</code>. */
	@Override
	public float idf(long docFreq, long docCount) {
		return (float) (Math.log((docCount + 1) / (double) (docFreq + 1)) + 1.0);
	}

	/** Implemented as <code>1 / (distance + 1)</code>. */
	@Override
	public float sloppyFreq(int distance) {
		return 1.0f / (distance + 1);
	}

	/** The default implementation returns <code>1</code> */
	@Override
	public float scorePayload(int doc, int start, int end, BytesRef payload) {
		return 1;
	}


}