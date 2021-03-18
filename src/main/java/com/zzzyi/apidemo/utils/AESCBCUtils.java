package com.zzzyi.apidemo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Author: Brian
 * @Description AES加密工具
 * @DATE: 2021/3/4 16:15
 * @Version 1.0
 */
@Slf4j
public class AESCBCUtils {

	public static final String KEY_ALGORITHM = "AES";
	
	//AES 加密模式
	public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	
	public static final String ENCODING = "UTF-8";
	
	// 向量
	public static final String IV_SEED = "1234567891234567";

	/**
	 * 加密
	 * 
	 * @param str 密文
	 * @param key 密key
	 * @return
	 */
	public static String encrypt(String str, String key) {
		try {
			if (str == null) {
				log.error("AES加密出错:Str为空null");
				return null;
			}
			// 判断Key是否正确
			if (key == null) {
				log.error("AES加密出错:Key为空null");
				return null;
			}
			// 判断Key是否为16位
			if (key.length() != 16) {
				log.error("AES加密出错:Key长度不是16位");
				return null;
			}
			byte[] raw = key.getBytes(ENCODING);
			SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_ALGORITHM);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			IvParameterSpec iv = new IvParameterSpec(IV_SEED.getBytes(ENCODING));
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] srawt = str.getBytes(ENCODING);
			byte[] encrypted = cipher.doFinal(srawt);
			return formatString(new String(Base64.encodeBase64(encrypted), "UTF-8"));
		} catch (Exception ex) {
			log.error("AES加密出错：" + ex.toString());
			return null;
		}
	}

	/**
	 * 解密
	 * 
	 * @param str 密文
	 * @param key 密key
	 * @return
	 */
	public static String decrypt(String str, String key) {
		try {
			// 判断Str是否为空
			if (str ==null) {
				log.error("AES解密出错：Str为空null");
				return null;
			}
			// 判断Key是否正确
			if (key == null) {
				log.error("AES解密出错:Key为空null");
				return null;
			}
			// 判断Key是否为16位
			if (key.length() != 16) {
				log.error("AES解密出错：Key长度不是16位");
				return null;
			}
			byte[] raw = key.getBytes(ENCODING);
			SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_ALGORITHM);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			IvParameterSpec iv = new IvParameterSpec(IV_SEED.getBytes(ENCODING));
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] bytes = Base64.decodeBase64(str.getBytes("UTF-8"));
			bytes = cipher.doFinal(bytes);
			return new String(bytes, ENCODING);
		} catch (Exception ex) {
			log.error("AES解密出错：" + ex.toString());
			return null;
		}
	}

	private static String formatString(String sourceStr) {
		if (sourceStr == null) {
			return null;
		}
		return sourceStr.replaceAll("\\r", "").replaceAll("\\n", "");
	}

	public static void main(String[] args) {
		String aes_key = "abcdefghabcdefgh";
		String source = "{\"channelId\":\"jsadfsa\",\"channelSecret\":\"tset\"}";
		// 加密
		String encrypt_str = encrypt(source, aes_key);
//		String encrypt_str = "U2FsdGVkX1+2m9WJWxFajooEfXbXAMrrIPvuD2IcUX8=";
		System.out.println(encrypt_str);
		// 解密
		String decrypt_str = decrypt(encrypt_str, aes_key);
		System.out.println(decrypt_str);
	}
}