package SentimentAnalysis;

import similarity.Similarity;

import java.util.List;
import java.util.Map;

import static common.Constants.ROOT;
import static tools.FileTools.*;

/**
 * Created by Ziyun on 2016/12/16.
 *
 * 计算词汇相似性
 */

public class Step2 {
	public static void main(String[] args) {
		String root = getCurRootPath() + ROOT;        // 根目录
		String wordFile = root + "/words.txt";            // 词汇
		String posFile = getCurRootPath() + "/dict/gPos.txt";
		String negFile = getCurRootPath() + "/dict/gNeg.txt";
		String similarityRoot = root + "/similarity/";

		List<String> wordList = readDataToList(wordFile);
		List<String> posList = readDataToList(posFile);
		List<String> negList = readDataToList(negFile);

		Similarity sim = Similarity.getInstance();
		Map<String, String> wordScoreByCilin = sim.calWordSim(posList, negList, wordList, 1);
		Map<String, String> wordScoreByHownet = sim.calWordSim(posList, negList, wordList, 2);
		Map<String, String> wordScoreBySopmi = sim.calWordSim(posList, negList, wordList, 3);
		// Map<String, String> wordScoreBySopmi = SoPmi.getInstance().calSoPmi(readDataToList(root + ROOT + "wordSegment.txt"), wordsList);

		writeMapToFile(similarityRoot + "Cilin.txt", wordScoreByCilin);
		writeMapToFile(similarityRoot + "Hownet.txt", wordScoreByHownet);
		writeMapToFile(similarityRoot + "Sopmi.txt", wordScoreBySopmi);
	}
}
