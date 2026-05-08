package com.xunjia.framework.utils;

import java.util.HashSet;
import java.util.Set;

public class SetUtils {

	/**
	 * 判断Set集合是否为空
	 * @return
	 */
	public static <T> boolean isSetEmpty(Set<T> set){
		if (set == null || set.size() == 0){
			return true;
		}
		return false;
	}
	
	/** 字符串数组转整型数字Set集合 */
	public static Set<Integer> stringArray2IntegerSet(String[] array){
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < array.length; i++) {
			set.add(Integer.parseInt(array[i]));
		}
		return set;
	}
}
