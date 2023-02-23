package com.xzkj.flowStore.utils.wechat.enums;

public enum WXTradeStateEnum {
    SUCCESS,    //支付成功
    REFUND,     //转入退款
    NOTPAY,     //未支付
    CLOSED,     //已关闭
    ACCEPT,     //已接收，等待扣款
    PAY_FAIL;   //支付失败(其他原因，如银行返回失败)
}
