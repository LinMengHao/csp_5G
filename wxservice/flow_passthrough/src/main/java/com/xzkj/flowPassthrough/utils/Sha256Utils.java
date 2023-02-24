package com.xzkj.flowPassthrough.utils;

import java.security.MessageDigest;

/**
 * sha256加密
 */
public class Sha256Utils {
    public static void main(String[] args) {

    }
    //字符串sha256加密
    public static String getSHA256(String s){
        MessageDigest messageDigest;
        String encodeStr="";
        try{
            messageDigest=MessageDigest.getInstance("SHA-256");
            messageDigest.update(s.getBytes("UTF-8"));
            encodeStr=byteToHex(messageDigest.digest());
        }catch (Exception e){
            e.printStackTrace();
        }
        return encodeStr;
    }
    public static String byteToHex(byte[] bytes){
        StringBuffer stringBuffer=new StringBuffer();
        String temp=null;
        for (int i = 0; i < bytes.length; i++) {
            temp=Integer.toHexString(bytes[i] & 0xFF);
            if(temp.length()==1){
                //得到一位，需补0
                stringBuffer.append(0);
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
