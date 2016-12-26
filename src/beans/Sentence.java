package beans;

import NLP.Ictclas;
import com.sun.javafx.beans.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static common.Constants.BLANK;
import static common.Constants.POSSEG;

/**
 * Created by Ziyun on 2016/12/22.
 * <p>
 * 标点分句后得到的句子
 */

public class Sentence {
	private String content;                // 内容
	private List<Segment1> segmentList;    // 子句列表
	private List<Word> wordList;            // 词语列表
	private String wordSegWithTag;        // 分词结果
	private float score;                // 句子得分

	public Sentence() {
		new Sentence(null);
	}

	public Sentence(String content) {
		this.content = content;
		this.segmentList = null;
		this.wordList = null;
		this.wordSegWithTag = null;
		this.score = 0f;
	}

	// 中科院分词工具分词
	private void segWordByIctclas() {
		if (content.length() > 0) {
			wordSegWithTag = Ictclas.ICTCLASSegment(content, 1);
		}
	}

	// 生成词汇类型
	private void genWordList() {
		if (this.wordSegWithTag == null) {
			segWordByIctclas();
		}

		List<Word> wordList = new ArrayList<>();            // 词汇
		int i = 0;
		for (String word : wordSegWithTag.split(BLANK)) {
			String[] wordTag = word.split(POSSEG);
			if (wordTag.length == 2) {
				Word curWord = new Word(wordTag[0]);
				curWord.setSite(i++);
				if (wordTag[1].startsWith("c")) {
					curWord.setType(conjType(wordTag[0]));
				}
				wordList.add(curWord);
			}
		}
		this.wordList = wordList;
	}

	// 获得词汇 word 的类型，默认为 NORMAL 类型
	private int conjType(String word) {
		List<Word> conjList = addAllConjunctions();            // 所有连词

		for (Word conj : conjList) {
			String conjWord = conj.getWord();
			if (conjWord.equals(word)) {
				return conj.getType();
			}
		}

		return 0;
	}

	// 连词分句,segment：wordSegmentWithTag type before next
	public void segSentenceByConjs() {
		genWordList();
		List<Segment1> segmentList = new ArrayList<>();        // 子句
		String[] wordSegment = wordSegWithTag.split(BLANK);
		int begin = 0;
		for (Word word : wordList) {
			int curIndex = word.getSite();
			if (word.getType() < 6) {
				// 当前词汇为连词
				int len = curIndex - begin;
				String[] words = new String[len];
				System.arraycopy(wordSegment, begin, words, begin, len);
				Segment1 segment1 = new Segment1(words);
				segment1.setType(Segment.NORMAL);
				Segment1 segment2 = new Segment1(word.getWord());
				segment2.setType(Segment.CONJ);
				segmentList.add(segment1);
				segmentList.add(segment2);
			}
			begin = curIndex + 1;
		}
		if (begin < wordSegment.length) {
			int len = wordSegment.length - begin;
			String[] words = new String[len];
			System.arraycopy(wordSegment, begin, words, begin, len);
			Segment1 segment1 = new Segment1(words);
			segment1.setType(Segment1.NORMAL);
			segmentList.add(segment1);
		}
		this.segmentList = segmentList;
		setBeforeNext();
	}

	// 设置子句的上下句
	private void setBeforeNext() {
		for (int i = 0; i < segmentList.size(); i++) {
			int j = i + 1;
			Segment1 curSegment = segmentList.get(i);
			Segment1 nextSegment = null;
			Segment1 beforeSegment = null;
			if (i > 0) {
				beforeSegment = segmentList.get(i - 1);
			}
			if (j < segmentList.size()) {
				nextSegment = segmentList.get(j);
			}
			curSegment.setBefore(beforeSegment);
			curSegment.setNext(nextSegment);
		}
	}

	// 构建连词列表
	private List<Word> addAllConjunctions() {

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
		List<Word> conjunctionList = new ArrayList<>();
		conjunctionList.addAll(addConjs(PURPOSE, Word.PURPOSE));
		conjunctionList.addAll(addConjs(CAUSE, Word.CAUSE));
		conjunctionList.addAll(addConjs(BUT, Word.BUT));
		conjunctionList.addAll(addConjs(ADDITION, Word.ADDITION));
		conjunctionList.addAll(addConjs(SEQUENCE, Word.SEQUENCE));

		return conjunctionList;
	}

	// 添加指定类型的连词
	private List<Word> addConjs(@NonNull String[] conjs, int type) {
		List<Word> result = new ArrayList<>();
		for (String conj : conjs) {
			result.add(new Word(type, conj));
		}
		return result;
	}

