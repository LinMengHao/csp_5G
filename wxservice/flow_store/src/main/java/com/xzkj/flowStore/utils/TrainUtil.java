package com.xzkj.flowStore.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Random;

/**
 * @author dashan
 * @date 2019/4/20 12:38 AM
 */
public class TrainUtil {

    /**
     * length用户要求产生字符串的长度
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    public static String getTeacherScore(int socre) {
        //1-C,2-B,3-B+,4-A,5-A+,6-S,7-S+
        switch (socre) {
            case 1:
                return "C";
            case 2:
                return "B";
            case 3:
                return "B+";
            case 4:
                return "A";

            case 5:
                return "A+";

            case 6:
                return "S";

            case 7:
                return "S+";
            default:
                return "";
        }
    }

    public static String maskPhone(String mobile) {
        if (StringUtils.isEmpty(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}
