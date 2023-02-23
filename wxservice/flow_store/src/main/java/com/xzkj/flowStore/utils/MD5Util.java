package com.xzkj.flowStore.utils;

import java.security.MessageDigest;


public class MD5Util {
    public MD5Util() {
    }

    public static final String MD5(String s) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] e = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(e);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for ( int i = 0; i < j; ++i ) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return (new String(str)).toLowerCase();
        } catch ( Exception var10 ) {
            var10.printStackTrace();
            return null;
        }
    }

    public static final String MD5(byte[] btInput) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(btInput);
            byte[] md = e.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for ( int i = 0; i < j; ++i ) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return (new String(str)).toLowerCase();
        } catch ( Exception var9 ) {
            var9.printStackTrace();
            return null;
        }
    }

    public static final byte[] MD5ToBytes(String s) {
        try {
            String e = MD5(s);
            return e != null ? HexUtil.hexStringToBytes(e) : null;
        } catch ( Exception var2 ) {
            var2.printStackTrace();
            return null;
        }
    }

    public static final Long getLongFromMd5(String s, int start, int end) {
        if ( start >= end ) {
            throw new RuntimeException("start必须小于end");
        } else if ( s == null ) {
            return Long.valueOf(0L);
        } else {
            byte[] md5 = MD5ToBytes(s);
            long result = 0L;
            int length = end - start;

            for ( int i = 0; i < length; ++i ) {
                result += (Long.valueOf((long) md5[i + start]).longValue() & 255L) << i * 8;
            }

            return Long.valueOf(result);
        }
    }
}
