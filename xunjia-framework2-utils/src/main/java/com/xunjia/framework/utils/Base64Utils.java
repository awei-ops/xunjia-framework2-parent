package com.xunjia.framework.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * BASE64编码工具
 * 2020年5月9日
 * @author 姜浩
 */
public class Base64Utils {

	/**
	 * 本地文件转换成base64字符串
	 * @param fileContent 	文件内容的二进制数组
	 * @return
	 */
	public static String fileToBase64ByLocalByte(byte[] fileContent) {
		// 对字节数组Base64编码
		Base64.Encoder encoder = Base64.getEncoder();

		return encoder.encodeToString(fileContent);// 返回Base64编码过的字节数组字符串
	}

	/**
	 * 在线文件转换成base64字符串
	 * @param fileURL 文件线上路径
	 * @return
	 */
	public static String fileToBase64ByOnline(String fileURL) {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			// 创建URL
			URL url = new URL(fileURL);
			byte[] by = new byte[1024];
			// 创建链接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			InputStream is = conn.getInputStream();
			// 将内容读取内存中
			int len = -1;
			while ((len = is.read(by)) != -1) {
				data.write(by, 0, len);
			}
			// 关闭流
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(data.toByteArray());
	}

	/**
	 * base64字符串转换成文件
	 * 对字节数组字符串进行Base64解码并生成图片
	 * @param fileStr      		base64字符串
	 * @param saveFilePath 	图片存放路径
	 * @return
	 */
	public static boolean base64ToImage(String fileStr, String saveFilePath) {

		if (StringUtils.isEmpty(fileStr)) // 图像数据为空
			return false;

		Base64.Decoder decoder = Base64.getDecoder();
		try {
			// Base64解码
			byte[] b = decoder.decode(fileStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}

			OutputStream out = new FileOutputStream(saveFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String fileToBase64(String filePath){
		File file = new File(filePath);
		FileInputStream fis = null;
		byte[] buff = null;
		try {
			fis = new FileInputStream(file);
			buff = new byte[fis.available()];
			fis.read(buff);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) { }
		}

		return Base64.getEncoder().encodeToString(buff);
	}
}
