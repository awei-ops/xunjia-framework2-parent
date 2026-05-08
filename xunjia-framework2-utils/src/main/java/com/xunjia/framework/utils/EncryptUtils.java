package com.xunjia.framework.utils;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加解密工具
 * 2020年5月9日
 * @author 姜浩
 */
public class EncryptUtils {
	
	public static String iv = "EJ9iIPhzB4I5UDfv";

	/**
	 * AES加密算法
	 * @param data	要加密的文本
	 * @param key		加密秘钥
	 * @param iv		加密向量
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String key, String iv) throws Exception {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");// "算法/模式/补码方式"
			int blockSize = cipher.getBlockSize();
 
			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}
 
			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
 
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
 
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);
 
			return Base64.getEncoder().encodeToString(encrypted);
	}

	/**
	 * DES解密算法
	 * @param data		密文
	 * @param key			加密秘钥
	 * @param iv			加密向量
	 * @return
	 * @throws Exception
	 */
	public static String desEncrypt(String data, String key, String iv) throws Exception {
			byte[] encrypted1 = Base64.getDecoder().decode(data);
 
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
 
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
 
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
	}
}
