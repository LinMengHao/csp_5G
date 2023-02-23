package com.xzkj.flowStore.utils.wx;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xzkj.flowStore.common.Constant;
import com.xzkj.flowStore.entity.Order;
import com.xzkj.flowStore.utils.WeiXinUtil;
import com.xzkj.flowStore.utils.bo.MinInfoBo;
import com.xzkj.flowStore.utils.bo.WeiXinInfoBo;
import com.xzkj.flowStore.utils.wechat.utils.WXPayConstants;
import com.xzkj.flowStore.utils.wechat.utils.WXPayRequest;
import com.xzkj.flowStore.utils.wechat.utils.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Struct;
import java.util.Map;

@Slf4j
public class WXUtil {

    private static String apiUrl = "http://vpn-api.mbqingyun.com";

    // 获取access_token的接口地址(GET) 限200次/天
    public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";


    /**
     * 获取access_token (7200秒有效期)
     *
     * @return
     */
    public static AccessToken getAccessToken() {
        String requestUrl = access_token_url.replace("APPID", Constant.WX_APP_ID).replace("APPSECRET", Constant.WX_APP_SECRET);
        try {
            String result = HttpUtil.get(requestUrl);
            log.info("获取微信accessToken返回：" + result);
            if (StrUtil.isNotBlank(result)) {
                JSONObject jsonObject = JSON.parseObject(result);
                String accessToken = jsonObject.getString("access_token");
                return new AccessToken(accessToken, jsonObject.getLong("expires_in"));
            }
        } catch (Exception e) {
            log.error("获取微信accessToken异常", e);
        }
        return null;
    }


    // 获取jsapi_ticket的接口地址(GET)
    public static String jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

    /**
     * 获取jsapi_ticket（jsapi_ticket的有效期为7200秒）
     *
     * @param token
     * @return
     */
    public static Ticket getTicket(String token) {
        try {
            String requestUrl = jsapi_ticket_url.replace("ACCESS_TOKEN", token);
            String result = HttpUtil.get(requestUrl);
            log.info("微信获取ticket返回{}", result);
            if (StrUtil.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                Integer errcode = jsonObject.getInteger("errcode");
                if (errcode == 0) {
                    Ticket ticket = new Ticket();
                    ticket.setTicket(jsonObject.getString("ticket"));
                    ticket.setExpiresIn(jsonObject.getLong("expires_in"));
                    return ticket;
                } else {
                    log.error("微信获取ticket失败{}", result);
                }
            }
        } catch (Exception e) {
            log.error("微信获取ticket失败{}", e);
        }
        return null;
    }


    public static WXShareVo getShareSign(String ticket, String url) {
        String noncestr = RandomUtil.randomString(10);
        Long timestamp = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        sb.append("jsapi_ticket=").append(ticket);
        sb.append("&noncestr=").append(noncestr);
        sb.append("&timestamp=").append(timestamp);
        sb.append("&url=").append(url);

        String signature = SecureUtil.sha1(sb.toString());

        WXShareVo vo = new WXShareVo();
        vo.setAppId(Constant.WX_APP_ID);
        vo.setNonceStr(noncestr);
        vo.setSignature(signature);
        vo.setTimestamp(timestamp);
        return vo;
    }

    public static Logger logger = LoggerFactory.getLogger(WeiXinUtil.class);


    public static WeiXinInfoBo login(String appId, String appSecret, String code) {
        String codeUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code";
        WeiXinInfoBo bo = new WeiXinInfoBo();
        String result = HttpUtil.get(codeUrl);
        if (StringUtils.isNotBlank(result)) {
            logger.info("微信小程序验证code  返回：{}", result);
            JSONObject json = JSONObject.parseObject(result);
            String openId = json.getString("openid");
            String sessionKey = json.getString("session_key");
            String unionId = json.getString("unionid");
            if (json != null && StringUtils.isNotBlank(openId) && StringUtils.isNotBlank(sessionKey)) {
                bo.setSuccess(true);
                bo.setOpenId(openId);
                bo.setUnionid(unionId);
                return bo;
            }
            Integer errcode = json.getInteger("errcode");
            if (errcode == null || errcode == -1) {
                //系统繁忙，重试
                logger.info("微信小程序验证code 系统繁忙，此时请开发者稍候再试  返回：{}", result);
                return login(appId, appSecret, code);
            } else if (errcode == 40029) {
                bo.setSuccess(false);
                bo.setMsg("code 无效");
                return bo;
            } else if (errcode == 45011) {
                bo.setSuccess(false);
                bo.setMsg("频率限制，每个用户每分钟100次");
                return bo;
            }
        }
        return bo;
    }

