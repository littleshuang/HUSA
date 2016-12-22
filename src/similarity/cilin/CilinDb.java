package similarity.cilin;

/**
 * Created by Ziyun on 2016/11/24.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 词林数据库
 *
 */
public class CilinDb {

	/** the logger */
	protected static Logger logger = LogManager.getLogger(CilinDb.class);
	/** 以词语为主键的索引表 */
	private final Map<String, Set<String>> wordIndex = new HashMap<>();
	/** 以编码为主键的索引表 */
	private final Map<String, Set<String>> codeIndex = new HashMap<>();

	private static CilinDb instance = null;

	public static CilinDb getInstance() {
		if (instance == null) {
			try {
				instance = new CilinDb();
			} catch (IOException e) {
				logger.error("Exception:{}", e.getMessage());
			}
		}
		return instance;
	}

	private CilinDb() throws IOException {
		// InputStream input = new GZIPInputStream(this.getClass().getClassLoader()
		// 		.getResourceAsStream("./Mdata/cilin.db.gz"));
		InputStream input = new FileInputStream("./Mdata/cilin.db");

				TraverseEvent<String> event = new TraverseEvent<String>() {
			@Override
			public boolean visit(String line) {
				String[] items = line.split(" ");
				Set<String> set = new HashSet<>();
				for (int i = 2; i < items.length; i++) {
					String code = items[i].trim();
					if (!code.equals("")) {	
						set.add(code);
						//加入codeIndex编码
						Set<String> codeWords = codeIndex.get(code);
						if (codeWords == null) {
							codeWords = new HashSet<>();
						}
						codeWords.add(items[0]);
						codeIndex.put(code, codeWords);
					}
				}
				wordIndex.put(items[0], set);
				items = null;
				return false;
			}
		};
		logger.info("loading cilin dictionary...");
		long time = System.currentTimeMillis();

		Utils.traverseLines(input, "UTF8", event);

		time = System.currentTimeMillis() - time;
		logger.info("loading cilin dictionary completely. time elapsed:{}", time);
	}

	/**
	 * 获取某个词语的词林编码，一个词语可以有多个编码，通过Set给出
	 * @param word
	 * @return
	 */
	public Set<String> getCilinCoding(String word) {
		return wordIndex.get(word);
	}

	public Set<String> getCilinWords(String code) {
		return codeIndex.get(code);
	}

	public static void main(String[] args) {
		CilinDb db = CilinDb.getInstance();
		String code = db.getCilinCoding("中国").iterator().next();
		System.out.println(CilinCoding.printCoding(code));
		System.out.println(db.getCilinWords(code));
	}

}

