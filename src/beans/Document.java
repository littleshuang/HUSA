package beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static tools.CommonTools.copyList;
import static tools.FileTools.newTrim;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class Document {

	private String title;			// 标题
	private List<String> content;	// 内容
	private List<Sentence> sentences;		// 句子列表
	private int rawType = 0;		// 原始类型
	private int predictType = 0;	// 预测类型
	private float score = 0.0f;		// 得分值

	public Document() {
	}

	public Document(String title) {
		this.title = title;
	}

	public Document(String title, List<String> content) {
		this.title = title;
		this.content = content;
	}

	// 标点分句
	public void segParas() {
		String[] puncs = {"。", "！", "？"};        // 定义用于分句的标点符号
		List<String> paraList = this.content;		// 段落列表

		for (String punc : puncs) {
			List<String> tmp = new ArrayList<>();
			for (String para : paraList) {
				if ((para = newTrim(para)).length() > 0) {
					tmp.addAll(segParaByPunc(para, punc));
				} else {
					tmp.add(para);
				}
			}
			paraList = copyList(tmp);
		}

		for (String str : paraList) {
			if (this.getSentences() == null) {
				this.setSentences(new LinkedList<Sentence>());
			}
			this.sentences.add(new Sentence(str));
		}
	}

	// 得到原始类别标签
	public void genRawType(){
		int beginIndex = title.indexOf("_") + 1;
		int endIndex = title.indexOf(".");
		int rawType = Integer.valueOf(title.substring(beginIndex, endIndex));
		this.setRawType(rawType);
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}

	public int getRawType() {
		return rawType;
	}

	public void setRawType(int rawType) {
		this.rawType = rawType;
	}

	public int getPredictType() {
		return predictType;
	}

	public void setPredictType(int predictType) {
		this.predictType = predictType;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
}
