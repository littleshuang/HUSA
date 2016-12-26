package SentimentAnalysis;

import NLP.Ictclas;
import NLP.Ltp;
import beans.ConjWord;
import beans.Document;
import beans.Segment;
import beans.Sentence;
import com.sun.javafx.beans.annotations.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static common.Constants.*;
import static tools.CommonTools.roundFloat;
import static tools.FileTools.getCurRootPath;
import static tools.FileTools.readDataToList;
import static tools.Output.println;

/**
 * Created by Ziyun on 2016/12/21.
 * <p>
 * 测试数据预处理，分句、分词、依存句法分析、保留指定词汇
 * 情感分析
 */

public class Step7 {

	public static void main(String[] args) {
		String root = getCurRootPath() + ROOT;        // 根目录
		String rawDataDir = root + "/raw_data";        // 原始语料
		String sentencesFile = root + "/sentences.txt";    // 句子
		String sentenceByConjFile = root + "/sentencesByConj.txt";    // 连词分句
		// String sentenceConjFile = root + "/sentenceConjType.txt";        // 句子连词类型
		String wordSegFile = root + "/wordSegment.txt";                // 分词
		String wordSegWithTagFile = root + "/wordSegmentWithTag.txt";    // 含词性标注的分词
		String sentiWordsFile = root + "/selectedWords.txt";            // adj adv n v
		String wordFile = root + "/words.txt";            // 词汇
		String timeFile = root + "/timeCost.txt";            // 记录时间消耗

		Step7 step7 = new Step7();
		println("Begin read documents!");
		List<Document> documents = step7.readDocument(rawDataDir);
		println("Begin segSentenceByConj!");
		step7.segSentenceByConj(documents);
		println("Begin wordSegment");
		step7.wordSegment(documents, true);
		println("Begin calculate segment score!");
	}

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

	// 句子连词分句
	private void segSentenceByConj(List<Document> documents) {
		for (Document document : documents) {
			for (Sentence sentence : document.getSentences()) {
				sentence.segSentenceByConj();
			}
		}
	}

	// 分词 & 保留特定词
	private void wordSegment(List<Document> documents, boolean combine) {
		Ictclas.initNLPIR();
		for (Document document : documents) {
			for (Sentence sentences : document.getSentences()) {
				if (combine) {
					Ltp.initLtp();
					for (Segment sentence : sentences.getSegmentList()) {
						sentence.combineSpecificWords();
					}
					Ltp.releaseLtp();
				} else {
					for (Segment sentence : sentences.getSegmentList()) {
						sentence.saveSpecificWords();
					}
				}
			}
		}
		Ictclas.ExitIctclas();
	}

	// 计算子句打分
	private void calSegmentScore(Segment segment, Set<String> posSet, Set<String> negSet) {
		String[] words = segment.getSentiWords().split(BLANK);
		List<String> wordList = Arrays.asList(words);

		int posNum = 0;
		int negNum = 0;
		for (String pos : posSet) {
			if (pos.contains(BLANK) && wordList.contains(pos.split(BLANK))) {
				posNum++;
			} else if (wordList.contains(pos)) {
				posNum++;
			}
		}

		for (String neg : negSet) {
			if (neg.contains(BLANK) && wordList.contains(neg.split(BLANK))) {
				negNum++;
			} else if (wordList.contains(neg)) {
				negNum++;
			}
		}
		segment.setScore(posNum - negNum);
	}

	private String[] wordType(Segment segment, Set<String> posSet, Set<String> negSet){
		String[] words = segment.getSentiWords().split(BLANK);
		String[] types = new String[words.length];

		for (int i = 0; i < words.length; i++){
			String wordI = words[i];

		}
		return types;
	}

	// 根据连词计算句子打分
	private void calSentenceScore(Sentence sentence) {
		List<ConjWord> conjWords = sentence.getConjWordList();
		List<Segment> segments = sentence.getSegmentList();

		for (ConjWord conjWord : conjWords) {
			Segment before = conjWord.getBefore();
			Segment next = conjWord.getNext();
			float beforeScore = before.getScore();
			float nextScore = next.getScore();
			switch (conjWord.getType()) {
				case 1:
					if (nextScore == 0 && beforeScore != 0) {
						next.setScore(beforeScore);
					}
					before.setScore(0);
					break;
				case 3:
					if (nextScore == 0) {
						next.setScore(-beforeScore);
					} else if (beforeScore == 0) {
						before.setScore(-nextScore);
					} else if ((beforeScore > 0 && nextScore > 0) ||
							(beforeScore < 0 && nextScore < 0)) {
						float diff = Math.abs(beforeScore) - Math.abs(nextScore);
						if (diff > 0) {
							next.setScore(-nextScore);
						} else {
							before.setScore(-beforeScore);
						}
					}
					break;
				default:
					if ((beforeScore > 0 && nextScore < 0) ||
							(beforeScore < 0 && nextScore > 0)) {
						float diff = Math.abs(beforeScore) - Math.abs(nextScore);
						if (diff > 0) {
							next.setScore(-nextScore);
						} else {
							before.setScore(-beforeScore);
						}
					}
					break;
			}
		}

		float score = 0f;
		for (Segment segment : segments){
			score += segment.getScore();
		}

		sentence.setScore(score);
	}

	// 计算文档打分
	private float calDocumentScore(Document document) {
		List<Sentence> sentences = document.getSentences();
		float score = 0f;
		for (Sentence sentence : sentences) {
			calSentenceScore(sentence);
			score += sentence.getScore();
		}
		return roundFloat(score / sentences.size(), 2);
	}

	private void getInfo(List<Document> documents, List<String> analysisInfo, List<String> finalInfo){

		for (Document doc : documents){
			StringBuilder docSb = new StringBuilder();
			docSb.append(doc.getTitle());
			docSb.append(SEG);
			docSb.append(doc.getRawType());
			docSb.append(SEG);
			docSb.append(doc.getScore());
			finalInfo.add(docSb.toString());
			for (Sentence sentence : doc.getSentences()){
				docSb.append(NEWLINE);
				docSb.append(sentence.getContent());
				docSb.append(SEG);
				docSb.append(sentence.getScore());
			}
			analysisInfo.add(docSb.toString());
		}
	}

	private void writeInfo(String path, List<Document> documents) {

	}

}