    /**
     * 获取小程序的accessToken
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public static AccessTokenBo getMinAccessToke(String appId, String appSecret) {
        String loginUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        String result = HttpUtil.get(loginUrl);
        logger.info("微信登录返回：{}", result);
        if (StringUtils.isNotBlank(result)) {
            AccessTokenBo accessTokenBo = JSONObject.parseObject(result, AccessTokenBo.class);
            if (accessTokenBo != null) {
                return accessTokenBo;
            }
        }
        return null;
    }


    public static MinInfoBo getMinInfoByLoginCode(String appId, String appSecret, String code) {
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code", appId, appSecret, code);
        String result = HttpUtil.get(url);
        logger.info("微信小程序登录返回：{}", result);
        if (StringUtils.isNotBlank(result)) {
            MinInfoBo accessTokenBo = JSONObject.parseObject(result, MinInfoBo.class);
            if (accessTokenBo != null) {
                return accessTokenBo;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String result = "{\"session_key\":\"mDeFqRJNduHMhqipkO2phA==\",\"openid\":\"owKls5Xfn2HfOux6gqOJJ-vCyMw0\"}";
        logger.info("微信小程序登录返回：{}", result);
        if (StringUtils.isNotBlank(result)) {
            MinInfoBo accessTokenBo = JSONObject.parseObject(result, MinInfoBo.class);
            if (accessTokenBo != null) {
                System.out.println(accessTokenBo);
            }
        }
    }

    /**
     * 微信支付，生成订单
     *
     * @param orders
     * @return
     */
    public static Map<String, String> weixinH5Pay(Order orders, String ip) {
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("appid", Constant.appID);
            parameterMap.put("mch_id", Constant.mchID);
            parameterMap.put("device_info", "WEB");
            parameterMap.put("nonce_str", WXPayUtil.generateNonceStr());
            parameterMap.put("sign_type", "MD5");
            parameterMap.put("body", orders.getOrderName());
            parameterMap.put("attach", "test");
            //商户订单号
            parameterMap.put("out_trade_no", orders.getOrderNo());
            parameterMap.put("fee_type", "CNY");
            parameterMap.put("total_fee", orders.getAmount().toString());
            parameterMap.put("spbill_create_ip", org.apache.commons.lang3.StringUtils.isNotBlank(ip) ? ip : "119.23.16.115");
            parameterMap.put("notify_url", apiUrl + "/web/order/weixin/notify");
            parameterMap.put("trade_type", "MWEB");
            parameterMap.put("scene_info", "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"https://www.fangao.cc\",\"wap_name\": \"饭糕绘画学院\"}}");
            parameterMap.put("sign", WXPayUtil.generateSignature(parameterMap, Constant.appKey, WXPayConstants.SignType.MD5));

            log.info("调用微信生成订单参数：{}", JSONObject.toJSONString(parameterMap));

            WXPayRequest request = new WXPayRequest();

            String requestXml = WXPayUtil.mapToXml(parameterMap);

            String response = request.requestOnce(WXPayConstants.DOMAIN_API, WXPayConstants.UNIFIED_ORDER, requestXml, 5000, 5000);
            Map<String, String> responeMap = WXPayUtil.xmlToMap(response);

            String returnCode = responeMap.get("return_code");


            if (WXPayConstants.SUCCESS.equals(returnCode)) {
                boolean isValidSign = WXPayUtil.isSignatureValid(responeMap, Constant.appKey);
                if (!isValidSign) {
                    log.error("发起扣款失败 ERROR:{}, {}", requestXml, response);

                    return null;
                }
                String resultCode = responeMap.get("result_code");
                if (WXPayConstants.SUCCESS.equals(resultCode)) {
                    log.info("调用微信生成订单成功：{}", JSONObject.toJSONString(responeMap));
                    return responeMap;
                } else {
                    log.info("发起扣款了失败 :{}, {}", requestXml, response);
                    return null;
                }
            } else {
                log.error("发起扣款失败 ERROR:{}, {}", requestXml, response);
                return null;
            }
        } catch (Exception ex) {
            log.info("发起扣款失败：userId={},tradeNo={}", ex);
        }
        return null;
    }

    /**
     * 微信支付，生成订单
     *
     * @param orders
     * @return
     */
    public static Map<String, String> weixinMinPay(Order orders, String ip, String openId) {
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("appid", Constant.WX_MIN_APP_ID);
            parameterMap.put("mch_id", Constant.mchID);
            parameterMap.put("nonce_str", WXPayUtil.generateNonceStr());
            parameterMap.put("sign_type", "MD5");
            parameterMap.put("body", orders.getOrderName());
//            parameterMap.put("attach", env);
            parameterMap.put("openid", openId);

            //商户订单号
            parameterMap.put("out_trade_no", orders.getOrderNo());
            parameterMap.put("fee_type", "CNY");
            parameterMap.put("total_fee", orders.getAmount().toString());
            parameterMap.put("spbill_create_ip", StrUtil.isBlank(ip) ? ip : "119.23.16.115");
            parameterMap.put("notify_url", apiUrl+"/web/order/weixin/notify");
            parameterMap.put("trade_type", "JSAPI");
            parameterMap.put("sign", WXPayUtil.generateSignature(parameterMap, Constant.appKey, WXPayConstants.SignType.MD5));

            logger.info("微信小程序 调用微信生成订单参数：{}", JSONObject.toJSONString(parameterMap));

            WXPayRequest request = new WXPayRequest();

            String requestXml = WXPayUtil.mapToXml(parameterMap);

            String response = request.requestOnce(WXPayConstants.DOMAIN_API, WXPayConstants.UNIFIED_ORDER, requestXml, 5000, 5000);
            Map<String, String> responeMap = WXPayUtil.xmlToMap(response);

            String returnCode = responeMap.get("return_code");

            if (WXPayConstants.SUCCESS.equals(returnCode)) {
                boolean isValidSign = WXPayUtil.isSignatureValid(responeMap, Constant.appKey);
                if (!isValidSign) {
                    logger.error("发起扣款失败 ERROR:{}, {}", requestXml, response);

                    return null;
                }
                String resultCode = responeMap.get("result_code");
                if (WXPayConstants.SUCCESS.equals(resultCode)) {
                    logger.info("调用微信生成订单成功：{}", JSONObject.toJSONString(responeMap));
                    return responeMap;
                } else {
                    logger.info("发起扣款了失败 :{}, {}", requestXml, response);
                    return null;
                }
            } else {
                logger.error("发起扣款失败 ERROR:{}, {}", requestXml, response);
                return null;
            }
        } catch (Exception ex) {
            logger.info("发起扣款失败：userId={},tradeNo={}", ex);
        }
        return null;
    }


}
