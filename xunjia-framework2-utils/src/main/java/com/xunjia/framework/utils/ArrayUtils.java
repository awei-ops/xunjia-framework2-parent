package com.xunjia.framework.utils;

import java.util.Arrays;

public class ArrayUtils {

	/**
	 * 判断数组是否为空
	 * @return
	 */
	public static <T> boolean isArrayEmpty(T[] array){
		if (array == null || array.length == 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 字符串数组转整型数字数组
	 * @return
	 */
	public static int[] stringArray2IntegerArray(String[] array) {
		int[] intArray = null;
		if (!isArrayEmpty(array)) {
			intArray = new int[array.length];
			for (int i = 0; i < array.length; i++) {
				intArray[i] = Integer.parseInt(array[i]);
			}
		}
		return intArray;
	}
	
	/**
	 * 字符串数组转双精度浮点型数字数组
	 * @return
	 */
	public static double[] stringArray2DoubleArray(String[] array) {
		double[] doubleArray = null;
		if (!isArrayEmpty(array)) {
			doubleArray = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				doubleArray[i] = Double.parseDouble(array[i]);
			}
		}
		return doubleArray;
	}
	
	public static <T> boolean isElementExists(T[] array, T element) {
		int a = Arrays.binarySearch(array, element);
		return a > 0 ? true : false;
	}
}
