package com.xzkj.flowStore.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description .NET与Java互通AES算法加密解密
 * @Author 三米阳光
 * @Date 2020-05-08 9:33
 **/
public class AESDESUtil {

    /*
     * AES/CBC/NoPadding (128)
     * AES/CBC/PKCS5Padding (128)
     * AES/ECB/NoPadding (128)
     * AES/ECB/PKCS5Padding (128)
     * DES/CBC/NoPadding (56)
     * DES/CBC/PKCS5Padding (56)
     * DES/ECB/NoPadding (56)
     * DES/ECB/PKCS5Padding (56)
     * DESede/CBC/NoPadding (168)
     * DESede/CBC/PKCS5Padding (168)
     * DESede/ECB/NoPadding (168)
     * DESede/ECB/PKCS5Padding (168)
     * RSA/ECB/PKCS1Padding (1024, 2048)
     * RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
     * RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
     */

    /**
     * 使用DES/AES加密数据
     *
     * @param data           : 原文
     * @param key            : 密钥(DES,密钥的长度必须是8个字节,AES,密钥的长度必须是16个字节)
     * @param transformation : 获取Cipher对象的算法
     * @param algorithm      : 获取密钥的算法
     * @return : 密文
     * @throws Exception
     */
    public static String encrypt(String data, String key,String ivs, String transformation, String algorithm) throws Exception {
        // 获取加密对象
        Cipher cipher = Cipher.getInstance(transformation);
        // 创建加密规则
        // 第一个参数key的字节
        // 第二个参数表示加密算法
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), algorithm);
        // 创建iv向量，iv向量，是使用到CBC加密模式
        // 在使用iv向量进行加密的时候，iv的字节也必须是8个字节
        IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
        // ENCRYPT_MODE：加密模式
        // DECRYPT_MODE: 解密模式
        // 初始化加密模式和算法
        cipher.init(Cipher.ENCRYPT_MODE,sks,iv);
        // 加密
        byte[] bytes = cipher.doFinal(data.getBytes());

        // 输出加密后的数据
        String encode =  new String(Base64.encodeBase64(bytes)); //Base64.encodeBase64(bytes);
        return encode;
    }

    /**
     * 解密
     * @param encryptData       :密文
     * @param key               :密钥
     * @param transformation    :加密算法
     * @param algorithm         :加密类型
     * @return
     */
    public static String decrypt(String encryptData, String key,String ivs, String transformation, String algorithm) throws Exception{
        Cipher cipher = Cipher.getInstance(transformation);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(),algorithm);
        // 创建iv向量
        IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
        //Cipher.DECRYPT_MODE:表示解密
        // 解密规则
        cipher.init(Cipher.DECRYPT_MODE,secretKeySpec,iv);
        // 解密，传入密文
        //byte[] bytes = cipher.doFinal(Base64.decode(encryptData));
        byte[] bytes = cipher.doFinal(Base64.decodeBase64(encryptData));
        String decryptData = new String(bytes);
        return decryptData;
    }

    /**
     * 加密：AES/CBC/PKCS5Padding
     */
    public static String encryptAESCBC5(String data, String key,String ivs) throws Exception {
        return encrypt(data, key,ivs, "AES/CBC/PKCS5Padding", "AES");
    }

    /**
     * 解密：AES/CBC/PKCS5Padding
     */
    public static String decryptAESCBC5(String encryptData, String key,String ivs) throws Exception{
        return decrypt(encryptData, key,ivs, "AES/CBC/PKCS5Padding", "AES");
    }

    /**
     * 加密：DES/CBC/PKCS5Padding
     */
    public static String encryptDESCCB5(String data, String key,String ivs) throws Exception {
        return encrypt(data, key,ivs, "DES/CBC/PKCS5Padding", "DES");
    }

    /**
     * 解密：DES/CBC/PKCS5Padding
     */
    public static String decryptDESBCB5(String encryptData, String key,String ivs) throws Exception{
        return decrypt(encryptData, key,ivs, "DES/CBC/PKCS5Padding", "DES");
    }

}

