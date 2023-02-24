package com.xzkj.flowPassthrough.utils;



import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;

public class Base64Util {
	/**
	 * * BASE64解码
	 * @param base64
	 * @return
	 */
	public static byte[] decodeBASE64(String base64) {
		return Base64.decodeBase64(base64.getBytes());
	}

	/**
	 * * BASE64解码
	 * @param base64
	 * @return
	 */
	public static String decodeBASE64AsString(String base64) {
		byte[] bytes = decodeBASE64(base64);
		return new String(bytes);
	}

	/**
	 * * BASE64编码
	 * @param bytes
	 * @return
	 */
	public static String encodeBASE64(byte[] bytes) {
		byte[] base64Bytes = Base64.encodeBase64(bytes);
		return new String(base64Bytes);
	}

	/**
	 * * BASE64编码
	 * @param str
	 * @return
	 */
	public static String encodeBASE64(String str) {
		return encodeBASE64(str.getBytes());
	}
	public static String encodeBASE64Md5(String origin, String charsetname) {
		byte[] bytes = MD5Utils.MD5EncodeByte(origin,charsetname);
		byte[] base64Bytes = Base64.encodeBase64(bytes);
		return new String(base64Bytes);
	}
	/**
	 * 利用java原生的类实现SHA256加密获取签名
	 * @param str 参数拼接的字符串
	 * @return
	 */
	public static String getSHA256(String str) {
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodeStr = byte2Hex(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encodeStr;
	}
	/**
	 * 将byte转为16进制
	 * @param bytes
	 * @return
	 */
	private static String byte2Hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		String temp = null;
		for (byte aByte : bytes) {
			temp = Integer.toHexString(aByte & 0xFF);
			if (temp.length() == 1) {
				// 1得到一位的进行补0操作
				sb.append("0");
			}
			sb.append(temp);
		}
		return sb.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(Base64Util.encodeBASE64("张"));
		System.out.println(Base64Util.decodeBASE64AsString(Base64Util
				.encodeBASE64("张")));
		String t1 = "0123456";
		System.out.println(t1.substring(1, 3));
		System.out.println(System.currentTimeMillis());
	}

}
