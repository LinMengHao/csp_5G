package com.xzkj.utils;

import java.util.UUID;

/**
 * @author lmh
 */
public class UUIDUtil {
    //获取一个uuid，去除- 并小写
    public static String getUUID32(){
        return UUID.randomUUID().toString().replace("-","").toLowerCase();
    }
    //获取去多个uuid
    public static String[] getUUIDs(int num){
        if(num<=0){
            return null;
        }
        String[] ids=new String[num];
        for (int i = 0; i < num; i++) {
            ids[i]=getUUID32();
        }
        return ids;
    }
}
