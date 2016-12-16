package beans;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class ConjWord {
	public static final int PURPOSE = 1;
	public static final int CAUSE = 2;
	public static final int BUT = 3;
	public static final int ADDITION = 4;
	public static final int SEQUENCE = 5;
	private int type;       // 连词类型：
	private String word;    // 词汇

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
}
