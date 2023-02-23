package com.xzkj.flowStore.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.xzkj.flowStore.utils.bo.WeiXinInfoBo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dashan
 * @date 2019/3/24 10:14 PM
 */
public class WeiXinUtil {

    public static Logger logger = LoggerFactory.getLogger(WeiXinUtil.class);


    public static String appId = "wxab004a27bfab4cc6";

    public static String secret = "80c8abffa72ae751b9d15c79e6dc2324";


    public static WeiXinInfoBo login(String code) {


        String codeUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";


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
                return login(code);
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


    public static WeiXinInfoBo getAccessToke() {
        WeiXinInfoBo bo = new WeiXinInfoBo();


        String loginUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret;

        String result = HttpUtil.get(loginUrl);

        if (StringUtils.isNotBlank(result)) {

            logger.info("微信登录返回：{}", result);

        }

        return bo;

    }

}

