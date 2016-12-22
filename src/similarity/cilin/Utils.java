package similarity.cilin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Ziyun on 2016/11/24.
 */

public class Utils {

	/**
	 * 根据指定编码从输入流中依次遍历每一行文字
	 *
	 * @param input
	 *            输入流
	 * @param encoding
	 *            输入流所用的文字编码
	 * @param event
	 *            遍历每一行时触发的事件处理
	 * @throws IOException
	 */
	public static void traverseLines(InputStream input, String encoding, TraverseEvent<String> event)
			throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(input, encoding));
		String line = null;

		while ((line = in.readLine()) != null) {
			event.visit(line);
		}

		input.close();
		in.close();
	}
}
