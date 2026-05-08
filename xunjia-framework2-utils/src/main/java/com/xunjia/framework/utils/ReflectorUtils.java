package com.xunjia.framework.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具集 2020年9月11日
 * @author 姜浩
 */
public class ReflectorUtils {

	/**
	 * 判断指定类是否存在
	 * 2020年9月11日
	 * @author 姜浩
	 * @param 类的权限定名称
	 * @return 类是否存在
	 */
	public static boolean isClassPresent(String className) {
		boolean isPresent = false;
		try {
			Thread.currentThread().getContextClassLoader().loadClass(className);
			isPresent = true;
		} catch (ClassNotFoundException e) {
			isPresent = false;
		}
		return isPresent;
	}

	/**
	 * 反射执行方法
	 * 2020年9月11日
	 * @author 姜浩
	 * @param className	类名
	 * @param methodName 方法名
	 * @return 方法返回结果
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeMethod(String className, String methodName)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {

		Class<? extends Object> clazz = Class.forName(className);
		Object object = clazz.newInstance();

		Method declaredMethod = clazz.getDeclaredMethod(methodName);
		return declaredMethod.invoke(object);
	}

	/**
	 * 反射执行方法
	 * 2020年9月11日
	 * @author 姜浩
	 * @param className 类名
	 * @param methodName 方法名称
	 * @param args 方法参数
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeMethod(String className, String methodName, Object... args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {

		Class<? extends Object> clazz = Class.forName(className);
		Object object = clazz.newInstance();

		Method declaredMethod = clazz.getDeclaredMethod(methodName);
		return declaredMethod.invoke(object, args);
	}
}
