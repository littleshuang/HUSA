package NLP;

import edu.hit.ir.ltp4j.Parser;

import java.util.ArrayList;
import java.util.List;

import static common.Constants.BLANK;
import static common.Constants.POSSEG;
import static tools.Output.println;
import static tools.Output.printlnErr;

/**
 * Created by Ziyun on 2016/12/15.
 *
 * 哈工大自然语言处理工具
 *
 */

public class Ltp {

	static final String MODEL_ROOT = "./libs/ltp_model/";       // LTP 模型根目录

	// 初始化 TLP 工具， 使用 LTP 进行 NLP 任务时，记得进行初始化
	public static void initLtp(){
		if (Parser.create(MODEL_ROOT + "parser.model") < 0) {
			printlnErr("LTP 创建失败！");
		}
	}

	// 释放 LTP 工具
	public static void releaseLtp(){
		Parser.release();
	}

	/**
	 * 依存句法分析
	 * @param words 待分析的词序列
	 * @param tags 待分析的词的词性序列
	 * @return 依存句法分析结果，格式如：威朗_0 重心_3 ATT
	 */
	public static String parseByLtp(List<String> words, List<String> tags){

		List<Integer> heads = new ArrayList<>();     // 结果依存弧，heads[i]代表第i个词的父亲节点的编号
		List<String> deprels = new ArrayList<>();     // 结果依存弧关系类型

		int size = Parser.parse(words, tags, heads, deprels);

		// String result = "";      // 依存句法分析结果
		StringBuilder sb = new StringBuilder();
		int pid;    // 父节点 id
		for (int i = 0; i < size; i++) {
			pid = heads.get(i) - 1;
			sb.append(words.get(i));
			sb.append("_");
			sb.append(i);
			sb.append(" ");
			if (pid != -1){
				sb.append(words.get(pid));
				sb.append("_");
			}
			sb.append(pid);
			sb.append(" ");
			sb.append(deprels.get(i));
			sb.append("\n");
			// result += sb.toString();
		}

		// System.out.print(result);
		return sb.toString();
	}

	/**
	 * 使用 LTP 进行依存句法分析，结果直接保存在参数提供的列表中
	 * @param words 待分析的词序列
	 * @param tags 待分析的词的词性序列
	 * @param heads 结果依存弧，heads[i]代表第i个词的父亲节点的编号
	 * @param deprels 结果依存弧关系类型
	 * @return 结果中词的个数
	 */
	public static int parseByLtp(List<String> words, List<String> tags, List<Integer> heads, List<String> deprels){

		return Parser.parse(words, tags, heads, deprels);
	}

	public static void main(String[] args) {
		String str = "其中/rz 自主/vn 品牌/n 仍然/d 增速/n 相对/d 更/d 高/a";
		List<String> words = new ArrayList<>();
		List<String> tags = new ArrayList<>();
		for (String seg : str.split(BLANK)){
			String[] part = seg.split(POSSEG);
			words.add(part[0]);
			tags.add(part[1]);
		}


		initLtp();
		println(parseByLtp(words, tags));
		releaseLtp();
	}
}
