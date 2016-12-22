package similarity.hownet;

import similarity.hownet.concept.BaseConceptParser;
import similarity.hownet.concept.XiaConceptParser;
import similarity.hownet.sememe.BaseSememeParser;
import similarity.hownet.sememe.XiaSememeParser;

import java.io.IOException;

/**
 * Hownet的主控制类, 通过知网的概念和义原及其关系计算汉语词语之间的相似度.
 * 相似度的计算理论参考论文《汉语词语语义相似度计算研究》
 *
 */
public class Hownet implements Similaritable {

	/** 知网的单例 */
	private static Hownet instance = null;

	private BaseConceptParser conceptParser = null;

	private Hownet() {
		try {
			BaseSememeParser sememeParser = new XiaSememeParser();
			conceptParser = new XiaConceptParser(sememeParser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 单例获取知网对象
	 * @return
	 */
	public static Hownet instance() {
		if (null == instance) {
			instance = new Hownet();
		}
		return instance;
	}

	/**
	 * 获取概念解析器
	 * @return
	 */
	public BaseConceptParser getConceptParser() {
		return conceptParser;
	}

	@Override
	public double getSimilarity(String item1, String item2) {
		return conceptParser.getSimilarity(item1, item2);
	}

}
