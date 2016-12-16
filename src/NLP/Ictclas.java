package NLP;

import common.Constants;

import static tools.Output.printlnErr;

/**
 * Created by Ziyun on 2016/12/15.
 * <p>
 * 中科院分词工具
 */

public class Ictclas {

	public static void initNLPIR() {
		initNLPIR(true);
	}

	// 使用中科院分词工具分词前需要先初始化该工具
	public static void initNLPIR(boolean userDict) {
		// charset: 1 表示 UTF-8 0 表示 GBK
		int init_flag = CLibrary.Instance.NLPIR_Init("", 1, "0");
		String nativeBytes;
		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			printlnErr("初始化失败！fail reason is " + nativeBytes);
			return;
		}
		if (userDict) {
			if (ImportUserDict() == 0) {
				printlnErr("未能导入用户词典！");
			}
			SaveUserDict();
		}
	}

	// 返回中科院分词结果（可以添加用户词典）返回格式为 我 是 中国 人
	public static String ICTCLASSegment(String sentence) {

		return ICTCLASSegment(sentence, 0);
	}

	/**
	 * 返回中科院分词结果（可以添加用户词典）返回格式为 这样/rzv 的/ude1 运动/vn
	 *
	 * @param sentence
	 * @param posTag   是否含词性标注结果，0：不含 1：含
	 * @return
	 */
	public static String ICTCLASSegment(String sentence, int posTag) {
		if (CLibrary.Instance == null) {
			initNLPIR();
		}

		return CLibrary.Instance.NLPIR_ParagraphProcess(sentence, posTag);
	}

	// 导入用户词典，返回成功导入的词典数
	public static int ImportUserDict() {
		return ImportUserDict(Constants.PATH_DICT_USER);
	}

	// 导入用户词典，返回成功导入的词典数
	public static int ImportUserDict(String dictPath) {
		return CLibrary.Instance.NLPIR_ImportUserDict(dictPath);
	}

	// 保存用户词典
	public static int SaveUserDict() {
		return CLibrary.Instance.NLPIR_SaveTheUsrDic();
	}

	public static void ExitIctclas() {
		CLibrary.Instance.NLPIR_Exit();
	}

	public static void main(String[] args) {
		initNLPIR();
		String nativeBytes;
		String test_str = "据悉，质检总局已将最新有关情况再次通报美方，" +
				"要求美方加强对输华玉米的产地来源、运输及仓储等环节的管控措施，" +
				"有效避免输华玉米被未经我国农业部安全评估并批准的转基因品系污染.";

		try {
			nativeBytes = ICTCLASSegment(test_str, 1);
			System.out.println("\n分词结果为： ");
			System.out.println(nativeBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ImportUserDict();
		try {
			nativeBytes = ICTCLASSegment(test_str, 1);
			System.out.println("\n增加用户词典后分词结果为： ");
			System.out.println(nativeBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		CLibrary.Instance.NLPIR_Exit();
	}
}