	// 连词分句
	// public void segSentenceByConj() {
	// 	List<Word> conjList = addAllConjunctions();        // 所有连词
	// 	List<Segment> sentenceList = new ArrayList<>();
	// 	List<Integer> indexList = sentencesConjs(this.content, conjList);        // 句子连词索引
	// 	// for (int j = 0; j < indexs.size(); j++) {
	// 	// 	logger.debug(indexs.get(j) + BLANK + conjs.get(j).getWord());
	// 	// }
	//
	// 	String tmp;
	// 	if (indexList.size() == 0) {
	// 		// 句中不含连词
	// 		sentenceList.add(new Segment(content));
	// 	} else {
	// 		// 句中包含连词
	// 		Segment segment;
	// 		int nextBegin = indexList.get(0);      // 下一句起始位置
	// 		Word lastConj = conjList.get(0);      // 上一个连词
	// 		String first = content.substring(0, nextBegin);    // 第一句话
	// 		if (first.length() > 0) {
	// 			// 不以连词开始
	// 			Segment firstSeg = new Segment(first);
	// 			sentenceList.add(firstSeg);
	// 			lastConj.setBefore(firstSeg);
	// 		}
	//
	// 		for (int j = 1; j < indexList.size(); j++) {
	// 			int nextEnd = indexList.get(j);
	// 			Word nextConj = conjList.get(j);
	// 			tmp = content.substring(nextBegin + lastConj.getWord().length(), nextEnd);
	// 			if (tmp.length() > 0) {
	// 				segment = new Segment(tmp);
	// 				segment.setFrontConj(lastConj);
	// 				sentenceList.add(segment);
	// 				lastConj.setNext(segment);
	// 				nextConj.setBefore(segment);
	// 			}
	// 			lastConj = nextConj;
	// 			nextBegin = nextEnd;
	// 		}
	// 		// 最后一句话
	// 		tmp = content.substring(nextBegin + lastConj.getWord().length());
	// 		if (tmp.length() > 0) {
	// 			segment = new Segment(tmp);
	// 			segment.setFrontConj(lastConj);
	// 			sentenceList.add(segment);
	// 			lastConj.setNext(segment);
	// 		}
	// 	}
	// 	this.segmentList = sentenceList;
	// 	setBeforeNext();
	// }

	// 设置句子的上下句
	// private void setBeforeNext() {
	// 	for (int i = 0; i < segmentList.size(); i++) {
	// 		int nextIndex = i + 1;
	// 		if (nextIndex < segmentList.size()) {
	// 			Segment curStr = segmentList.get(i);
	// 			Segment nextStr = segmentList.get(nextIndex);
	//
	// 			curStr.setNext(nextStr);
	// 			curStr.setBackConj(nextStr.getFrontConj());
	// 			nextStr.setBefore(curStr);
	// 		}
	// 	}
	// }

	/**
	 * 返回句中出现的连词位置
	 *
	 * @return 按连词出现顺序的索引
	 */
	// private List<Integer> sentencesConjs(String sentences, List<Word> conjList) {
	// 	List<Integer> indexList = new LinkedList<>();
	// 	String lastConj = "";  // 上一个连词
	//
	// 	for (int i = 0; i < sentences.length(); i++) {
	// 		String tmp = sentences.substring(i);
	// 		for (Word conj : conjList) {
	// 			//调整连词顺序，将 但是 放于 但 前 ，而是 放于 而 前 这样会先匹配上字数多的 但是 和 而是，不会再去匹配 但 和 而
	// 			if (tmp.indexOf(conj.getWord()) == 0 && !lastConj.contains(conj.getWord())) {
	// 				this.wordList.add(conj);
	// 				indexList.add(i);
	// 				lastConj = conj.getWord();
	// 				i += conj.getWord().length() - 1;
	// 			}
	// 		}
	// 	}
	// 	return indexList;
	// }
	public List<Segment1> getSegmentList() {
		return segmentList;
	}

	public String getWordSegWithTag() {
		return wordSegWithTag;
	}

	public void setWordSegWithTag(String wordSegWithTag) {
		this.wordSegWithTag = wordSegWithTag;
	}

	public void setSegmentList(List<Segment1> segmentList) {
		this.segmentList = segmentList;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Word> getWordList() {
		return wordList;
	}

	public void setWordList(List<Word> wordList) {
		this.wordList = wordList;
	}
}
