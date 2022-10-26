package com.xzkj.accessService.conf.interceptor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.xzkj.accessService.entity.xmlToModel.Multimedia;
import com.xzkj.accessService.utils.AsyncUtils;
import com.xzkj.accessService.utils.RedisUtils;
import com.xzkj.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.time.Duration;

/**
 *  媒体文件审核
 */
@Slf4j
@Component
public class NotificationInterceptor implements HandlerInterceptor {
    @Autowired
    AsyncUtils asyncUtils;
    @Autowired
    RedisUtils redisUtils;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("线程名称"+Thread.currentThread().getName());
        long l = System.currentTimeMillis();
        StringBuffer requestURL = request.getRequestURL();
        String[] split = requestURL.toString().split("/");
        String chatbotURI = split[split.length - 1];
        //消息发送后的状态通知
        String authstatus = request.getHeader("Authstatus");
        String tid = request.getHeader("tid");
        String uuid32 = UUIDUtil.getUUID32();

        XmlMapper xmlMapper=new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Multimedia multimedia=new Multimedia();
        BufferedReader reader = null;

        try {
            reader=request.getReader();
            StringBuilder str=new StringBuilder();
            String s1=null;
            while ((s1=reader.readLine())!=null){
                if(s1.indexOf("xmlns:e")>-1){
                    str.append(" ");
                }
                str.append(s1);
            }
            multimedia=xmlMapper.readValue(str.toString(), Multimedia.class);
            System.out.println(multimedia.toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            reader.close();
        }

        multimedia.setChatbotURI(chatbotURI);
        multimedia.setId(uuid32);
        //记录重试，不超过3次,一个小时自动删除
        redisUtils.setCacheObject(uuid32,1, Duration.ofHours(1));
        if(StringUtils.hasText(authstatus)&&StringUtils.hasText(tid)){
            //设置主叫地址
            multimedia.setAuthstatus(authstatus);
            multimedia.setTid(tid);
            log.info("通知结果： "+authstatus+"交互id: "+tid);
        }
        asyncUtils.sendAuditToMQ(multimedia);
        long l1 = System.currentTimeMillis();
        log.info("接受到响应耗时：" +(l1-l)+"毫秒");
        //忽略该请求
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
