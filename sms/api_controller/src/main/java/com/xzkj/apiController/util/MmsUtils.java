package com.xzkj.apiController.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MmsUtils {

    private static int seq = 0;

    /**
     * @return
     * 生成唯一主键
     */
    public static synchronized String getMmsLinkID() {
        String link_id=new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        if(seq>=9999){
            seq = 1;
        }else{
            seq+=1;
        }
        link_id+=String.format("%04d", seq);
        return link_id;
    }
    public static String getSenderTableName(){
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return "mms_sender_"+dateStr;
    }
    /**
     * 解析linkid得到表名
     * @param linkID (2020031213292137100001)
     * @return
     */
    public static String parseLinkID(String linkID) {
        if(StringUtils.isEmpty(linkID)){
            return "";
        }
        String tableStr = new String("mms_sender_");
        String str = linkID.substring(0,8);

        tableStr = tableStr + str;

        return tableStr;
    }
}
