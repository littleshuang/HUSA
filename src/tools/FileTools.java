package tools;

import com.sun.javafx.beans.annotations.NonNull;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static common.Constants.SEG;
import static tools.Output.log;
import static tools.Output.println;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class FileTools {

	// 获取当前工作目录
	public static String getCurRootPath() {
		return System.getProperty("user.dir");
	}

	// 从指定目录中读取所有数据
	public static List<String> readDataFromFiles(@NonNull String dir){
		return readDataFromFiles(new File(dir));
	}

	/**
	 * 从指定目录中读取所有数据
	 *
	 * @param directory 数据文件目录
	 * @return 数据列表
	 */
	public static List<String> readDataFromFiles(File directory) {
		List<String> dataList = new ArrayList<>();

		try {
			File[] files;
			if (directory.isDirectory()) {
				files = directory.listFiles();
				for (File file : files) {
					dataList.addAll(readDataToList(file));
				}
			} else {
				log(directory.getName() + "is not exist or is not a directory");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}

	// 从目录代表的文件中读取数据
	public static List<String> readDataToList(@NonNull String fileName) {
		return readDataToList(new File(fileName));
	}

	/**
	 * 从文件中读取数据
	 *
	 * @param file 文件名
	 * @return 数据列表
	 */
	public static List<String> readDataToList(@NonNull File file) {
		List<String> dataList = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				// if (!newTrim(line).equals("")) {
				// 	dataList.add(line);
				// }
				dataList.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataList;
	}

	public static Set<String> readDataToSet(@NonNull String file){
		return readDataToSet(new File(file));
	}

	public static Set<String> readDataToSet(@NonNull File file){
		Set<String> dataSet = new HashSet<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				dataSet.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataSet;
	}

	// 将列表数据写入文件中
	public static <T> void writeListToFile(@NonNull String file, @NonNull List<T> data) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (T t : data) {
				String str = t.toString();
				if (newTrim(str).length() > 0 && !newTrim(str).equals("")) {
					writer.write(str);
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			log(e.getStackTrace());
		}
	}

	// 将集合数据写入文件中
	public static <T> void writeSetToFile(@NonNull String file, @NonNull Set<T> data) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (T t : data) {
				String str = t.toString();
				if (newTrim(str).length() > 0 && !newTrim(str).equals("")) {
					writer.write(str);
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			log(e.getStackTrace());
		}
	}


	// 将字典数据写入文件中,键值间用 Tab 分隔
	public static <T, V> void writeMapToFile(@NonNull String file, @NonNull Map<T, V> map){
		writeMapToFile(file, map, SEG);
	}

	// 将字典数据写入文件中,键值间用 指定分隔符分开
	public static <T, V> void writeMapToFile(@NonNull String file, @NonNull Map<T, V> map, String seperator) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (T t : map.keySet()) {
				if (newTrim(t.toString()).length() > 0) {
					writer.write(t.toString());
					writer.write(seperator);
					writer.write(map.get(t).toString());
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			log(e.getStackTrace());
		}
	}

	public static <T> void writeCollectionToFile(@NonNull String file, @NonNull Collection<T> collection) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (T t : collection) {
				if (newTrim(t.toString()).length() > 0) {
					writer.write(t.toString());
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			log(e.getStackTrace());
		}
	}

	// 根据路径创建目录
	public static boolean createDir(@NonNull String dirName){
		File dir = new File(dirName);
		if (dir.exists()){
			// printlnErr("创建目录" + dirName + "失败，目标目录已经存在");
			return false;
		}
		if (!dirName.endsWith(File.separator)){
			dirName += File.separator;
		}
		if (dir.mkdirs()){
			println("创建目录" + dirName + "成功！");
			return true;
		}else {
			// printlnErr("创建目录" + dirName + "失败！");
			return false;
		}
	}

	/**
	 * 创建所给文件或目录的父目录
	 *
	 * @param file 文件或目录
	 * @return 父目录
	 */
	public static File mkParentDirs(File file) {
		final File parentFile = file.getParentFile();
		if (null != parentFile && !parentFile.exists()) {
			parentFile.mkdirs();
		}
		return parentFile;
	}

	/**
	 * 创建父文件夹，如果存在直接返回此文件夹
	 *
	 * @param path 文件夹路径，使用POSIX格式，无论哪个平台
	 * @return 创建的目录
	 */
	public static File mkParentDirs(@NonNull String path) {
		return mkParentDirs(new File(path));
	}

	/**
	 * 去掉字符串中的空格、回车、换行符、制表符
	 * \n 回车(\u000a)
	 * \t 水平制表符(\u0009)
	 * \s 空格(\u0008)
	 * \r 换行(\u000d)
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	//去掉字符串前后半角和全角空格
	public static String newTrim(@NonNull String str) {
		String result = str.trim();
		while (result.startsWith("　")) {
			result = result.substring(1, result.length()).trim();
		}
		while (result.endsWith("　")) {
			result = result.substring(0, result.length() - 1).trim();
		}
		return result;
	}
}
