package com.xzkj.flowStore.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class XingJiUtil {


    //修治测试环境：
    //key=XMkXyFI0v8iwTIwlJB2tWnqH5zMLXl94
    //channelCode=1239
    private static final String KEY = "XMkXyFI0v8iwTIwlJB2tWnqH5zMLXl94";

    //密钥:7JG0D86jr50bkwGi偏移量:307h1797w32362aT
    private static final String SECRET = "7JG0D86jr50bkwGi";
    private static final String IVS = "307h1797w32362aT";

    private static final String CHANNEL_CODE = "1239";

    public static void main(String[] args) {


        String phone = "13911911812";

//        boolean s = checkPhone(phone);
//        System.out.println(s);
//
//        getSMSCode(phone);

//        RegisterUserBo registerUserBo = registerUser(phone, "6337", "abc123456", "219.141.169.165");
//        System.out.println(registerUserBo);
//        System.out.println(registerUserBo.getResult());
//        System.out.println(registerUserBo.getMessage());
//        System.out.println(registerUserBo.getUserId());

//        {"validityPeriod":"2020-10-09 10:06:54","code":"10000","data":"","message":"注册成功","userid":"08048008006075005004"}

        //{"validityPeriod":"2020-10-09 10:06:54","code":"10000","data":"","message":"注册成功","userid":"08012020071847070039"}


        String userId = "08012020071847070039";
//        getProductInfo(userId);


        //String userId, Integer rechargeMoney, Integer rechargeDays, String publicNetIp,
        //                                    Integer terminalType, Integer payType, String pid, Long orderId, String tradeNo, String rechargeTime
//        syncOrder("08061028165019075946",3800,7,"219.141.169.165",1,1,"1284003834273058816",
//                );

        getUserInfo(userId);
    }


    public static String getToken() {

        //token有效期为10分钟
        String url = "https://test.bjbywx.com/netbarcloud/vpn/channel/getToken.do";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("key", KEY);
        parmas.put("channelCode", CHANNEL_CODE);

        String result = HttpUtil.post(url, parmas);
        log.info("星际获取token，params={},result={}", parmas, result);

        try {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if ("1000".equals(jsonObject.getStr("code"))) {
                String token = jsonObject.getStr("token");
                if (!StrUtil.isBlank(token)) {
                    return token;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static boolean getSMSCode(String phone) {
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            return false;
        } else if (StrUtil.isBlank(phone)) {
            return false;
        }

        String url = "https://test.bjbywx.com/netbarcloud/vpn/channel/getSMSCode.do";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("key", KEY);
        parmas.put("token", token);
        parmas.put("phoneNumber", phone.trim());
        parmas.put("channelCode", CHANNEL_CODE);
        String result = HttpUtil.post(url, parmas);
        log.info("星际获取验证码:phone={},result={}", phone, result);
        try {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String code = jsonObject.getStr("code");
            if ("1000".equals(code)) {
                log.info("星际获取验证码 {}", phone);
                return true;
            } else if ("20062".equals(code)) {
                log.info("星际获取验证码{}", phone);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 获取手机号是否已经注册
     *
     * @return
     */
    public static boolean checkPhone(String phone) {
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            return false;
        }
        String url = "https://test.bjbywx.com/netbarcloud/vpn/channel/checkPhoneNumber.do";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("key", KEY);
        parmas.put("token", token);
        parmas.put("phoneNumber", phone.trim());
        String result = HttpUtil.post(url, parmas);
        log.info("星际判断手机号是否注册:phone={},result={}", phone, result);
        //{"code":"1000","data":"","message":"手机号未被注册"}
        try {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String code = jsonObject.getStr("code");
            if ("1000".equals(code)) {
                log.info("星际获取该手机 未注册了 {}", phone);
                return false;
            } else if ("20062".equals(code)) {
                log.info("星际获取该手机 已注册了 {}", phone);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static class RegisterUserBo {

        private Boolean result = false;

        private String code;

        private String userId;

        private String message;

        public Boolean getResult() {
            return result;
        }

        public void setResult(Boolean result) {
            this.result = result;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * 注册账号
     *
     * @return
     */
    public static RegisterUserBo registerUser(String phone, String smsCode, String password, String clientIp) {
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            return null;
        }
        String url = "https://test.bjbywx.com/netbarcloud/vpn/channel/openRegister.do";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("key", KEY);
        parmas.put("token", token);
        parmas.put("phoneNumber", phone.trim());
        parmas.put("smsCode", smsCode.trim());
        parmas.put("password", password);
        parmas.put("checkPassword", password);
        parmas.put("channelCode", CHANNEL_CODE);
        parmas.put("clientIp", clientIp);

        String result = HttpUtil.post(url, parmas);
        log.info("星际手机号注册:phone={},result={}", phone, result);
        RegisterUserBo bo = new RegisterUserBo();
        try {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String code = jsonObject.getStr("code");
            if ("10000".equals(code)) {
                log.info("星际手机号注册成功 phone={}, result={}", phone, result);
                bo.setResult(true);
                bo.setUserId(jsonObject.getStr("userid"));
                return bo;
            } else {
                log.error("星际手机号注册失败phone={},result={}", phone, result);
                DingdingUtil.sendMsg("星际注册失败：" + result);
                bo.setMessage(jsonObject.getStr("message"));
                return bo;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //https://test.bjbywx.com/netbarcloud/vpn/syRechargeInfo.do

    public static boolean getProductInfo(String userId) {
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            return false;
        }
        String url = "https://test.bjbywx.com/netbarcloud/vpn/syRechargeInfo.do";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("key", KEY);
        parmas.put("token", token);
        parmas.put("userId", userId);
        parmas.put("channelId", CHANNEL_CODE);

        String result = HttpUtil.post(url, parmas);
        log.info("星际获取产品信息:phone={},result={}", userId, result);
        try {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String code = jsonObject.getStr("code");
            if ("1000".equals(code)) {
                log.info("星际获取产品信息 phone={}, result={}", userId, result);
                return true;
            } else {
                log.error("星际获取产品信息 {}", userId);
                DingdingUtil.sendMsg("星际获取产品信息：" + result);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 同步订单
     */
    public static boolean syncOrder(String userId, Integer rechargeMoney, Integer rechargeDays, String publicNetIp,
                                    Integer terminalType, Integer payType, String pid, Long orderId, String tradeNo, String rechargeTime) {
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            return false;
        }

        String url = "https://test.bjbywx.com/netbarcloud/vpn/topUpOrder.do";
        Map<String, String> datas = Maps.newHashMap();
        datas.put("appType", "1");
        datas.put("userId", userId);
        datas.put("rechargeMoney", rechargeMoney + ""); //充值金额：单位分
        datas.put("rechargeDays", rechargeDays + "");
        datas.put("publicNetIp", publicNetIp);
        datas.put("terminalType", terminalType + "");//终端类型（1：浏览器PC   2：浏览器安卓 3：其它）
        datas.put("payType", payType + ""); //支付方式：1：支付宝 2：微信
        datas.put("pid", pid);
        datas.put("orderid", orderId + "");
        datas.put("tradeNo", tradeNo);
        datas.put("rechargeTime", rechargeTime);

        String data = JSONUtil.toJsonStr(datas);
        String TopUpData = null;
        try {
            TopUpData = AESDESUtil.encryptAESCBC5(data, SECRET, IVS);
        } catch (Exception e) {
            log.error("同步订单，加密异常：userId={},orderI={},json={}", userId, orderId, data);
            return false;
        }
        if (StrUtil.isBlank(TopUpData)) {
            log.error("同步订单，加密异常：userId={},orderI={},json={}", userId, orderId, data);
            return false;
        }

        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("TopUpData", TopUpData);
        parmas.put("channelCode", CHANNEL_CODE);

        String result = HttpUtil.post(url, parmas);
        log.info("星际同步订单:phone={},result={}", userId, result);
        try {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String code = jsonObject.getStr("code");
            if ("1000".equals(code)) {
                log.info("星际同步订单 phone={}, result={}", userId, result);
                return true;
            } else {
                log.error("星际同步订单 {}", userId);
                DingdingUtil.sendMsg("星际同步订单：" + result);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Long getUserInfo(String userId) {
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            return 0L;
        }
        String url = "https://test.bjbywx.com/netbarcloud/vpn/syAccountInfo.do";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("key", KEY);
        parmas.put("token", token);
        parmas.put("userId", userId);

        String result = HttpUtil.post(url, parmas);
        log.info("星际获取用户信息:useerId={},result={}", userId, result);
        try {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String code = jsonObject.getStr("code");
            if ("1000".equals(code)) {
                log.info("星际获取用户信息 phone={}, result={}", userId, result);
                return jsonObject.getJSONObject("data").getLong("validityPeriod");
            } else {
                log.error("星际获取用户信息 {}", userId);
                return 0L;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}
