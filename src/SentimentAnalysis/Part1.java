package SentimentAnalysis;

import NLP.Ictclas;
import beans.Document;
import beans.Segment1;
import beans.Sentence;
import com.sun.javafx.beans.annotations.NonNull;

import java.io.File;
import java.util.*;

import static common.Constants.BLANK;
import static common.Constants.ROOT;
import static tools.FileTools.*;
import static tools.Output.log;

/**
 * Created by Ziyun on 2016/12/26.
 */

public class Part1 {

	public static void main(String[] args) {

		String root = getCurRootPath() + ROOT;        // 根目录
		String rawDataDir = root + "/raw_data";    // 原始语料

		log("Begin");
		log("Step 1: 读取数据 & 分句");
		Part1 part1 = new Part1();
		List<Document> documents = part1.readDocument(rawDataDir);
		List<Sentence> sentenceList = part1.docToSentence(documents);

		log("Step 2: 分词 & 连词分句");
		List<Segment1> segmentList = part1.segSentenceByConj(sentenceList);

		log("Step 3: 保留特定词汇");
		segmentList = part1.saveSpecificWords(segmentList);

		log("Step 4: 获取词汇集合 & 写出");
		part1.writeInfo(segmentList, root);
	}

	// 把原始数据读入文档
	private List<Document> readDocument(@NonNull String documentPath) {
		List<Document> documentList = new LinkedList<>();

		try {
			File directory = new File(documentPath);
			File[] files;
			if (directory.isDirectory()) {
				files = directory.listFiles();
				for (File file : files) {
					Document document = new Document();
					document.setContent(readDataToList(file));
					document.segParas();        // 段落分句
					String fileTitle = file.getName();
					document.setTitle(fileTitle);
					document.genRawType();        // 得到原始类别标签
					documentList.add(document);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentList;
	}

	// 从文档列表中得到句子列表
	private List<Sentence> docToSentence(List<Document> documentList) {
		List<Sentence> sentenceList = new ArrayList<>();
		for (Document document : documentList) {
			sentenceList.addAll(document.getSentences());
		}
		return sentenceList;
	}

	// 分词 & 连词分句
	private List<Segment1> segSentenceByConj(List<Sentence> sentenceList) {
		List<Segment1> segmentList = new ArrayList<>();
		Ictclas.initNLPIR();
		for (Sentence sentence : sentenceList) {
			sentence.segSentenceByConjs();
			segmentList.addAll(sentence.getSegmentList());
		}
		Ictclas.ExitIctclas();
		return segmentList;
	}

	// 保留指定词性的词汇
	private List<Segment1> saveSpecificWords(List<Segment1> segmentList) {
		String[] tags = {"a", "n", "nl", "nd", "stock"};
		for (Segment1 segment : segmentList) {
			segment.saveSpecificWords(tags);
		}
		return segmentList;
	}

	// 写出数据
	private void writeInfo(List<Segment1> segmentList, String path) {
		List<String> wordSegments = new ArrayList<>();        // 分词结果
		List<String> selectedWords = new ArrayList<>();        // 选择的用于挖掘的词汇

		for (Segment1 segment : segmentList){
			wordSegments.add(segment.getWordSegWithTag());
			selectedWords.add(segment.getSelectedWords());
		}
		Map<String, Integer> wordMap = genWordSet(selectedWords);

		writeListToFile(path + "wordSegWithTag.txt", wordSegments);
		writeListToFile(path + "selectedWords.txt", selectedWords);
	}

	// 计算词频
	private Map<String, Integer> genWordSet(List<String> selectedWords){
		Map<String, Integer> wordFre = new HashMap<>();        // 词汇--词频
		for (String str : selectedWords){
			for (String word : str.split(BLANK)){
				if (wordFre.keySet().contains(word)){
					wordFre.put(word, wordFre.get(word) + 1);
				}else {
					wordFre.put(word, 1);
				}
			}
		}
		return wordFre;
	}
}
