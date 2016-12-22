package SentimentAnalysis;

import com.sun.javafx.beans.annotations.NonNull;
import tools.FileTools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static common.Constants.BLANK;
import static common.Constants.ROOT;
import static common.Constants.SEG;
import static tools.CommonTools.sortMapByValue;
import static tools.FileTools.writeMapToFile;

/**
 * Created by Ziyun on 2016/12/21.
 *
 * id 表示的模式转化为词汇形式
 */

public class Step5 {
	public static void main(String[] args) {
		String root = FileTools.getCurRootPath() + ROOT;        // 根目录
		String wordsFile = root + "/words.txt";
		String huisFile = root + "/huis.txt";
		String huisWordsPath = root + "/huis_words.txt";        // 词汇表示的高效用模式集

		Step5 step5 = new Step5();
		Map<String, String> idWordMap = step5.getWordIdMap(wordsFile);
		Map<String, String> huisMap = step5.convertHuisToWords(huisFile, idWordMap);
		writeMapToFile(huisWordsPath, huisMap);
	}

	private Map<String, String> getWordIdMap(@NonNull String idWordPath) {
		Map<String, String> idWordMap = new HashMap<>();

		try {
			FileReader fr = new FileReader(idWordPath);
			BufferedReader br = new BufferedReader(fr);

			String line;
			int i = 1;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					String[] idWord = line.split(SEG);
					if (idWord.length > 0){
						idWordMap.put(i++ + "", idWord[0]);
					}
				}
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return idWordMap;
	}

	private Map<String, String> convertHuisToWords(@NonNull String huisPath, Map<String, String>idWordMap){
		Map<String, String> huisMap = new HashMap<>();
		String CURSEG = "#UTIL:";
		try {
			FileReader fr = new FileReader(huisPath);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					String[] hui = line.split(CURSEG);
					StringBuilder sb = new StringBuilder();
					for (String wid : hui[0].trim().split(BLANK)){
						if (!idWordMap.keySet().contains(wid)){
							continue;
						}
						sb.append(idWordMap.get(wid));
						sb.append(BLANK);
					}
					huisMap.put(sb.toString().trim(), hui[1]);
				}
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sortMapByValue(huisMap);		// 排序后输出
	}
}
