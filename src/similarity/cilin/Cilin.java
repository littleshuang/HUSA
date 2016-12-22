package similarity.cilin;

import java.util.Set;

/**
 * Created by Ziyun on 2016/11/24.
 */

public class Cilin {

	private static Cilin instance = null;

	public static Cilin getInstance() {
		if (instance == null) {
			instance = new Cilin();
		}
		return instance;
	}

	private Cilin() {

	}

	public double getSimilarity(String item1, String item2) {
		double sim = 0.0;

		if (item1 == null && item2 == null) {
			return 1.0;
		} else if (item1 == null || item2 == null) {
			return 0.0;
		} else if (item1.equalsIgnoreCase(item2)) {
			return 1.0;
		}

		Set<String> codeSet1 = CilinDb.getInstance().getCilinCoding(item1);
		Set<String> codeSet2 = CilinDb.getInstance().getCilinCoding(item2);
		if (codeSet1 == null || codeSet2 == null) {
			return 0.0;
		}
		for (String code1 : codeSet1) {
			for (String code2 : codeSet2) {
				double s = getSimilarityByCode(code1, code2);
				// System.out.println(code1 + "-" + code2 + "-" + CilinCoding.calculateCommonWeight(code1, code2));
				if (sim < s)
					sim = s;
			}
		}
		return sim;
	}

	public double getSimilarityByCode(String code1, String code2) {
		return CilinCoding.calculateCommonWeight(code1, code2) / CilinCoding.TOTAL_WEIGHT;
	}

	public static void main(String[] args) {
		Cilin cilin = Cilin.getInstance();
		String term1 = "好";
		String term2 = "坏";
		Double sim = cilin.getSimilarity(term1, term2);
		System.out.println("Similarity between " + term1 + " " + term2 + " is " + sim);

		String[] words1 = {"好", "优秀", "赞赏", "坏", "差"};
		String[] words2 = {"好", "不好", "坏", "可以", "差", "优秀", "失败", "批评"};
		for (String word1 : words1) {
			for (String word2 : words2) {
				Double similarity = cilin.getSimilarity(word1, word2);
				System.out.println("Similarity between " + word1 + " " + word2 + " is " + similarity);
			}
		}
	}
}
