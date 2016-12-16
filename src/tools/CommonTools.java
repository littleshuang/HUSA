package tools;

import java.util.*;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class CommonTools {

	/**
	 * 列表深度复制
	 * @param originList：原始列表
	 * @param <T>：列表数据类型
	 * @return 复制后得到的列表
	 */
	public static<T> List<T> copyList(List<T> originList){
		List<T> destList = new ArrayList<>(originList.size());
		for (T t : originList){
			destList.add(t);
		}
		return destList;
	}

	/**
	 * 集合深度复制
	 * @param originSet：原集合
	 * @param <T>：集合元素类型
	 * @return 复制后的集合
	 */
	public static<T> Set<T> copySet(Set<T> originSet){
		Set<T> destSet = new HashSet<>(originSet.size());
		for (T t : originSet){
			destSet.add(t);
		}
		return destSet;
	}

	/**
	 * 字典深度复制
	 * @param originMap：原集合
	 * @param <T>：集合元素类型
	 * @param <E>：集合元素类型
	 * @return 复制后的集合
	 */
	public static<T, E> Map<T, E> copyMap(Map<T, E> originMap){
		Map<T, E> destMap = new HashMap<>(originMap.size());
		for (T t : originMap.keySet()){
			destMap.put(t, originMap.get(t));
		}
		return destMap;
	}

	// Map 按值降序排列
	public static Map<String, String> sortMapByValue(Map<String, String> oriMap){
		return sortMapByValue(oriMap, true);
	}

	/**
	 * 使用 Map按value进行排序
	 * @param oriMap
	 * @param descend 是否降序排列
	 * @return
	 */
	public static Map<String, String> sortMapByValue(Map<String, String> oriMap, final boolean descend) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		Map<String, String> sortedMap = new LinkedHashMap<>();
		List<Map.Entry<String, String>> entryList = new ArrayList<>(
				oriMap.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				if (descend){
					return (o2.getValue()).compareTo(o1.getValue());
				}else {
					return (o1.getValue()).compareTo(o2.getValue());
				}
			}
		});

		Iterator<Map.Entry<String, String>> iter = entryList.iterator();
		Map.Entry<String, String> tmpEntry;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
		return sortedMap;
	}

	/**
	 * double数保留指定小数位
	 * @param number: 待处理的数
	 * @param n: 保留位数
	 * @return
	 */
	public static double roundDouble(double number, int num){
		double factor = Math.pow(10, num);
		return (double) Math.round(number * factor) / factor;
	}

	/**
	 * float保留小数
	 *
	 * @param f
	 * @param num 保留小数位数
	 * @return
	 */
	public static float roundFloat(float number, int num) {
		float factor = (float) Math.pow(10, num);
		return  (float) Math.round(number * factor) / factor;
	}
}
