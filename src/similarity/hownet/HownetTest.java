package similarity.hownet;

import similarity.hownet.concept.LiuConceptParser;
import similarity.hownet.concept.XiaConceptParser;

/**
 * Created by Ziyun on 2016/12/17.
 */

public class HownetTest {

	public static void main(String[] args) {
		XiaConceptParser xParser = XiaConceptParser.getInstance();
		LiuConceptParser lParser = LiuConceptParser.getInstance();

		String word1 = "电动车";
		String word2 = "自行车";
		double x_sim = xParser.getSimilarity(word1, word2);
		double l_sim = lParser.getSimilarity(word1, word2);

		System.out.println("x_sim:" + x_sim);
		System.out.println("l_sim:" + l_sim);
	}

}
