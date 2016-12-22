package similarity;

import similarity.cilin.Cilin;
import similarity.hownet.concept.XiaConceptParser;
import tools.Output;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static common.Constants.ROOT;
import static common.Constants.SEG;
import static tools.CommonTools.roundDouble;
import static tools.FileTools.*;

/**
 * Created by Ziyun on 2016/12/17.
 */

public class Similarity {
	public static void main(String[] args) {
		String root = getCurRootPath();
		String posFile = root + "/dict/pos.txt";
		String negFile = root + "/dict/neg.txt";
		String wordsFile = root + "/dict/word.txt";
		String similarityRoot = root + ROOT + "/similarity/";

		List<String> posList = readDataToList(posFile);
		List<String> negList = readDataToList(negFile);
		List<String> wordsList = readDataToList(wordsFile);

		Map<String, String> wordScoreByCilin = getInstance().calWordSim(posList, negList, wordsList, 1);
		Map<String, String> wordScoreByHownet = getInstance().calWordSim(posList, negList, wordsList, 2);
		Map<String, String> wordScoreBySopmi = getInstance().calWordSim(posList, negList, wordsList, 3);
		// Map<String, String> wordScoreBySopmi = SoPmi.getInstance().calSoPmi(readDataToList(root + ROOT + "wordSegment.txt"), wordsList);

		writeMapToFile(similarityRoot + "Cilin.txt", wordScoreByCilin);
		writeMapToFile(similarityRoot + "Hownet.txt", wordScoreByHownet);
		writeMapToFile(similarityRoot + "Sopmi.txt", wordScoreBySopmi);
	}

	private static Similarity instance = null;

	public static Similarity getInstance() {
		if (instance == null) {
			instance = new Similarity();
		}
		return instance;
	}

	private Similarity() {
	}

	// which 表示采用何种计算相似性的算法，1：cilin 2：hownet 3：sopmi
	public Map<String, String> calWordSim(List<String> posList, List<String> negList, List<String> wordList, int which) {
		Map<String, String> wordSimMap = new LinkedHashMap<>(wordList.size());
		double sim;
		if (which == 3) {
			for (String word : wordList) {
				sim = calSimByType(word, null, which);
				sim = roundDouble(sim, 3);
				wordSimMap.put(word, sim + SEG + Math.abs(sim));
				// wordSimMap.put(word, Math.abs(sim) + "");
			}
		} else {
			// wordSimMap.put("Word", "posSim" + SEG + "negSim" + SEG + "diffSim" + SEG + "Math.abs(diffSim)");
			for (String word : wordList) {
				Output.println("current word: " + SEG + word);
				double posSim = calSim(word, posList, which);
				double negSim = calSim(word, negList, which);
				posSim = roundDouble(posSim, 3);
				negSim = roundDouble(negSim, 3);
				double diffSim = roundDouble(posSim - negSim, 3);
				wordSimMap.put(word, posSim + SEG + negSim + SEG + diffSim + SEG + Math.abs(diffSim));
				// wordSimMap.put(word, "" + Math.abs(diffSim));
			}
		}
		return wordSimMap;
	}

	private double calSim(String word, List<String> wordList, int which) {
		double total = 0.0;
		double max = 0.0;
		double tmp;
		for (String w : wordList) {
			tmp = calSimByType(word, w, which);
			max = max > tmp ? max : tmp;
			total += tmp;
		}
		return roundDouble(max, 3);
		// return roundDouble((total / wordList.size()), 3);
	}

	// 根据指定的相似度计算方法计算词汇与情感词之间的相似性
	private double calSimByType(String word, String sentiWord, int which) {
		double result = 0.0;
		switch (which) {
			case 1:
				result = Cilin.getInstance().getSimilarity(word, sentiWord);
				break;
			case 2:
				// result = LiuConceptParser.getInstance().getSimilarity(word, sentiWord);
				result = XiaConceptParser.getInstance().getSimilarity(word, sentiWord);
				break;
			case 3:
				List<String> sentences = readDataToList(getCurRootPath() + ROOT + "/wordSegment.txt");
				result = SoPmi.getInstance().getSimilarity(sentences, word);
				break;
			default:
				break;
		}
		return result;
	}
}
