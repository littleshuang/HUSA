package NLP;

import com.sun.jna.Library;
import com.sun.jna.Native;

//定义接口CLibrary，继承自com.sun.jna.Library
public interface CLibrary extends Library {

	// 定义并初始化接口的静态变量
	CLibrary Instance = (CLibrary) Native.loadLibrary("./libs/NLPIR", CLibrary.class);

	//初始化
	public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);

	//对字符串进行分词
	public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

	//对 TXT文件内容进行分词
	public double NLPIR_FileProcess(String sSourceFilename, String sResultFilename, int bPOStagged);

	//提取关键字
	public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);

	//提取txt文件中关键词
	public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);

	//加入用户词语
	public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10

	//删除用户词语
	public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10

	//将用户词典保存至硬盘
	public int NLPIR_SaveTheUsrDic();

	//从TXT文件中导入用户词典
	public int NLPIR_ImportUserDict(String sFilename);

	public String NLPIR_GetLastErrorMsg();

	public void NLPIR_Exit();
}
