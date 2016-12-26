package beans;

/**
 * Created by Ziyun on 2016/12/26.
 */

public class Word<T> {
	// 词语类型
	public static final int PURPOSE = 1;	// 目的连词
	public static final int CAUSE = 2;		// 原因连词
	public static final int BUT = 3;		// 转折连词
	public static final int ADDITION = 4;	// 并列连词
	public static final int SEQUENCE = 5;	// 递进连词
	public static final int POS = 6;		// 正向情感
	public static final int NEG = 7;		// 负面情感
	public static final int NORMAL = 8;		// 一般词汇

	private int type;				// 当前词汇类型
	private int site;				// 词汇在当前句中的位置
	private String word;			// 当前词语
	private T before, next;			// 如果为连词，则为前后segment，如果非连词，则为前后Word

	public Word() {
		new Word(8, null);
	}

	public Word(String word) {
		new Word(8, word);
	}

	public Word(int type, String word) {
		this.type = type;
		this.word = word;
		this.before = null;
		this.next = null;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public T getBefore() {
		return before;
	}

	public void setBefore(T before) {
		this.before = before;
	}

	public T getNext() {
		return next;
	}

	public void setNext(T next) {
		this.next = next;
	}

	public int getSite() {
		return site;
	}

	public void setSite(int site) {
		this.site = site;
	}
}
