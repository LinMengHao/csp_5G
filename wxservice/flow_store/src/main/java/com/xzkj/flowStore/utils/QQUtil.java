package com.xzkj.flowStore.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xzkj.flowStore.utils.bo.QQInfoBo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.Map;

public class QQUtil {


    private static Logger logger = LoggerFactory.getLogger(QQUtil.class);


    private static final String APP_ID = "101817320";

    private static final String APP_SECRET = "de8dad423eecc95d85fff053d4a1cdf1";


    public static String getAccessToken(String code, String redirectUri) {

        String accessToken = "";

        String result = HttpUtil.get("https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=" + APP_ID + "&client_secret=" + APP_SECRET + "&code=" + code + "&redirect_uri=" + URLEncoder.encode(redirectUri));

        logger.info("QQ授权登录获取accessToken {} ", result);

        if (StringUtils.isNotBlank(result)) {
            String[] strs = result.split("&");
            for (String str : strs) {
                if (str.contains("access_token")) {
                    accessToken = str.replace("access_token=", "");
                }
            }
        }
        return accessToken;
    }


    public static String getQQOpenId(String accessToken) {
        String result = HttpUtil.get("https://graph.qq.com/oauth2.0/me?access_token=" + accessToken);
        // callback( {"client_id":"101817320","openid":"8E11DC0BC0BA0A1210D337EF8F1BBDC4"} );
        if (result.contains("openid")) {
            String json = result.replace("callback(", "").replace(");", "");
            JSONObject jsonObject = JSON.parseObject(json);
            if (jsonObject != null) {
                return jsonObject.getString("openid");
            }
        }
        return null;

    }


    public static QQInfoBo getQQInfo(String accessToken, String openId) {


        Map<String, String> resultMap = null;

        //获取用户的信息
        String userinfoResult = HttpUtil.get("https://graph.qq.com/user/get_user_info?access_token=" + accessToken + "&openid=" + openId + "&oauth_consumer_key=" + APP_ID);
        logger.info("QQ授权登录获取获取用户的信息 {} ", userinfoResult);

        if (StringUtils.isNotBlank(userinfoResult)) {
            JSONObject userJson = JSON.parseObject(userinfoResult);
            if (userJson != null && userJson.getInteger("ret") == 0) {

                String nickName = String.valueOf(userJson.get("nickname"));
                //1为男性，2为女性
                String gender = userJson.getString("gender");
                String openid = userJson.getString("openid");
                String headimgurl = userJson.getString("figureurl_qq_1");


                QQInfoBo bo = new QQInfoBo();
                bo.setNick(nickName);
                bo.setOpenId(openId);
                bo.setGender("男".equals(gender) ? 1 : 2);
                bo.setHeadPic(headimgurl);
                return bo;
            }
        }

        return null;
    }
}
