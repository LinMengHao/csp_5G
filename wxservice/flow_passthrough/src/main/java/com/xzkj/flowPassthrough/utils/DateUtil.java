package com.xzkj.flowPassthrough.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    //获取当前GMT时间
    public static String getGMTDate() {
        String format=null;
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date=new Date();
            format = simpleDateFormat.format(date);
            System.out.println(format);
        }catch (Exception e){
            e.printStackTrace();
        }
        return format;
    }

}
