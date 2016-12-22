package common;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class Constants {
	public static String SUFFIX = ".txt";

	// 文件位置
	public static final String CUR_DIR = "F:/HUSL";
	public static final String ROOT = "/Mdata/tmp";
	public static final String MIN_FHM_ROOT = ROOT + "/minFhm";
	public static final String CLOSED_EFIM_ROOT = ROOT + "/closedEfim";
	public static final String PATH_DICT_USER = "./dict/stock_dict.txt";
	public static final String PATH_STOPWORDS = "./dict/stopwords.txt";

	// 全局常量
	public static final String LOGGER = "HUSL";
	public static final String SEG = "\t";                    // 分隔符，主要用于属性间分割
	public static final String POSSEG = "/";                 // 词性分隔符
	public static final String BLANK = " ";                // 空白，主要用于词语间分割
	public static final String NEWLINE = "\r\n";                // 新行标志
	public static final String UNDERLINE = "_";                // 下划线
	public static final int DN = 3;						// 保留几位小数

	public static final String P_WS = "ws";     // 分词
	public static final String P_POS = "pos";     // 词性标注
	public static final String P_NER = "ner";     // 命名实体识别
	public static final String P_DP = "dp";     // 依存句法分析
	public static final String P_SDP = "sdp";     // 语义依存分析
	public static final String FORMAT_P = "plain";    // 格式

	public static final String SPOS = "stockpos";
	public static final String SNEG = "stockneg";
	public static final String GPOS = "gpos";		// general positive
	public static final String GNEG = "gneg";
	public static final String NEGA = "negation";
	public static final String EXTRE = "extermely";
	public static final String H = "high";
	public static final String M = "medium";
	public static final String L = "low";
	public static final String LI = "little";
	public static final String COMP = "compare";
	public static final String O = "over";
	public static final String COMMON = "common";
}
