package com.xzkj.accessService.controller.gateway;


import com.xzkj.accessService.entity.msgModel.TextMsgModel;
import com.xzkj.accessService.utils.AsyncUtils;
import com.xzkj.accessService.utils.RedisUtils;
import com.xzkj.utils.R;
import com.xzkj.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("xiuzhi/bj_mobile")
public class SendMsgGateWayApi {
    @Autowired
    AsyncUtils asyncUtils;
    @Autowired
    RedisUtils redisUtils;

    @PostMapping("MsgSend")
    public R sendMsg(@RequestBody TextMsgModel textMsgModel){
        log.info("网关线程名称"+Thread.currentThread().getName());
        long l = System.currentTimeMillis();

        Map<String, String> map = textMsgModel.getMap();
        if(map.containsKey("destinationAddress")){
            String destinationAddress = map.get("destinationAddress");
            String[] split = destinationAddress.split(",");
            if (split.length>100){
                return R.error().message("群发消息中可携带接收方地址数组，最多支持100个号码");
            }
        }

        String uuid32 = UUIDUtil.getUUID32();
        textMsgModel.setId(uuid32);
        //记录重试，不超过3次,一个小时自动删除
        redisUtils.setCacheObject(uuid32,1, Duration.ofHours(1));
        log.info("生产耗时: "+(System.currentTimeMillis()-l));
        asyncUtils.sendMsgToMQ(textMsgModel);
        long l1 = System.currentTimeMillis();
        log.info("接受到响应耗时：" +(l1-l)+"毫秒");
        return R.ok();
    }
}
