package com.xzkj.flowStore.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

@Slf4j
public class DingdingUtil {

    public static void main(String[] args) {
        sendMsg("测试");
    }

    public static boolean sendMsg(String content) {
        if (StrUtil.isBlank(content)) {
            return false;
        }
        try {
            Long timestamp = System.currentTimeMillis();
            String secret = "SEC8ac496805770b8ee2510ebbc6807450f84022ee57463f9c455e8ec65e5efc3ea";
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");

            String url = "https://oapi.dingtalk.com/robot/send?access_token=e06934a4015acd7f4304a6b6b07918d0dfd162d3191b4e7a7ee36bba3d336646&timestamp=" + timestamp + "&sign=" + sign;

            String aa = "{\"msgtype\": \"text\",\"text\": {\"content\": \"" + content + "\"}}";
            JSONObject data = new JSONObject();
            data.set("msgtype", "text");

            JSONObject contentJson = new JSONObject();
            contentJson.set("content", content);
            data.set("text", contentJson);

            String result = HttpUtil.post(url, JSONUtil.toJsonStr(data));
            log.info("发送钉钉信息 content = {},result={}", content, result);
            if (StrUtil.isNotBlank(result)) {
                JSONObject jsonObject = JSONUtil.parseObj(result);
                if (jsonObject.getInt("errcode") == 0) {
                    return true;
                } else {
                    log.error("发送钉钉失败:{}", result);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
