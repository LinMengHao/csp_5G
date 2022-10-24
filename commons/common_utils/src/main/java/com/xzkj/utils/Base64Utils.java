package com.xzkj.utils;


import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Utils {
     final static Base64.Encoder encoder=Base64.getEncoder();
     final static Base64.Decoder decoder=Base64.getDecoder();
    //编码
    public static String encode(String s){
        String encode=null;
        try {
            byte[] bytes = s.getBytes("UTF-8");
            encode = encoder.encodeToString(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return encode;
    }
    //解码
    public static String decode(String s){
        String decode=null;
        try {
            byte[] bytes = s.getBytes("UTF-8");
            decode = new String(decoder.decode(bytes),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return decode;
    }
}
