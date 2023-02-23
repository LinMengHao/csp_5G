package com.xzkj.flowStore.utils.wechat.utils;

import org.apache.http.client.HttpClient;

/**
 * 常量
 */
public class WXPayConstants {

    public enum SignType {
        MD5, HMACSHA256
    }

    public static final String DOMAIN_API = "api.mch.weixin.qq.com";

    public static final String FAIL = "FAIL";
    public static final String SUCCESS = "SUCCESS";
    public static final String HMACSHA256 = "HMAC-SHA256";
    public static final String MD5 = "MD5";

    public static final String FIELD_SIGN = "sign";
    public static final String FIELD_SIGN_TYPE = "sign_type";

    public static final String USER_AGENT =
            " (" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") +
                    ") Java/" + System.getProperty("java.version") + " HttpClient/" + HttpClient.class.getPackage().getImplementationVersion();

    //申请扣款
    public static final String PAPPAY_URL_SUFFIX = "/pay/pappayapply";

    //查询订单
    public static final String QUERY_ORDER_URL_SUFFIX = "/pay/paporderquery";

    public static final String QUERY_REFUND_ORDER_URL_SUFFIX = "/pay/refundquery";


    //申请退款
    public static final String SECAPI_PAY_REFUND = "/secapi/pay/refund";


    //预下单
    public static final String UNIFIED_ORDER = "/pay/unifiedorder";


}

