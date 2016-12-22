package beans;

import com.sun.javafx.beans.annotations.NonNull;

import java.util.*;

/**
 * Created by Ziyun on 2016/12/22.
 *
 * 标点分句后得到的句子
 */

public class Sentence {
	private String content;        // 内容
	private List<Segment> segmentList = null;
	private float score;

	public Sentence() {
	}

	public Sentence(String content) {
		this.content = content;
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
		List<ConjWord> conjunctionList = new ArrayList<>();
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

	// 连词分句
	public void segSentenceByConj() {
		List<ConjWord> conjList = addAllConjunctions();        // 所有连词
		List<Segment> sentenceList = new ArrayList<>();
		Map<Integer, ConjWord> conjWordMap = sentencesConjs(this.content, conjList);        // 句子连词索引
		// for (int j = 0; j < indexs.size(); j++) {
		// 	logger.debug(indexs.get(j) + BLANK + conjs.get(j).getWord());
		// }

		String tmp;
		if (conjWordMap.size() == 0) {
			// 句中不含连词
			sentenceList.add(new Segment(content));
		} else {
			// 句中包含连词
			Segment segment;

			Iterator iterator = conjWordMap.keySet().iterator();
			int nextBegin = (int) iterator.next();      // 下一句起始位置
			ConjWord lastConj = conjWordMap.get(nextBegin);      // 上一个连词
			String first = content.substring(0, nextBegin);    // 第一句话
			if (first.length() > 0) {
				// 不以连词开始
				sentenceList.add(new Segment(first));
			}
			while (iterator.hasNext()) {
				int nextEnd = (int) iterator.next();
				tmp = content.substring(nextBegin + lastConj.getWord().length(), nextEnd);
				if (tmp.length() > 0) {
					segment = new Segment(tmp);
					segment.setFrontConj(lastConj.getType());
					sentenceList.add(segment);
				}
				lastConj = conjWordMap.get(nextEnd);
				nextBegin = nextEnd;
			}
			// 最后一句话
			tmp = content.substring(nextBegin + lastConj.getWord().length());
			if (tmp.length() > 0) {
				segment = new Segment(tmp);
				segment.setFrontConj(lastConj.getType());
				sentenceList.add(segment);
			}
		}
		this.segmentList = sentenceList;
		setBeforeNext();
	}

	// 设置句子的上下句
	private void setBeforeNext() {
		for (int i = 0; i < segmentList.size(); i++) {
			int nextIndex = i + 1;
			if (nextIndex < segmentList.size()) {
				Segment curStr = segmentList.get(i);
				Segment nextStr = segmentList.get(nextIndex);

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

	public List<Segment> getSegmentList() {
		return segmentList;
	}

	public void setSegmentList(List<Segment> segmentList) {
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
}
