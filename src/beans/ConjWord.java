package beans;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class ConjWord {
	public static final int PURPOSE = 1;
	public static final int CAUSE = 2;
	public static final int BUT = 3;
	public static final int ADDITION = 4;
	public static final int SEQUENCE = 5;	// 0 表示不含连词
	private int type;       // 连词类型：
	private String word;    // 词汇
	private Segment before, next;		// 连词分割的上下子句指针

	public ConjWord(int type, String word) {
		this.type = type;
		this.word = word;
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

	public Segment getBefore() {
		return before;
	}

	public void setBefore(Segment before) {
		this.before = before;
	}

	public Segment getNext() {
		return next;
	}

	public void setNext(Segment next) {
		this.next = next;
	}
}
