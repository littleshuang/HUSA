package beans;

import java.util.LinkedHashMap;
import java.util.Map;

import static common.Constants.BLANK;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class Trans {
	private int tid;	// 事务id
	private Map<Integer, Integer> wordMap = null;	// 词汇集 词汇--效用
	private String[] words;
	private int tu = 0;	// 事务效用值

	public Trans(){}

	public Trans(int tid, String[] words) {
		this.tid = tid;
		this.words = words;
	}

	public void addWord(int wid, int wutil){
		if (this.wordMap == null){
			this.wordMap = new LinkedHashMap<>();
		}
		this.wordMap.put(wid, wutil);
	}

	@Override
	public String toString() {
		if (this.getWordMap() == null){
			return super.toString();
		}
		StringBuilder wordSb = new StringBuilder();
		StringBuilder utilSb = new StringBuilder();
		for (int word : this.wordMap.keySet()){
			wordSb.append(word);
			wordSb.append(BLANK);
			utilSb.append(wordMap.get(word));
			utilSb.append(BLANK);
		}
		wordSb.deleteCharAt(wordSb.lastIndexOf(BLANK));
		utilSb.deleteCharAt(utilSb.lastIndexOf(BLANK));
		wordSb.append(":");
		wordSb.append(this.tu);
		wordSb.append(":");
		wordSb.append(utilSb.toString());
		return wordSb.toString();
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public Map<Integer, Integer> getWordMap() {
		return wordMap;
	}

	public void setWordMap(Map<Integer, Integer> wordMap) {
		this.wordMap = wordMap;
	}

	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

	public int getTu() {
		return tu;
	}

	public void setTu(int tu) {
		this.tu = tu;
	}
}

