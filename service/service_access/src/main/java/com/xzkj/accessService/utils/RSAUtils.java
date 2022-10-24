package com.xzkj.accessService.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 *RSA鉴权
 * @author linmenghao
 *
 */
public class RSAUtils {

    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;//设置长度
    private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    //RSA加密最大明文大小
    public static final int MAX_ENCRYPT_BLOCK=117;

    //RAS解密最大密文大小256 (私钥需2028)
    public static final int MAX_DECRYPT_BLOCK=256;

    //测试写死
    private static final String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo7vu77TdjyzfT9pxeCL3X9PpYlcqArFYTJzD2J0ZOBtaD2RK5ho8hB4p+Gm7jCRLETi49nI+2LcZ7Kn4tdxyZfsCGPuBgoNg1CtbGtqbUDKL2MOtLDPl0/TqC4TDdUBWnoE3ewdKfR0A17TcDqlHxKX/AosafGRDlRYig3ay3N3SRFjILjOSwloO74VRkhRNvXoDU8NuST2QF2pC3ZdvhDa0wAU/RkpWthDBI/vJ3bIi8/wni2j8ttlTRl81drJ7IppKQaTrP6LsYIE6PjEESFqLYztNCDRDqiVJXZEXhFNJfWI88tf6CjhoNymzfRFbvRej4w8EdFvQ4BOLvDQivwIDAQAB";
    private static final String privateKey="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCju+7vtN2PLN9P2nF4Ivdf0+liVyoCsVhMnMPYnRk4G1oPZErmGjyEHin4abuMJEsROLj2cj7Ytxnsqfi13HJl+wIY+4GCg2DUK1sa2ptQMovYw60sM+XT9OoLhMN1QFaegTd7B0p9HQDXtNwOqUfEpf8Cixp8ZEOVFiKDdrLc3dJEWMguM5LCWg7vhVGSFE29egNTw25JPZAXakLdl2+ENrTABT9GSla2EMEj+8ndsiLz/CeLaPy22VNGXzV2snsimkpBpOs/ouxggTo+MQRIWotjO00INEOqJUldkReEU0l9Yjzy1/oKOGg3KbN9EVu9F6PjDwR0W9DgE4u8NCK/AgMBAAECggEAIjW5BZJ3nFuWyPtuK9i6cOY45BgsKeXd+hvi+5ZPV563YXHmCE/BByW6RTHqQqyulomQc8WJBgSzwPP+iBjoOhCI7qlV9Ov4RjxMy76TJt14xLCVtR+ezHtmj0ONBymLL5D2A5mRYbS2oVsjg1ixKxA3yNNBlG3qhPLcATXCx0ynEy0DXX5vVRxcMJX3rhcS23YYgn7OZ0XE48Km/UhGFzVVXaAlaxe42V+cz9JC0f+9ErEwZL/2q4PFfHB9I2HM4A/ba2MMRfRRMD0Cyk/psPS+EWADi5ldghbJQtlheO8QjDaNdDFf9UjYfJLIGvJ4MFqlj77C92jYSt72+VYsCQKBgQDs8uMkSZnXZLlNg+jQiKpDpn7Gf8ihKju5xUvQjp9Rz2G80vovT2t0k+vxAQvc633X7IQ/1LokcdqlWByo0FVR9vLzb5t6SCLIRWNKCy3Q2GXxP7sXtmnaaSzcORsUkeHsj1QgBbzoqO1wedSSnVAqGzdOuW9k8JkY6SXJ6k2k1QKBgQCw5hHjfHgjlX8UOXw69tqgNMO2Q5/ZQM5Ffn4Kz3WwHzibELR7WQ7IYzvJXEtRGBI/NJhI0fQMYla8b0K5vwsZxJIXgyarQoizdDxMrkdYvNe8SyJWt5vpuNd1lLw5+zN4poE/Syq7Z6v3GJmPhSnP+CjywoeMZ41ncpPCTOWDQwKBgCm4HjweZWLiTioSxqQo2+mrX/jtONL/j6KG1XF0GyMZY3kOoJRB5IaTIOC/MWqFYBPrheGDP/74uOmR2QH0JIGY/G/WP8bI+q7OYMwBqeByqYhPk056hKq7B/YCa/00LlYrakF1ISzJyXybRs1Fu36eah+HUNCx+BLFwiyJ/3+9AoGAeqAhymYgCdq56tUmjuFMED5DZPdfuYQ4+OeEjA3f8STTYnqkAdVsb/CKIz19YEulancVEXWpDWSl553gzU3nbgdbljzt80394Rg0LJurDZJqojYbKa3BwLTHdtSR3keKGJqjgbKjfqGRDYrZ+ClBIM9P42iB+gxWc11zm/8eIm0CgYBjxRjW6bu+EfWeMWjg/mbrEkrXK8rsAGwEqy6Ww62LUjZsMu9m6OXTQeU6UTYwWnbGZL7QDDWlSdWGvihtTNt66RFlehgn85we0sKug1qWkYj6vQ6tGdgI69M7fL/ROeqCegYuBB85z92CXWhf7aDy4pbf8z4b3DnQOTuXJUZNxg==";

