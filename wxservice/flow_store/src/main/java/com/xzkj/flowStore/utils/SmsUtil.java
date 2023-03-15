package com.xzkj.flowStore.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class SmsUtil {

    //账号：100061
    //密码：aMRHUuhj
    //产品：C00057
    //ip地址：39.96.38.172

    public enum SmsType {
        Login, Register
    }


    public static Boolean send(SmsType smsType, String phone, String code) {
        String content = "";

        switch (smsType) {
            case Login:
                content = String.format("【修治】您好，您的验证码是%s，请勿告诉其他人！", code);
                break;
            case Register:
                content = String.format("【修治】您 好，您的验证码是%s，请勿告诉其他人！", code);
                break;
        }

        if (StrUtil.isBlank(content)) {
            return false;
        }
        String account = "100061";
        String password = "aMRHUuhj";
        String timestamp = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
        String token = SecureUtil.md5(account + timestamp + password);
        String authorization = Base64.encode(account + ":" + timestamp);
        String url = "http://39.96.38.172/smsapi/SmsMt.php";
        Map<String, Object> params = Maps.newHashMap();
        params.put("token", token);
        params.put("mobile", phone);
        params.put("product", "C00057");
        params.put("msg", content);
        String result = HttpRequest.post(url).header(Header.AUTHORIZATION, authorization).form(params).execute().body();
        log.info("发送短信：param={},result={}", params, result);
        JSONObject json = JSONUtil.parseObj(result);
        if (json == null) {
            log.error("发送短信  发送失败 type={},phone={},code={}", smsType, phone, code);
            return false;
        }
        if (json.getInt("RetCode") == 0) {
            log.info("发送短信  发送成功 type={},phone={},code={}", smsType, phone, code);
            return true;
        } else {
            log.error("发送短信  发送失败 type={},phone={},code={}", smsType, phone, code);
            return false;
        }
    }


}
