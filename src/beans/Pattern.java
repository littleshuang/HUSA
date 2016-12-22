package beans;

/**
 * Created by Ziyun on 2016/12/21.
 */

public class Pattern {
	private String terms;		// BLANK 隔开的词汇集
	private float[] eus;
	private float util;
	private int type;		// 1, 0, -1 分别表示正类，中性和负类

	public Pattern() {
	}

	public Pattern(String terms) {
		this.terms = terms;
	}

	public Pattern(String terms, float[] eus) {
		this.terms = terms;
		this.eus = eus;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public float[] getEus() {
		return eus;
	}

	public void setEus(float[] eus) {
		this.eus = eus;
	}

	public double getUtil() {
		return util;
	}

	public void setUtil(float util) {
		this.util = util;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
