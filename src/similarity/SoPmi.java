package similarity;

import tools.FileTools;

import java.util.*;

import static common.Constants.BLANK;
import static common.Constants.SEG;
import static tools.CommonTools.roundDouble;

/**
 * Created by Ziyun on 2016/12/16.
 * <p>
 * 采用so-pmi计算词汇的情感得分
 */

public class SoPmi {
	//褒义词
	private static Set<String> positiveSet = null;
	//贬义词
	private static Set<String> negtiveSet = null;

	static {
		positiveSet = new HashSet<>();
		String positiveStr = "涨 上涨 暴涨 疯涨 涨幅 涨停 利好 增持 买入 推荐 牛市 重仓";
		StringTokenizer tokenizer = new StringTokenizer(positiveStr);
		while (tokenizer.hasMoreTokens()) {
			positiveSet.add(tokenizer.nextToken());
		}
		negtiveSet = new HashSet<>();
		String negtiveStr = "跌 下跌 大跌 跌幅 暴跌 跌停 减持 卖出 减仓 轻仓 利空 熊市";
		tokenizer = new StringTokenizer(negtiveStr);
		while (tokenizer.hasMoreTokens()) {
			negtiveSet.add(tokenizer.nextToken());
		}
	}

	private static SoPmi instance = null;

	public static SoPmi getInstance() {
		if (instance == null) {
			instance = new SoPmi();
		}
		return instance;
	}

	private SoPmi() {

	}

	// 计算词语的 SoPmi
	public double getSimilarity(List<String> sentences, String item) {

		List<String> positiveList = FileTools.readDataToList(FileTools.getCurRootPath() + "/dict/pos.txt");
		List<String> negtiveList = FileTools.readDataToList(FileTools.getCurRootPath() + "/dict/neg.txt");
		double pScore = 0.0;        // 正向 pmi
		double nScore = 0.0;        // 负向 pmi
		double sopmi;        // so-pmi
		double tmp;
		for (String pos : positiveList) {
			// pScore += calPmi(sentences, word, pos);
			tmp = calPmi(sentences, item, pos);
			pScore = pScore > tmp ? pScore : tmp;
		}
		for (String neg : negtiveList) {
			// nScore += calPmi(sentences, word, neg);
			tmp = calPmi(sentences, item, neg);
			nScore = nScore > tmp ? nScore : tmp;
		}
		// pScore = roundDouble(pScore / positiveSet.size(), 3);
		pScore = roundDouble(pScore, 3);
		// nScore = roundDouble(nScore / negtiveSet.size(), 3);
		nScore = roundDouble(nScore, 3);
		sopmi = roundDouble(pScore - nScore, 3);
		return sopmi;
	}

	// 计算词语的 so-pmi
	// public Map<String, String> calSoPmi(List<Segment> sentences, List<String> words) {
	// 	Map<String, String> resultMap = new HashMap<>(words.size());
	//
	// 	for (String word : words) {
	// 		double pScore = 0.0;        // 正向 pmi
	// 		double nScore = 0.0;        // 负向 pmi
	// 		double sopmi;        // so-pmi
	// 		for (String pos : positiveSet) {
	// 			pScore += calPmi(sentences, word, pos);
	// 		}
	// 		for (String neg : negtiveSet) {
	// 			nScore += calPmi(sentences, word, neg);
	// 		}
	// 		pScore = roundDouble(pScore / positiveSet.size(), 3);
	// 		nScore = roundDouble(nScore / negtiveSet.size(), 3);
	// 		sopmi = pScore - nScore;
	// 		if (sopmi > 0) {
	// 			positiveSet.add(word);
	// 		} else if (sopmi < 0) {
	// 			negtiveSet.add(word);
	// 		}
	// 		resultMap.put(word, pScore + SEG + nScore + SEG + sopmi);
	// 	}
	// 	return resultMap;
	// }

	// 计算词语的 so-pmi
	public Map<String, String> calSoPmi(List<String> sentences, List<String> words) {
		Map<String, String> resultMap = new HashMap<>(words.size());

		for (String word : words) {
			double pScore = 0.0;        // 正向 pmi
			double nScore = 0.0;        // 负向 pmi
			double sopmi;        // so-pmi
			double tmp;
			for (String pos : positiveSet) {
				// pScore += calPmi(sentences, word, pos);
				tmp = calPmi(sentences, word, pos);
				pScore = pScore > tmp ? pScore : tmp;
			}
			for (String neg : negtiveSet) {
				// nScore += calPmi(sentences, word, neg);
				tmp = calPmi(sentences, word, neg);
				nScore = nScore > tmp ? nScore : tmp;
			}
			// pScore = roundDouble(pScore / positiveSet.size(), 3);
			pScore = roundDouble(pScore, 3);
			// nScore = roundDouble(nScore / negtiveSet.size(), 3);
			nScore = roundDouble(nScore, 3);
			sopmi = roundDouble(pScore - nScore, 3);
			// if (sopmi > 0) {
			// 	positiveSet.add(word);
			// } else if (sopmi < 0) {
			// 	negtiveSet.add(word);
			// }
			resultMap.put(word, pScore + SEG + nScore + SEG + sopmi);
		}
		return resultMap;
	}


	// 计算两个词语间的点对互信息
	// private double calPmi(List<Segment> sentences, String word1, String word2) {
	// 	int common = calDf(sentences, word1, word2);
	// 	if (common == 0) {
	// 		return 0;
	// 	}
	// 	int df1 = calDf(sentences, word1);
	// 	int df2 = calDf(sentences, word2);
	// 	return Math.log(sentences.size() * common / (df1 * df2)) / Math.log(2);
	// }

	// 计算两个词语间的点对互信息
	private double calPmi(List<String> sentences, String word1, String word2) {
		int common = calDf(sentences, word1, word2);
		if (common == 0) {
			return 0;
		}
		int df1 = calDf(sentences, word1);
		int df2 = calDf(sentences, word2);
		return Math.log(sentences.size() * common / (df1 * df2)) / Math.log(2);
	}

	// // 计算包含词语(组)的句子数
	// private int calDf(List<Segment> sentences, String... words) {
	// 	int sum = 0;
	// 	for (Segment sentence : sentences) {
	// 		String[] content = sentence.getWordSeg().split(BLANK);
	// 		if (content.length > 0) {
	// 			List<String> wordSet = Arrays.asList(content);
	// 			int num = 0;
	// 			for (String word : words) {
	// 				if (wordSet.contains(word)) {
	// 					num++;
	// 				}
	// 			}
	// 			if (num == words.length) {
	// 				sum++;
	// 			}
	// 		}
	// 	}
	// 	return sum;
	// }

	// 计算包含词语(组)的句子数
	private int calDf(List<String> sentences, String... words) {
		int sum = 0;
		for (String sentence : sentences) {
			String[] content = sentence.split(BLANK);
			if (content.length > 0) {
				List<String> wordSet = Arrays.asList(content);
				int num = 0;
				for (String word : words) {
					if (wordSet.contains(word)) {
						num++;
					}
				}
				if (num == words.length) {
					sum++;
				}
			}
		}
		return sum;
	}
}
