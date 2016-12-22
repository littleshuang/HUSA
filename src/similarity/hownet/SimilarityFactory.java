package similarity.hownet;

import similarity.hownet.concept.XiaConceptParser;

public class SimilarityFactory {

	private static WordSimilarity wordSimilarity = (WordSimilarity) XiaConceptParser.getInstance();

	private SimilarityFactory() {
		//
	}

	public static WordSimilarity getWordSimilarity() {
		return wordSimilarity;
	}

}
