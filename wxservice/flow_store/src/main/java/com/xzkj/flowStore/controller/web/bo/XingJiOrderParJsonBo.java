package com.xzkj.flowStore.controller.web.bo;

import lombok.Data;

@Data
public class XingJiOrderParJsonBo {
    //"expire_time": 1612167731000,  //充值后的有效期，时间戳，必填
    //  "userid": "92503894117056",    //用户唯一ID，必填
    //  "orderStat":"10000"

    private String expire_time;
    private String userid;
    private String orderStat;


}
