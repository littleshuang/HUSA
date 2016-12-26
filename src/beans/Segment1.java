package beans;

import NLP.Ictclas;

import java.util.ArrayList;
import java.util.List;

import static NLP.Ltp.parseByLtp;
import static common.Constants.*;

/**
 * Created by Ziyun on 2016/12/26.
 * 子句
 */

public class Segment1 {
	public static final int NORMAL = 1;        // 一般类型
	public static final int CONJ = 2;        // 连词类型

	private String id;               // 句子 id
	private int type;                // 子句类型
	private String[] wordSegmentWithTag;    // 分词词组
	private String wordSegWithTag;         // 带词性标注的分词
	private String wordSeg;        // 分词
	private String content;         // 句子内容
	private String selectedWords;
	private List<Word> wordList;	// 词汇列表
	private String dgs;         // 依存句法分析
	private int size;           // 依存语句数
	List<String> words;         // 词列表
	List<String> tags;          // 词性列表
	List<Integer> heads;         // 结果依存弧，heads[i]代表第i个词的父亲节点的编号
	List<String> deprels;       // 结果依存弧关系类型
	Segment1 before, next;        // 指向前后句子的指针
	float score = 0f;


	public Segment1(String[] wordArray) {
		this.wordSegmentWithTag = wordArray;
	}

	public Segment1(String content) {
		this.content = content;
	}

	public Segment1(String id, String content) {
		this.id = id;
		this.content = content;
	}

	// 分词
	public void segWord() {
		if (words == null) {
			words = new ArrayList<>();
		}
		if (tags == null) {
			tags = new ArrayList<>();
		}
		if (content.length() > 0) {
			wordSegWithTag = Ictclas.ICTCLASSegment(content, 1);
			wordSegmentWithTag = wordSegWithTag.split(BLANK);
		}

		StringBuilder sb = new StringBuilder();
		for (String wordTag : wordSegmentWithTag) {
			String[] wordTags = wordTag.split(POSSEG);
			String word = wordTags[0];
			String tag = wordTags[1];
			words.add(word);
			tags.add(tag);
			sb.append(word);
			sb.append(BLANK);
		}
		wordSeg = sb.toString().trim();
	}

	// LTP 依存句法分析
	public void dpByLtp() {
		if (words == null || tags == null) {
			segWord();
		}
		this.heads = new ArrayList<>();
		this.deprels = new ArrayList<>();
		this.size = parseByLtp(this.words, this.tags, this.heads, this.deprels);
	}

	// 生成dgs句子，形如 word1_3 word2_5 ATT
	public void genDgsStr() {
		if (dgs == null) {
			if (deprels == null) {
				dpByLtp();
			}
			StringBuilder sb = new StringBuilder();
			int pid;    // 父节点 id
			for (int i = 0; i < size; i++) {
				pid = heads.get(i) - 1;
				sb.append(words.get(i));
				sb.append(UNDERLINE);
				sb.append(i);
				sb.append(BLANK);
				if (pid != -1) {
					sb.append(words.get(pid));
					sb.append(UNDERLINE);
				}
				sb.append(pid);
				sb.append(BLANK);
				sb.append(deprels.get(i));
				sb.append(NEWLINE);
			}
			dgs = sb.toString().trim();
		}
	}

	// 保留指定依存关系对,并将父节点与子节点相邻的项结合
	public List<String> saveSpecificDp() {
		List<String> resultDps = new ArrayList<>();

		if (this.deprels == null) {
			dpByLtp();
		}

		for (int i = 0; i < size; i++) {
			if (qualifiedDp(deprels.get(i))) {
				StringBuilder sb = new StringBuilder();
				String curWord = words.get(i);
				String pWord = "";
				sb.append(curWord);
				sb.append(UNDERLINE);
				sb.append(i);
				sb.append(BLANK);
				int pid = heads.get(i) - 1;
				if (pid != -1) {
					pWord = words.get(pid);
					sb.append(pWord);
					sb.append(UNDERLINE);
				}
				sb.append(pid);
				sb.append(BLANK);
				sb.append(deprels.get(i));
				sb.append(SEG);
				sb.append(curWord);
				sb.append(POSSEG);
				sb.append(tags.get(i));
				sb.append(BLANK);
				if (pid != -1) {
					sb.append(pWord);
					sb.append(POSSEG);
					sb.append(tags.get(pid));
					if ((i - pid) == -1) {
						sb.append(SEG);
						sb.append(curWord);
						sb.append(pWord);
					} else if ((i - pid) == 1) {
						sb.append(SEG);
						sb.append(pWord);
						sb.append(curWord);
					}
				}
				sb.append(NEWLINE);
				resultDps.add(sb.toString());
			}
		}
		return resultDps;
	}

	// 判断是否为需要的依存句法类型
	private boolean qualifiedDp(String str) {
		String[] dps = {"ADV", "CMP", "VOB", "ATT", "SBV", "COO"};

		for (String dp : dps) {
			if (dp.equals(str)) {
				return true;
			}
		}

		return false;
	}

	// 保留 adj adv n v stock
	public void saveSpecificWords() {
		this.saveSpecificWords(null);
	}

