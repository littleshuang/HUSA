package beans;

/**
 * Created by Ziyun on 2016/12/15.
 *
 */

public class Term {
	private String term = null;
	private int id;
	private double eu = 0.0;	// outer utility
	private double iu = 0.0;	// inner utility
	private double wu = 0.0;	// whole utility
	private int fre = 0;		// 词频

	public Term() {}

	public Term(String term) {
		this.term = term;
	}

	public Term(int id, String term) {
		this.term = term;
		this.id = id;
	}

	public void addWu(double util){
		this.wu += util;
	}

	public void calWu(){
		if (this.iu == 0.0){
			this.iu = 1.0;
		}
		if (this.eu == 0.0){
			this.eu = 1.0;
		}
		this.wu = this.eu * this.iu;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getEu() {
		return eu;
	}

	public void setEu(double eu) {
		this.eu = eu;
	}

	public double getIu() {
		return iu;
	}

	public void setIu(double iu) {
		this.iu = iu;
	}

	public double getWu() {
		if (this.wu == 0.0){
			calWu();
		}
		return wu;
	}

	public void setWu(double wu) {
		this.wu = wu;
	}

	public int getFre() {
		return fre;
	}

	public void setFre(int fre) {
		this.fre = fre;
	}
}
