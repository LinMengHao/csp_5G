package com.xzkj.flowStore.utils;


public class HexUtil {
    public HexUtil() {
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if ( src != null && src.length > 0 ) {
            for ( int i = 0; i < src.length; ++i ) {
                int v = src[i] & 255;
                String hv = Integer.toHexString(v);
                if ( hv.length() < 2 ) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        if ( hexString != null && !hexString.equals("") ) {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];

            for ( int i = 0; i < length; ++i ) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else {
            return null;
        }
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
