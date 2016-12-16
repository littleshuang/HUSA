package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static tools.FileTools.createDir;
import static tools.FileTools.getCurRootPath;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class Output {
	static BufferedWriter writer;

	private static File creatLogFile() {
		String logRoot = getCurRootPath() + "/log/";
		String logPath = logRoot + System.currentTimeMillis() + ".txt";
		createDir(logRoot);
		return new File(logPath);
	}

	public static void log(Object o) {
		log(o, true);
	}

	/**
	 * 写入日志文件
	 *
	 * @param o
	 * @param console: 是否同时写入控制台，默认写入
	 */
	public static void log(Object o, boolean console) {
		if (writer == null) {
			try {
				writer = new BufferedWriter(new FileWriter(creatLogFile()));
			} catch (IOException e) {
				printlnErr("创建日志文件失败！");
				e.printStackTrace();
			}
		}
		try {
			writer.write(String.valueOf(o));
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (console) {
			println(o);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (writer != null) {
			writer.flush();
			writer.close();
		}
	}

	public static void println() {
		System.out.println();
	}

	public static void println(Object o) {
		System.out.println(o);
	}

	public static void print(Object o) {
		System.out.print(o);
	}

	public static void printlnErr(Object o) {
		System.err.println(o);
	}

	public static void printErr(Object o) {
		System.err.print(o);
	}
}
