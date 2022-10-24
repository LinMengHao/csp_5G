package com.xzkj.accessService.utils;


import com.xzkj.accessService.entity.msgModel.TextMsgModel;
import com.xzkj.accessService.entity.xmlToModel.Messages;
import com.xzkj.accessService.entity.xmlToModel.Multimedia;
import com.xzkj.accessService.rabbitMQ.conf.DirectRabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

//异步请求管理工具
@Slf4j
@Component
public class AsyncUtils {
    //打印数据到特定日志文件
    private static final Logger logger=LogManager.getLogger("shortMessageRollingFile");
    @Autowired
    RabbitTemplate rabbitTemplate;

    //上行，撤回通知消息上行
    @Async("asyncPoolTaskExecutor")
    public void sendMessageToMQ(Messages messages) {
        rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE_WORK_ACCESS,DirectRabbitConfig.ROUTING_WORK_ACCESS, messages);
    }

    //状态通知
    @Async("asyncPoolTaskExecutor")
    public void sendNotifyToMQ(Messages messages) {
        rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE_WORK_ACCESS,DirectRabbitConfig.ROUTING_WORK_NOTIFY, messages);
    }

    //审核消息上行
    @Async("asyncPoolTaskExecutor")
    public void sendAuditToMQ(Multimedia multimedia) {
        rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE_WORK_ACCESS,DirectRabbitConfig.ROUTING_WORK_AUDIT, multimedia);
    }
    @Async("sendPoolTaskExecutor")
    public void sendMsgToMQ(TextMsgModel textMsgModel) {
        log.info("异步线程： "+Thread.currentThread().getName()+"发送到客户端时间: "+System.currentTimeMillis());
        rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE_WORK_SEND,DirectRabbitConfig.ROUTING_WORK_SEND,textMsgModel);
    }
}
