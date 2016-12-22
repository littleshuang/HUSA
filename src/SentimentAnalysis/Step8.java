package SentimentAnalysis;

import com.sun.javafx.beans.annotations.NonNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static common.Constants.BLANK;
import static common.Constants.SEG;
import static tools.FileTools.readDataToList;

/**
 * Created by Ziyun on 2016/12/21.
 * <p>
 * 情感分析：3类 & 5类
 * <p>
 * 句子情感计算：如果句中包含情感词汇，则直接使用句中的情感词汇表示情感
 * 如果句中不含情感词汇，则根据上下句间转折关系来推断情感
 */

public class Step8 {
	private static final boolean FIVE_CLASS = false;    // 如果进行5级情感划分，则使用程度副词

	private Map<String, String> getWordId(@NonNull String wordFile) {
		Map<String, String> wordIdMap = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(wordFile));
			String line;
			String[] seg;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					if ((seg = line.split(SEG)).length > 0) {
						wordIdMap.put(seg[0], seg[1]);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wordIdMap;
	}

	// 模式数组中第i个位置存放以i开始的模式集，模式间用SEG分割，模式内部用BLANK分割
	private String[] patternToId(@NonNull String patFile, Map<String, String> wordIdMap){
		List<String> patList = readDataToList(patFile);
		String[] patIds = new String[wordIdMap.size()];

		for (String pat : patList){
			String[] terms = pat.split(BLANK);
			int[] ids = new int[terms.length];
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < terms.length; i++){
				int id = Integer.parseInt(wordIdMap.get(terms[i]));
				ids[i] = id;
				sb.append(id);
				if (i < terms.length - 1){
					sb.append(BLANK);
				}
			}

			Arrays.sort(ids);
			int position = ids[0];
			if (patIds[position] != null){
				sb.append(SEG);
				sb.append(patIds[position]);
			}
			patIds[position] = sb.toString();
		}

		return patIds;
	}

}