	// 保留指定词性的词汇
	public void saveSpecificWords(String[] tags) {
		if (wordSegmentWithTag.length == 0) {
			segWord();
		}
		StringBuilder sb = new StringBuilder();
		for (String word : wordSegmentWithTag) {
			String[] words = word.split(POSSEG);
			if (words.length == 2 && isSentiWord(words[1], tags)) {
				sb.append(word.split(POSSEG)[0]);
				sb.append(BLANK);
			}
			selectedWords = sb.toString();
		}
	}

	private boolean isSentiWord(String tag){
		return isSentiWord(tag, null);
	}

	/**
	 * 判断一个词汇是否为具有情感倾向性的词性词汇
	 *
	 * @param tag
	 * @return
	 */
	private boolean isSentiWord(String tag, String[] sentiTags) {
		// String[] sentiTags = {"a", "d", "n", "nl", "nd", "v", "stock"};
		if (sentiTags == null) {
			sentiTags = new String[]{"a", "n", "nl", "nd", "stock"};
		}

		for (String sentiTag : sentiTags) {
			if (tag.equals(sentiTag)) {
				return true;
			}
		}
		return false;
	}

	// 将某些特定类型词汇进行结合
	public void combineSpecificWords() {
		if (deprels == null) {
			dpByLtp();
		}

		StringBuilder wordSb = new StringBuilder();
		int i = 0;
		while (i < size) {
			String curWord = words.get(i);
			String curTag = tags.get(i);
			int next = i + 1;
			boolean addCur = false;        // 是否 append curWord的标志位
			if (isSentiWord(curTag)) {
				if (next < size) {
					String nextWord = words.get(next);
					String nextTag = tags.get(next);
					if (isSentiWord(nextTag)) {
						// 当前词汇为下一个词汇的父节点
						if (qualifiedDp(deprels.get(next)) && heads.get(next) == next) {
							i++;
							wordSb.append(curWord);
							wordSb.append(nextWord);
							wordSb.append(BLANK);
						} else if (qualifiedDp(deprels.get(i)) && heads.get(i) == next) {
							// 下一个词汇是当前词汇的父节点
							i++;
							wordSb.append(nextWord);
							addCur = true;
							// sb.append(curWord);
							// sb.append(BLANK);
						} else {
							// 当前词汇为具备情感倾向的词汇
							addCur = true;
							// sb.append(curWord);
							// sb.append(BLANK);
						}
					} else {
						// 当前词汇为具备情感倾向的词汇
						addCur = true;
						// sb.append(curWord);
						// sb.append(BLANK);
					}
				} else {
					addCur = true;
					// sb.append(curWord);
					// sb.append(BLANK);
				}
			}
			if (addCur) {
				wordSb.append(curWord);
				wordSb.append(BLANK);
			}
			i++;
		}
		selectedWords = wordSb.toString().trim();
	}

	/**
	 * 词汇合并，d+a, d+v
	 */
	public void combineWord() {
		if (wordSeg.length() == 0) {
			segWord();
		}

		StringBuilder sb = new StringBuilder();
		StringBuilder combineSb = new StringBuilder();
		combineSb.append(content);
		combineSb.append(NEWLINE);
		String[] words = wordSegWithTag.split(BLANK);
		String[] nextWord;

		for (int i = 0; i < words.length - 1; ) {
			String wordTag = words[i++];
			String[] word = wordTag.split(POSSEG);
			if (isSentiWord(wordTag)) {
				sb.append(word[0]);
				if (word[1].startsWith("d")) {
					nextWord = words[i].split(POSSEG);
					if (nextWord.length == 2 &&
							(nextWord[1].startsWith("a") || nextWord[1].startsWith("v"))) {
						i++;
						sb.append(nextWord[0]);

						combineSb.append(word[0]);
						combineSb.append(nextWord[0]);
						combineSb.append(NEWLINE);
					}
				}
				sb.append(BLANK);
			}
			selectedWords = sb.toString();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String[] getWordSegmentWithTag() {
		return wordSegmentWithTag;
	}

	public void setWordSegmentWithTag(String[] wordSegmentWithTag) {
		this.wordSegmentWithTag = wordSegmentWithTag;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWordSegWithTag() {
		return wordSegWithTag;
	}

	public void setWordSegWithTag(String wordSegWithTag) {
		this.wordSegWithTag = wordSegWithTag;
	}

	public String getWordSeg() {
		return wordSeg;
	}

	public void setWordSeg(String wordSeg) {
		this.wordSeg = wordSeg;
	}

	public String getSelectedWords() {
		return selectedWords;
	}

	public void setSelectedWords(String selectedWords) {
		this.selectedWords = selectedWords;
	}

	public String getDgs() {
		return dgs;
	}

	public void setDgs(String dgs) {
		this.dgs = dgs;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<Integer> getHeads() {
		return heads;
	}

	public void setHeads(List<Integer> heads) {
		this.heads = heads;
	}

	public List<String> getDeprels() {
		return deprels;
	}

	public void setDeprels(List<String> deprels) {
		this.deprels = deprels;
	}

	public Segment1 getBefore() {
		return before;
	}

	public void setBefore(Segment1 before) {
		this.before = before;
	}

	public Segment1 getNext() {
		return next;
	}

	public void setNext(Segment1 next) {
		this.next = next;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public List<Word> getWordList() {
		return wordList;
	}

	public void setWordList(List<Word> wordList) {
		this.wordList = wordList;
	}
}
