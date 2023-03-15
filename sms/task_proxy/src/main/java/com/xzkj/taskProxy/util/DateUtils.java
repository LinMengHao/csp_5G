package com.xzkj.taskProxy.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String timeStampToString(long dateValue){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateValue);
        Date date = calendar.getTime();
        if(date == null){
            date = new Date();
        }
        return sdf.format(date);
    }
}
