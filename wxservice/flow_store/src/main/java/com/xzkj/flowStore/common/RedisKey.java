package com.xzkj.flowStore.common;

public class RedisKey {


    //速写自习室累计参数人数
    public static final String SUXIE_TOTAL_USER = "suxie_total_users";

    //速写自习室当前还在参数人数
    public static final String SUXIE_NOW_TOTAL_USER = "suxie_now_total_users";

    //速写自习室总作业数
    public static final String SUXIE_TOTAL_HOMEWORK = "suxie_total_homework";

    //毅力榜数据
    public static final String POWER_USER_LIST = "power_user_list";


    public static final String GET_SUXIE_DAY_SUM(String date){
        return String.format("sum_date_%s",date);
    }



}
