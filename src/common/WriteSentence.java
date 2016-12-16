package common;

import beans.Sentence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static common.Constants.BLANK;
import static common.Constants.NEWLINE;
import static common.Constants.SEG;

/**
 * Created by Ziyun on 2016/12/15.
 *
 * 一个用于将 Sentence 列表信息输出到文件中的工具类
 */

public class WriteSentence {

	public static final int CONTENT = 12;                    // 句子内容
	public static final int WORDSEG = 13;                // 分词
	public static final int SENTIWORDS = 22;                // 情感倾向性词汇
	public static final int WORDSEGWITHTAG = 14;            // 带词性标注的分词
	public static final int DGS = 15;                        // 依存句法分析
	public static final int SIZE = 16;                        // 依存语句数
	public static final int WORDS = 17;                    // 词列表
	public static final int TAGS = 18;                        // 词性列表
	public static final int HEADS = 19;                    // 结果依存弧，heads[i]代表第i个词的父亲节点的编号
	public static final int DEPRELS = 20;                    // 结果依存弧关系类型
	public static final int ALL = 21;                        //所有属性

	int type;                                    // 待写入文件的属性
	boolean withId = false;                            // 是否输出id，默认为不输出

	/**
	 * 将指定类型的句子信息写入文件中, 不带id
	 *
	 * @param file：文件名
	 * @param sentences：待写入的句子
	 * @param type：待输出的句子类型
	 */
	public static void writeSentenceInfo(String file, List<Sentence> sentences, int type) {
		writeSentenceInfo(file, sentences, false, type);
	}

	/**
	 * 将指定类型的句子信息写入文件中
	 *
	 * @param file：文件名
	 * @param sentences：待写入的句子
	 * @param withId：是否输出句子    id
	 * @param type：待输出的句子类型
	 */
	static void writeSentenceInfo(String file, List<Sentence> sentences, boolean withId, int type) {

		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			StringBuilder sb = new StringBuilder();
			for (Sentence sentence : sentences) {
				String info = getInfo(sentence, type);
				if (info != null && info.length() != 0) {
					if (withId) {
						sb.append(sentence.getId());
						sb.append(SEG);
					}
					sb.append(info);
					sb.append(NEWLINE);
				}
			}
			bw.write(sb.toString());
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 返回指定类型的 Sentence 信息
	public static String getInfo(Sentence sentence, int type) {
		switch (type) {
			case CONTENT:
				return sentence.getContent();
			case WORDSEG:
				return sentence.getWordSeg();
			case SENTIWORDS:
				return sentence.getSentiWords();
			case WORDSEGWITHTAG:
				return sentence.getWordSegWithTag();
			case DGS:
				return sentence.getDgs();
			case SIZE:
				return sentence.getSize() + "";
			case WORDS:
				return appendList(sentence.getWords());
			case TAGS:
				return appendList(sentence.getTags());
			case HEADS:
				return appendList(sentence.getHeads());
			case DEPRELS:
				return appendList(sentence.getDeprels());
			case ALL:
				StringBuilder sb = new StringBuilder();
				sb.append(sentence.getContent());
				sb.append(SEG);
				sb.append(sentence.getWordSegWithTag());
				sb.append(SEG);
				sb.append(sentence.getDgs());
				return sb.toString();
			default:
				return null;
		}
	}

	public static <T> String appendList(List<T> list) {
		StringBuilder sb = new StringBuilder();
		for (T word : list) {
			sb.append(word);
			sb.append(BLANK);
		}
		return sb.toString();
	}
}
