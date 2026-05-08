package com.xunjia.framework.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 字符串拼音码工具
 * 2020年5月9日
 * @author 姜浩
 */
public class StringLetterUtils {

	/**
	 * 获取一个字符串每个汉字的首拼 
	 * @param chineseLan
	 * @return
	 */
	public static String getFirstLetter(String chineseLan) { 
		String ret = "";
		// 将汉字转换为字符数组
		char[] charChineseLan = chineseLan.toCharArray();
		// 定义输出格式
		HanyuPinyinOutputFormat hpFormat = new HanyuPinyinOutputFormat();
		// 小写格式输出
		hpFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		// 不需要语调输出
		hpFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		try {
		    for (int i = 0; i < charChineseLan.length; i++) {
			if(String.valueOf(charChineseLan[i]).matches("[\u4e00-\u9fa5]+")) {
		            // 如果字符是中文,则将中文转为汉语拼音（获取全拼则去掉红色的代码即可）
		            ret += PinyinHelper.toHanyuPinyinStringArray(charChineseLan[i], hpFormat)[0].substring(0, 1);
		        } else {
			    // 如果字符不是中文,则不转换
			    ret += charChineseLan[i];
		        }
		    }
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return ret;
	} 

}