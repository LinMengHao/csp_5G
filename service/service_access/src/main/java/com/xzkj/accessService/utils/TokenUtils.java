package com.xzkj.accessService.utils;

import com.xzkj.utils.Base64Utils;
import com.xzkj.utils.Sha256Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TokenUtils {
    //获取GMT时间
    public static String getGMTDate() {
        String format=null;
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date=new Date();
            format = simpleDateFormat.format(date);
            System.out.println(format);
        }catch (Exception e){
            e.printStackTrace();
        }
        return format;
    }
    //获取鉴权字符串 Authorization
    public static String getAuthorization(String cspid,String cspToken){
        String date = getGMTDate();
        String sha256 = Sha256Utils.getSHA256(cspToken + date);
        String authorization = Base64Utils.encode(cspid + ":" + sha256);
        return "Basic "+authorization;
    }
    public static String getAuthorization(String cspid,String cspToken,String date){
        String sha256 = Sha256Utils.getSHA256(cspToken + date);
        String authorization = Base64Utils.encode(cspid + ":" + sha256);
        return "Basic "+authorization;
    }


}
