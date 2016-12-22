package SentimentAnalysis;

import static common.Constants.ROOT;
import static tools.FileTools.getCurRootPath;

/**
 * Created by Ziyun on 2016/12/21.
 *
 * 测试数据预处理，分句、分词、依存句法分析、保留指定词汇
 * 情感分析
 */

public class Step7 {

	public static void main(String[] args) {
		String root = getCurRootPath() + ROOT;        // 根目录
		String rawDataDir = root + "/raw_data";    	// 原始语料
		String sentencesFile = root + "/sentences.txt";    // 句子
		String sentenceByConjFile = root + "/sentencesByConj.txt";    // 连词分句
		// String sentenceConjFile = root + "/sentenceConjType.txt";        // 句子连词类型
		String wordSegFile = root + "/wordSegment.txt";                // 分词
		String wordSegWithTagFile = root + "/wordSegmentWithTag.txt";    // 含词性标注的分词
		String sentiWordsFile = root + "/selectedWords.txt";            // adj adv n v
		String wordFile = root + "/words.txt";            // 词汇
		String wordSpeechFile = root + "/wordSpeech.txt";            // 词汇
		String timeFile = root + "/timeCost.txt";            // 记录时间消耗

	}
}
