package SentimentAnalysis;

import beans.Pattern;
import com.sun.javafx.beans.annotations.NonNull;

import java.io.*;
import java.util.*;

import static common.Constants.BLANK;
import static common.Constants.ROOT;
import static common.Constants.SEG;
import static tools.FileTools.getCurRootPath;
import static tools.Output.println;

/**
 * Created by Ziyun on 2016/12/21.
 * <p>
 * 模式聚类：聚为正负两类
 */

public class Step6 {

	public static void main(String[] args) {
		String root = getCurRootPath() + ROOT;        // 根目录
		String patternFile = root + "/huis_words_minFhm.txt";	// 模式文件
		String euFile = root + "/similarity/Sopmi.txt";		// 相似性（外部效用）文件
		String patternPath = root + "/pattern/";		// 分类后的模式目录

		Step6 step6 = new Step6();
		List<Pattern> patternList = step6.readPatFromFile(patternFile);
		Map<String, String> termScoreMap = step6.getTermScore(euFile);
		step6.classify(patternList, termScoreMap);
		step6.writePatternByType(patternPath, patternList);
	}

	// 从高效用模式文件中读取模式集
	// 模式文件：一行代表一个模式，SEG分割模式 & 模式效用值，BLANK 分割模式中的词汇
	private List<Pattern> readPatFromFile(@NonNull String file) {
		List<Pattern> patternList = new ArrayList<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			String[] seg;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					if ((seg = line.split(SEG)).length == 2) {
						patternList.add(new Pattern(seg[0]));
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patternList;
	}

	// 读取外部效用值，Sopmi 文件中一行为一个词汇，SEG分割词汇--Sopmi--Sopmi绝对值
	private Map<String, String> getTermScore(@NonNull String file) {
		Map<String, String> termScoreMap = new HashMap<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			String[] seg;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					if ((seg = line.split(SEG)).length > 0) {
						termScoreMap.put(seg[0], seg[1]);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return termScoreMap;
	}

	// 根据模式词汇效用值之和的正负决定模式的正负
	private void classify(List<Pattern> patternList, Map<String, String> termScoreMap){

		Collection<String> keyset = termScoreMap.keySet();

		for (Pattern pattern : patternList){
			String[] terms = pattern.getTerms().split(BLANK);
			float[] eus = new float[terms.length];
			float total = 0f;
			for (int i = 0; i < terms.length; i++){
				if (keyset.contains(terms[i])){
					float util = Float.parseFloat(termScoreMap.get(terms[i]));
					eus[i] = util;
					total += util;
				}
			}
			pattern.setEus(eus);
			pattern.setUtil(total);
			println(pattern.getTerms() + SEG + total);
			if (total > 0){
				pattern.setType(1);
			}else if(total < 0){
				pattern.setType(-1);
			}else {
				pattern.setType(0);
			}
		}
	}

	// 将分类后的模式写入指定文件中
	private void writePatternByType(@NonNull String patternPath, List<Pattern> patternList){
		File path = new File(patternPath);
		if (!path.isDirectory()){
			path.mkdir();
		}

		File posPattern = new File(patternPath + "/posPattern.txt");
		File neuPattern = new File(patternPath + "/neuPattern.txt");
		File negPattern = new File(patternPath + "/negPattern.txt");

		try {
			BufferedWriter posBw = new BufferedWriter(new FileWriter(posPattern));
			BufferedWriter neuBw = new BufferedWriter(new FileWriter(neuPattern));
			BufferedWriter negBw = new BufferedWriter(new FileWriter(negPattern));

			for (Pattern pattern : patternList){
				if (pattern.getType() == 1){
					posBw.write(pattern.getTerms());
					posBw.newLine();
				}else if (pattern.getType() == -1){
					negBw.write(pattern.getTerms());
					negBw.newLine();
				}else {
					neuBw.write(pattern.getTerms());
					neuBw.newLine();
				}
			}

			posBw.flush();
			neuBw.flush();
			negBw.flush();
			posBw.close();
			neuBw.close();
			negBw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
