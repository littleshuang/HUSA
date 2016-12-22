package SentimentAnalysis;

import minFHM.AlgoMinFHM;
import tools.FileTools;

import java.io.IOException;

import static common.Constants.ROOT;

/**
 * Created by Ziyun on 2016/12/20.
 * <p>
 * 高效用模式挖掘
 */

public class Step4 {
	public static void main(String[] args) {
		String root = FileTools.getCurRootPath() + ROOT;        // 根目录
		String huiTrans = root + "/huiTrans.txt";                // 高效用模式挖掘数据库
		String huis = root + "/huis.txt";                        // 输出文件名
		String fimFile = root + "/fimTrans.txt";        // 频繁挖掘
		String fims = root + "/fims.txt";

		int minUtil = 15000;        // 最低效用阈值
		// double minSup = 0.02;		// 最低支持度（相对）

		Step4 step4 = new Step4();
		step4.mineByMinFhm(minUtil, huiTrans, huis);
	}

	// 采用minFhm方法进行高效用模式挖掘
	private void mineByMinFhm(int minUtil, String inputPath, String outputPath) {
		// Applying the  algorithm
		AlgoMinFHM algorithm = new AlgoMinFHM();
		try {
			algorithm.runAlgorithm(inputPath, outputPath, minUtil);
			algorithm.printStats();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
