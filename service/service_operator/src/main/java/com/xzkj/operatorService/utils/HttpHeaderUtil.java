package com.xzkj.operatorService.utils;



import com.xzkj.operatorService.constants.Keys;
import com.xzkj.utils.DateUtil;
import com.xzkj.utils.Sha256Utils;
import com.xzkj.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * 请求头方法
 */
@Component
public class HttpHeaderUtil {
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    Keys keys;




    /**
     * 运营对接请求头
     * @return
     */
    public HttpHeaders getHttpHeaderByRAS(){
        HttpHeaders headers=new HttpHeaders();
        long time = new Date().getTime();
        String timestamp = String.valueOf(time);
        headers.set("Timestamp",timestamp);
        String requestId= UUIDUtil.getUUID32();
        headers.set("Request-ID",requestId);
        headers.set("App-ID",keys.getXzcspAppId());
        //鉴权字段
        String token = Sha256Utils.getSHA256(keys.getXzkjPassword());
        String signatureStr=token+timestamp+requestId;
        String sign = RSAUtils.sign(keys.getXzcspPrivateKey(), signatureStr);
        headers.set("Authorization","Basic "+sign);
        return headers;
    }
}
