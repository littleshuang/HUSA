package SentimentAnalysis;

import NLP.Ictclas;
import NLP.Ltp;
import beans.Document;
import beans.Segment1;
import beans.Sentence;
import com.sun.javafx.beans.annotations.NonNull;

import java.io.File;
import java.util.*;

import static common.Constants.*;
import static tools.CommonTools.roundFloat;
import static tools.FileTools.*;
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
		String posWords = root + "/pattern/posPattern.txt";
		String negWords = root + "/pattern/negPattern.txt";

		Set<String> posSet = readDataToSet(posWords);
		Set<String> negSet = readDataToSet(negWords);

		Step7 step7 = new Step7();
		println("Begin read documents!");
		List<Document> documents = step7.readDocument(rawDataDir);
		println("Begin segSentenceByConj!");
		step7.segSentenceByConj(documents);
		println("Begin wordSegment");
		step7.wordSegment(documents, true);
		println("Begin calculate segment score!");
		step7.calDocumentListScore(documents, posSet, negSet);
		step7.writeInfo(documents, root);
	}

	// 读取文档数据
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

	// 句子分词 & 连词分句
	private void segSentenceByConj(List<Document> documents) {
		Ictclas.initNLPIR();
		for (Document document : documents) {
			for (Sentence sentence : document.getSentences()) {
				sentence.segSentenceByConjs();
			}
		}
		Ictclas.ExitIctclas();
	}

	// 保留特定词
	private void wordSegment(List<Document> documents, boolean combine) {
		for (Document document : documents) {
			for (Sentence sentences : document.getSentences()) {
				if (combine) {
					Ltp.initLtp();
					for (Segment1 segment : sentences.getSegmentList()) {
						segment.combineSpecificWords();
						// segment.setWordList(genWordList(segment, posSet, negSet));
					}
					Ltp.releaseLtp();
				} else {
					for (Segment1 segment : sentences.getSegmentList()) {
						String[] sentiTags = {"a", "d", "n", "nl", "nd", "v", "stock"};
						segment.saveSpecificWords(sentiTags);
						// segment.setWordList(genWordList(segment, posSet, negSet));
					}
				}
			}
		}
	}

	// 子句得分计算
	private void calSegmentScore(Segment1 segment, Set<String> posSet, Set<String> negSet) {
		List<String> words = Arrays.asList(segment.getSelectedWords().split(BLANK));

		int posNum = 0;
		int negNum = 0;
		for (String pos : posSet) {
			String[] posWords;
			if (pos.contains(BLANK)) {
				posWords = pos.split(BLANK);
			} else {
				posWords = new String[]{pos};
			}
			int i = 0;
			for (String posWord : posWords) {
				if (words.contains(posWord)) {
					i++;
				}
			}
			if (i == posWords.length) {
				posNum++;
			}
		}
		for (String neg : negSet) {
			String[] negWords;
			if (neg.contains(BLANK)) {
				negWords = neg.split(BLANK);
			} else {
				negWords = new String[]{neg};
			}
			int i = 0;
			for (String posWord : negWords) {
				if (words.contains(posWord)) {
					i++;
				}
			}
			if (i == negWords.length) {
				negNum++;
			}
		}
		segment.setScore(posNum - negNum);
	}

	// 计算子句打分
	// private void calSegmentScore(Segment1 segment) {
	// 	int posNum = 0;
	// 	int negNum = 0;
	//
	// 	for (Word word : segment.getWordList()) {
	// 		if (word.getType() == Word.POS) {
	// 			posNum++;
	// 		} else if (word.getType() == Word.NEG) {
	// 			negNum++;
	// 		}
	// 	}
	//
	// 	segment.setScore(posNum - negNum);
	// }

	// 根据连词计算句子打分
	private void calSentenceScore(Sentence sentence, Set<String> posSet, Set<String> negSet) {
		List<Segment1> segments = sentence.getSegmentList();

		for (Segment1 segment : segments) {
			calSegmentScore(segment, posSet, negSet);
		}

		for (Segment1 segment : segments) {
			if (segment.getType() == Segment1.CONJ) {
				Segment1 before = segment.getBefore();
				Segment1 next = segment.getNext();
				float beforeScore = before.getScore();
				float nextScore = next.getScore();
				switch (segment.getWordList().get(0).getType()) {
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
		}

		float score = 0f;
		for (Segment1 segment : segments) {
			score += segment.getScore();
		}
		sentence.setScore(score);
	}

	// 计算文档打分
	private float calDocumentScore(Document document, Set<String> posSet, Set<String> negSet) {
		List<Sentence> sentences = document.getSentences();
		float score = 0f;
		for (Sentence sentence : sentences) {
			calSentenceScore(sentence, posSet, negSet);
			score += sentence.getScore();
		}
		return roundFloat(score / sentences.size(), 2);
	}

	// 计算文档集的得分
	private void calDocumentListScore(List<Document> documents,
									  Set<String> posSet, Set<String> negSet){
		for (Document document : documents){
			calDocumentScore(document, posSet, negSet);
		}
	}

	private void writeInfo(List<Document> documents, String path) {
		List<String> analysisInfo = new ArrayList<>();
		List<String> finalInfo = new ArrayList<>();
		for (Document doc : documents) {
			StringBuilder docSb = new StringBuilder();
			docSb.append(doc.getTitle());
			docSb.append(SEG);
			docSb.append(doc.getRawType());
			docSb.append(SEG);
			docSb.append(doc.getScore());
			finalInfo.add(docSb.toString());
			for (Sentence sentence : doc.getSentences()) {
				docSb.append(NEWLINE);
				docSb.append(sentence.getContent());
				docSb.append(SEG);
				docSb.append(sentence.getScore());
			}
			analysisInfo.add(docSb.toString());
		}

		writeListToFile(path + "analysisInfo.txt", analysisInfo);
		writeListToFile(path + "finalInfo.txt", finalInfo);
	}
}
