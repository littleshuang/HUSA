package SentimentAnalysis;

import NLP.Ictclas;
import NLP.Ltp;
import beans.ConjWord;
import beans.Segment;
import com.sun.javafx.beans.annotations.NonNull;

import java.util.*;

import static common.Constants.*;
import static common.WriteSentence.*;
import static tools.CommonTools.copyList;
import static tools.CommonTools.sortMapByValue;
import static tools.FileTools.*;
import static tools.Output.log;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class Step1 {

	private static int id = 1;           // 句子id

	public static void main(String[] args) {

		String root = getCurRootPath() + ROOT;        // 根目录
		String rawDataDir = root + "/raw_data";    // 原始语料
		String sentencesFile = root + "/sentences.txt";    // 句子
		String sentenceByConjFile = root + "/sentencesByConj.txt";    // 连词分句
		// String sentenceConjFile = root + "/sentenceConjType.txt";        // 句子连词类型
		String wordSegFile = root + "/wordSegment.txt";                // 分词
		String wordSegWithTagFile = root + "/wordSegmentWithTag.txt";    // 含词性标注的分词
		String sentiWordsFile = root + "/selectedWords.txt";            // adj adv n v
		String wordFile = root + "/words.txt";            // 词汇
		String wordSpeechFile = root + "/wordSpeech.txt";            // 词汇
		String timeFile = root + "/timeCost.txt";            // 记录时间消耗
		// String tmpFile = root + "/tmp.txt";            // 临时文件

		log("Begin");
		log("Step 1: 读取数据 & 分句");
		Step1 step1 = new Step1();
		List<String> rawDataList = readDataFromFiles(rawDataDir);
		List<String> sentencesList = step1.segParas(rawDataList);
		log("rawDataList size: " + rawDataList.size());
		log("sentencesList size: " + sentencesList.size());
		writeListToFile(sentencesFile, sentencesList);

		log("Step 2: 连词分句");
		List<Segment> sentenceList = step1.segSentenceByConj(sentencesList);
		writeSentenceInfo(sentenceByConjFile, sentenceList, CONTENT);

		log("Step 3: 分词");
		step1.wordSegment(sentenceList);
		log("Step 4: 保留特定词汇");
		step1.saveSpecificWords(sentenceList, true);
		writeSentenceInfo(wordSegFile, sentenceList, WORDSEG);
		writeSentenceInfo(wordSegWithTagFile, sentenceList, WORDSEGWITHTAG);
		writeSentenceInfo(sentiWordsFile, sentenceList, SENTIWORDS);

		log("Step 5: 获取词汇集合");
		// Map<String, Integer> wordMap = step1.genWordSet(sentenceList);
		// Map<String, String> sortedWordMap = step1.sortWord(wordMap);
		Map<String, String> wordSpeechMap = new HashMap<>();
		Map<String, Integer> wordMap = step1.genWordSet(sentenceList, wordSpeechMap);
		writeMapToFile(wordSpeechFile, wordSpeechMap);
		writeCollectionToFile(wordFile, wordMap.keySet());
	}

	// 标点分句
	private List<String> segParas(List<String> paras) {
		String[] puncs = {"。", "！", "？"};        // 定义用于分句的标点符号

		for (String punc : puncs) {
			List<String> tmp = new ArrayList<String>();
			for (String para : paras) {
				if ((para = newTrim(para)).length() > 0) {
					tmp.addAll(segParaByPunc(para, punc));
				}
			}
			paras = copyList(tmp);
		}
		return paras;
	}

	/**
	 * 使用特定标点对段落进行分句
	 *
	 * @param para：待分句的段落
	 * @param punc：指定的标点
	 * @return 分句后的句子列表
	 */
	private List<String> segParaByPunc(String para, String punc) {
		assert para.length() > 0;
		return Arrays.asList(para.split(punc));
	}

	// 构建连词列表
	private List<ConjWord> addAllConjunctions() {

		// 连词按字数从多至少存放
		String[] PURPOSE = {"如此一来", "由此可见", "这样一来", "以至于", "所以",
				"结果", "因此", "因而", "于是", "致使", "从而", "故"};
		String[] CAUSE = {"之所以", "因为", "由于"};
		String[] BUT = {"事实上", "实际上", "实质上", "反而是", "虽然", "然而", "而是",
				"但是", "不过", "反之", "固然", "尽管", "纵然", "即使", "可是", "偏偏",
				"不料", "只是", "反而", "但", "却"};
		String[] ADDITION = {"不但", "不仅", "何况", "进而"};
		String[] SEQUENCE = {"除此之外", "与此同时", "除此以外", "另一方面",
				"也就是说", "此外", "然后", "而后", "最后", "最终", "终于"};

		// 将连词列表中的词汇添加到 conjunctionList 中
		List<ConjWord> conjunctionList = new ArrayList<ConjWord>();
		conjunctionList.addAll(addConjs(PURPOSE, ConjWord.PURPOSE));
		conjunctionList.addAll(addConjs(CAUSE, ConjWord.CAUSE));
		conjunctionList.addAll(addConjs(BUT, ConjWord.BUT));
		conjunctionList.addAll(addConjs(ADDITION, ConjWord.ADDITION));
		conjunctionList.addAll(addConjs(SEQUENCE, ConjWord.SEQUENCE));

		return conjunctionList;
	}

	// 添加指定类型的连词
	private List<ConjWord> addConjs(@NonNull String[] conjs, int type) {
		List<ConjWord> result = new ArrayList<>();
		for (String conj : conjs) {
			result.add(new ConjWord(type, conj));
		}
		return result;
	}

	/**
	 * 连词分句
	 *
	 * @param sentencesList：待分句的句子
	 * @return 句子列表
	 */
	private List<Segment> segSentenceByConj(List<String> sentencesList) {
		List<Segment> resultList = new ArrayList<>();

		for (String sentence : sentencesList) {
			if (sentence.length() > 0) {
				resultList.addAll(segSentenceByConj(sentence));
			}
		}
		return resultList;
	}

	/**
	 * 连词分句
	 *
	 * @param sentences：待分句的句子
	 * @return 句子列表
	 */
	private List<Segment> segSentenceByConj(String sentences) {
		List<ConjWord> conjList = addAllConjunctions();        // 所有连词
		List<Segment> sentenceList = new ArrayList<>();
		Map<Integer, ConjWord> conjWordMap = sentencesConjs(sentences, conjList);        // 句子连词索引

		String tmp;
		if (conjWordMap.size() == 0) {
			// 句中不含连词
			sentenceList.add(new Segment(id++ + "", sentences));
		} else {
			// 句中包含连词
			Segment sentence;
			Iterator iterator = conjWordMap.keySet().iterator();
			int nextBegin = (int) iterator.next();      // 下一句起始位置
			ConjWord lastConj = conjWordMap.get(nextBegin);      // 上一个连词
			String first = sentences.substring(0, nextBegin);    // 第一句话
			if (first.length() > 0) {
				// 不以连词开始
				sentenceList.add(new Segment(id++ + "", first));
			}
			while (iterator.hasNext()) {
				int nextEnd = (int) iterator.next();
				tmp = sentences.substring(nextBegin + lastConj.getWord().length(), nextEnd);
				if (tmp.length() > 0) {
					sentence = new Segment(id++ + "", tmp);
					sentence.setFrontConj(lastConj.getType());
					sentenceList.add(sentence);
				}
				lastConj = conjWordMap.get(nextEnd);
				nextBegin = nextEnd;
			}
			// 最后一句话
			tmp = sentences.substring(nextBegin + lastConj.getWord().length());
			if (tmp.length() > 0) {
				sentence = new Segment(id++ + "", tmp);
				sentence.setFrontConj(lastConj.getType());
				sentenceList.add(sentence);
			}
		}
		return sentenceList;
	}

	// 设置句子的上下句
	public void setBeforeNext(List<Segment> sentences) {
		for (int i = 0; i < sentences.size(); i++) {
			int nextIndex = i + 1;
			if (nextIndex < sentences.size()) {
				Segment curStr = sentences.get(i);
				Segment nextStr = sentences.get(nextIndex);

				curStr.setNext(nextStr);
				curStr.setBackConj(nextStr.getFrontConj());
				nextStr.setBefore(curStr);
			}
		}
	}

	/**
	 * 返回句中出现的连词位置
	 *
	 * @return 按连词出现顺序的索引
	 */
	private Map<Integer, ConjWord> sentencesConjs(String sentences, List<ConjWord> conjList) {
		Map<Integer, ConjWord> resultMap = new LinkedHashMap<>();
		String lastConj = "";  // 上一个连词

		for (int i = 0; i < sentences.length(); i++) {
			String tmp = sentences.substring(i);
			for (ConjWord conj : conjList) {
				//调整连词顺序，将 但是 放于 但 前 ，而是 放于 而 前 这样会先匹配上字数多的 但是 和 而是，不会再去匹配 但 和 而
				if (tmp.indexOf(conj.getWord()) == 0 && !lastConj.contains(conj.getWord())) {
					resultMap.put(i, conj);
					lastConj = conj.getWord();
					i += conj.getWord().length() - 1;
				}
			}
		}
		return resultMap;
	}

	// 分词
	private void wordSegment(List<Segment> sentences) {
		Ictclas.initNLPIR();
		for (Segment sentence : sentences) {
			sentence.segWordByIctclas();
		}
		Ictclas.ExitIctclas();
	}

	// 依存句法分析
	private void dgs(List<Segment> sentences) {
		Ltp.initLtp();
		for (Segment sentence : sentences) {
			sentence.dpByLtp();
		}
		Ltp.releaseLtp();
	}

	// 只保留 adj adv n v, combine 决定是否将某些类型词汇结合处理
	private void saveSpecificWords(List<Segment> sentences, boolean combine) {
		Ictclas.initNLPIR();
		if (combine) {
			Ltp.initLtp();
			for (Segment sentence : sentences) {
				sentence.combineSpecificWords();
			}
			Ltp.releaseLtp();
		} else {
			for (Segment sentence : sentences) {
				sentence.saveSpecificWords();
			}
		}
		Ictclas.ExitIctclas();
	}

	// 得到所有词汇--词频字典
	private Map<String, Integer> genWordSet(List<Segment> sentences) {
		Map<String, Integer> wordMap = new HashMap<>();
		for (Segment sentence : sentences) {
			if (sentence.getSentiWords() != null) {
				for (String word : sentence.getSentiWords().split(BLANK)) {
					if (wordMap.keySet().contains(word)) {
						wordMap.put(word, wordMap.get(word) + 1);
					} else {
						wordMap.put(word, 1);
					}
				}
			}
		}
		return wordMap;
	}

	// 得到所有词汇--词频--词性字典
	private Map<String, Integer> genWordSet(List<Segment> sentences, Map<String, String> wordSpeech) {
		Map<String, Integer> wordMap = new HashMap<>();
		for (Segment sentence : sentences) {
			if (sentence.getSentiWords() != null) {
				String[] words = sentence.getSentiWords().split(BLANK);
				String[] tags = sentence.getSentiTags().split(BLANK);
				for (int i = 0; i < words.length; i++){
					String word = words[i];
					String tag = tags[i];
					if (wordMap.keySet().contains(word)) {
						wordMap.put(word, wordMap.get(word) + 1);
					} else {
						wordMap.put(word, 1);
					}
					wordSpeech.put(word, wordMap.get(word) + SEG + tag);
				}
			}
		}
		return wordMap;
	}

	// 词汇按词频降序排序
	private Map<String, String> sortWord(Map<String, Integer> wordMap) {
		Map<String, String> tmpMap = new HashMap<>(wordMap.size());
		log("Current dir is " + ROOT, false);
		for (String word : wordMap.keySet()) {
			tmpMap.put(word, wordMap.get(word) + "");
			log(word + BLANK + wordMap.get(word), false);
		}
		return sortMapByValue(tmpMap);
	}
}