    private static final String username="xiuzhi_bj";
    private static final String password="xzkj@12345";
    /**
     * 生成公、私钥
     * 根据需要返回String或byte[]类型
     * @return
     */
    private static Map<String, String> createRSAKeys(){
        Map<String, String> keyPairMap = new HashMap<String, String>();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            System.out.println("公钥长度>>>"+publicKeyString.length());
            System.out.println("私钥长度>>>"+privateKeyString.length());
            System.out.println("总长度： "+(publicKeyString.length()+privateKeyString.length()));
            /*Map<String, byte[]> byteMap = new HashMap<String, byte[]>();
            byteMap.put(PUBLIC_KEY_NAME, publicKey.getEncoded());
            byteMap.put(PRIVATE_KEY_NAME, privateKey.getEncoded());*/

            //获取公、私钥值
            String publicKeyValue = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyValue = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            //存入
            keyPairMap.put(PUBLIC_KEY, publicKeyValue);
            keyPairMap.put(PRIVATE_KEY, privateKeyValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return keyPairMap;
    }


    /**
     * 解码PublicKey
     * @param key
     * @return
     */
    public static PublicKey getPublicKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解码PrivateKey
     * @param key
     * @return
     */
    public static PrivateKey getPrivateKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 签名
     * @param key	私钥
     * @param requestData	请求参数
     * @return
     */
    public static String sign(String key, String requestData){
        String signature = null;
        byte[] signed = null;
        try {
            PrivateKey privateKey = getPrivateKey(key);

            Signature Sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            Sign.initSign(privateKey);
            Sign.update(requestData.getBytes());
            signed = Sign.sign();

            signature = Base64.getEncoder().encodeToString(signed);
            System.out.println("===签名结果："+signature);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return signature;
    }

    /**
     * 验签
     * @param key	公钥
     * @param requestData	请求参数
     * @param signature	签名
     * @return
     */
    public static boolean verifySign(String key, String requestData, String signature){
        boolean verifySignSuccess = false;
        try {
            PublicKey publicKey = getPublicKey(key);

            Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifySign.initVerify(publicKey);
            verifySign.update(requestData.getBytes());

            verifySignSuccess = verifySign.verify(Base64.getDecoder().decode(signature));
            System.out.println("===验签结果："+verifySignSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return verifySignSuccess;
    }






    //解密
    public static String dencrypt(String str,String privateKeyStr) throws Exception{
        //64位解码加密后的字符串
        byte[] encryptedData = Base64.getDecoder().decode(str);
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            byte[] cache;
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        String outStr = new String(decryptedData,"UTF-8");
        return outStr;
    }

    //加密
    public static String encrypt(String data, String publicKey) throws Exception{
        PublicKey pubKey = getPublicKey(publicKey);
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] dataByte = data.getBytes("UTF-8");
        int inputLen = dataByte.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            byte[] cache;
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(dataByte, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataByte, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        String outStr = Base64.getEncoder().encodeToString(encryptedData);
        return outStr;
    }

}
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
