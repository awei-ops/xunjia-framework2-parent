package com.xunjia.framework.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 姜浩
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 将字符串的第一个字母转成大写字母
     * 2020年9月27日
     *
     * @param str
     * @return
     * @author 姜浩
     */
    public static String upperFirst(String str) {
        String firstChar = String.valueOf(str.charAt(0)).toUpperCase();
        return str.length() == 1 ? firstChar : firstChar + str.substring(1);
    }

    /**
     * 将字符串的第一个字母转换成小写字母
     * 2020年9月27日
     *
     * @param str
     * @return
     * @author 姜浩
     */
    public static String lowerFirst(String str) {
        String firstChar = String.valueOf(str.charAt(0)).toLowerCase();
        return str.length() == 1 ? firstChar : firstChar + str.substring(1);
    }

    /**
     * 确定字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串的每个字符是否相等
     * @param str
     * @return
     */
    public static boolean isCharEqual(String str) {
        return str.replace(str.charAt(0), ' ').trim().length() == 0;
    }

    /**
     * 驼峰转下划线
     * @param str   目标字符串
     * @return: java.lang.String
     */
    public static String humpToUnderline(String str) {
        String regex = "([A-Z])";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            String target = matcher.group();
            str = str.replaceAll(target, "_"+target.toLowerCase());
        }
        return str;
    }

    /**
     * 下划线转驼峰
     * @param str   目标字符串
     * @return: java.lang.String
     */
    public static String underlineToHump(String str) {
        String regex = "_(.)";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            String target = matcher.group(1);
            str = str.replaceAll("_"+target, target.toUpperCase());
        }
        return str;
    }
}
