package com.xzkj.flowStore.utils.wechat.enums;

public enum  WXErrorCodeEnums {
    SYSTEMERROR,                //接口返回错误 系统超时 系统异常，请使用相同参数重新调用接口
    CONTRACT_NOT_EXIST,         //签约协议不存在，用户已解约 请检查签约协议号是否正确,是否已解约
    PARAM_ERROR,                //参数错误,请根据接口返回的详细信息检查您的程序
    ORDERPAID,                  //订单已支付,订单号重复
    ORDERCLOSED,                //订单已关闭,该订单已关
    SIGN_ERROR,                 //签名错误
    APPID_MCHID_NOT_MATCH,      //appid 和 mch_id 不匹配
    ORDER_ACCEPTED,             //扣款请求已受理，请勿重复发起, 请调用查询订单接口查看订单最新状态
    CONTRACTERROR,              //协议已过期, 请检查签约协议号是否已过期
    INVALID_REQUEST,            //无效请求，比如不是使用post方法等
    FREQUENCY_LIMITED;          //频率限制,请检查接口请求频率是否超过限制

}
