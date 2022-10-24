package com.xzkj.accessService.conf.interceptor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.xzkj.accessService.entity.xmlToModel.Messages;
import com.xzkj.accessService.utils.AsyncUtils;
import com.xzkj.accessService.utils.RedisUtils;
import com.xzkj.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.time.Duration;

/**
 * 上行,通知请求拦截器
 */
@Slf4j
@Component
public class InboundMessageInterceptor implements HandlerInterceptor {
    @Autowired
    AsyncUtils asyncUtils;
    @Autowired
    RedisUtils redisUtils;

    //这个方法是在访问接口之前执行的
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("线程名称"+Thread.currentThread().getName());
        long l = System.currentTimeMillis();
        //获取请求url，将chabotURI从路径中提取出来
        StringBuffer requestURL = request.getRequestURL();
        String[] split = requestURL.toString().split("/");
        //判断是消息状态还是上行消息
        String chatbotURI = split[split.length - 1];
        //上行消息

        String address = request.getHeader("Address");
        String uuid32 = UUIDUtil.getUUID32();
        XmlMapper xmlMapper=new XmlMapper();
        //xml转对象，未找到对应字段，可以忽略该异常
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Messages messages=new Messages();
        BufferedReader reader = null;

        //将请求体的xml提取出来，做解析
        try {
            reader=request.getReader();
            StringBuilder str=new StringBuilder();
            String s1=null;
            while ((s1=reader.readLine())!=null){
                str.append(s1);
            }
            messages=xmlMapper.readValue(str.toString(), Messages.class);
            System.out.println(messages.toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            reader.close();
        }

        //将chatbotURI保存到对象
        messages.setChatbotURI(chatbotURI);
        messages.setId(uuid32);
        //记录重试，不超过3次,一个小时自动删除
        redisUtils.setCacheObject(uuid32,1, Duration.ofHours(1));
        if(StringUtils.hasText(address)){
            //设置主叫地址
            messages.setMainAddress(address);
            log.info("主叫地址： "+address);
        }

        //判断是用户终端上行消息给chatbot 还是 消息发送后的状态上报
        String s = split[split.length - 2];
        if("InboundMessageNotification".equals(s)){
            //上行
            log.info("上行消息");
            asyncUtils.sendMessageToMQ(messages);
        }else if("DeliveryInfoNotification".equals(s)||"MessageStatusNotification".equals(s)){
            //状态通知
            log.info("状态通知/消息撤回通知");
            asyncUtils.sendNotifyToMQ(messages);
        }
        long l1 = System.currentTimeMillis();
        log.info("接受到响应耗时：" +(l1-l)+"毫秒");
        //忽略该请求
        return false;

    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }

}
