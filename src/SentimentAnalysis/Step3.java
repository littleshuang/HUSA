package SentimentAnalysis;

import beans.Term;
import beans.Trans;
import com.sun.javafx.beans.annotations.NonNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static common.Constants.*;
import static tools.CommonTools.copyList;
import static tools.CommonTools.copyMap;
import static tools.FileTools.getCurRootPath;
import static tools.FileTools.readDataToList;
import static tools.FileTools.writeListToFile;

/**
 * Created by Ziyun on 2016/12/20.
 * 生成高效用事务集
 */

public class Step3 {

	public static void main(String[] args) {
		String root = getCurRootPath() + ROOT;        // 根目录
		String wordFile = root + "/wordSpeech.txt";
		String transFile = root + "/selectedWords.txt";
		String euFile = root +"/similarity/Sopmi.txt";		// 外部效用值
		String huisFile = root +"/huiTrans.txt";		// 高效用挖掘
		String fimFile = root +"/fimTrans.txt";		// 频繁挖掘

		Step3 step3 = new Step3();
		List<Trans> transList = step3.readTrans(transFile);
		Map<String, Term> termMap = step3.readTerms(wordFile);
		Map<String, Double> wordMap = step3.readEu(euFile);
		termMap = step3.setEuForTerms(termMap, wordMap);
		List<Trans> huiTrans = step3.genTransForHuim(transList, termMap);
		List<String> fimTrans = step3.genTransForFim(transList, termMap);
		writeListToFile(huisFile, huiTrans);
		writeListToFile(fimFile, fimTrans);
	}

	// 从文件中读取事务
	private List<Trans> readTrans(@NonNull String transFile) {
		List<Trans> transList = new ArrayList<>();
		int tid = 1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(transFile));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					transList.add(new Trans(tid++, line.split(BLANK)));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return transList;
	}

	// 根据 word.txt 文件中的词汇列表将词汇转换为词汇--id 字典
	private Map<String, Integer> wordToId(String wordFile) {
		List<String> wordList = readDataToList(wordFile);
		Map<String, Integer> wordMap = new HashMap<>(wordList.size());
		for (int i = 1; i <= wordList.size(); i++) {
			wordMap.put(wordList.get(i), i);
		}
		return wordMap;
	}

	// 从词汇--词频--词性文件中读取数据
	private Map<String, Term> readTerms(@NonNull String file){
		Map<String, Term> termMap = new HashMap<>();
		int tid = 1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			String[] seg;
			while ((line = br.readLine()) != null){
				if (line.length() > 0 && ((seg = line.split(SEG)).length == 3)){
					Term term = new Term(tid++, seg[0]);
					term.setFre(Integer.valueOf(seg[1]));
					termMap.put(seg[0], term);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		return termMap;
	}

	// 读取外部效用值
	private Map<String, Double> readEu(@NonNull String euFile) {
		Map<String, Double> wordMap = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(euFile));
			String line;
			String[] seg;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					if ((seg = line.split(SEG)).length == 3) {
						wordMap.put(seg[0], Double.valueOf(seg[2]));
					} else if ((seg = line.split(SEG)).length == 5) {
						wordMap.put(seg[0], Double.valueOf(seg[4]));
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wordMap;
	}

	// 设置外部效用值
	private Map<String, Term> setEuForTerms(Map<String, Term> termMap, Map<String, Double> wordMap){
		Map<String, Term> resultMap = copyMap(termMap);
		for (String word : resultMap.keySet()){
			if (wordMap.keySet().contains(word)){
				Term term = resultMap.get(word);
				term.setEu(wordMap.get(term.getTerm()));		// 打分值作为外部效用
				// term.setEu(wordMap.get(term.getTerm()) * term.getFre());	// 打分 * 词频作为外部效用
				resultMap.put(word, term);
			}
		}
		return resultMap;
	}

	// 生成高效用模式挖掘的事务
	private List<Trans> genTransForHuim(List<Trans> transList, Map<String, Term> termMap) {

		List<Trans> resultList = copyList(transList);

		for (Trans trans : resultList) {
			int tu = 0;
			String[] words = trans.getWords();
			for (String word : words) {
				if (termMap.keySet().contains(word)) {
					// todo 设置词汇内部效用
					double wu = termMap.get(word).getWu();
					wu *= Math.pow(10, DN);
					trans.addWord(termMap.get(word).getId(), (int) wu);
					tu += wu;
				}
			}
			trans.setTu(tu);
		}
		return resultList;
	}

	// 生成频繁模式挖掘的事务
	private List<String> genTransForFim(List<Trans> transList, Map<String, Term> termMap) {
		List<String> resultList = new ArrayList<>(transList.size());

		for (Trans trans : transList) {
			String[] words = trans.getWords();
			StringBuilder sb = new StringBuilder();
			for (String word : words) {
				if (termMap.keySet().contains(word)) {
					sb.append(termMap.get(word).getId());
					sb.append(BLANK);
				}
			}
			resultList.add(sb.toString());
		}
		return resultList;
	}
}
