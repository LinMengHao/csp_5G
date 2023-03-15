package com.xzkj.flowPassthrough.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xzkj.flowPassthrough.utils.AsyncUtils;
import com.xzkj.flowPassthrough.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

/**
 * 流量订购拦截器
 */
@Slf4j
@Component
public class OrderFlowQueryInterceptor implements HandlerInterceptor {
    @Value("${flow.sendip}")
    private String[] ip;
    @Autowired
    AsyncUtils asyncUtils;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("流量订购查询拦截器线程名称"+Thread.currentThread().getName());
        long l = System.currentTimeMillis();

        String requestURL = RequestUtils.getIp(request);
        BufferedReader reader=null;
//        boolean flag=false;
//        for (int i = 0; i < ip.length; i++) {
//            //判断ip
//            if(requestURL.contains(ip[i])){
//                flag=true;
//            }
//        }
//        if(flag){
            //ip校验成功，业务执行透传
            JSONObject jsonObject=null;
            try{
                reader=request.getReader();
                StringBuilder str=new StringBuilder();
                String s1=null;
                while ((s1=reader.readLine())!=null){
                    str.append(s1);
                }
                //商户提交数据
                jsonObject=JSON.parseObject(str.toString());
                log.info("流量订购状态查询内容：{}",jsonObject);
                HttpHeaders headers=new HttpHeaders();
                headers.set("Content-Type","Application/json;charset=UTF-8");
                HttpEntity entity=new HttpEntity(jsonObject,headers);
                ResponseEntity<String> response1 = restTemplate.postForEntity("http://47.99.35.157:8061/action/flow/flowQuery", entity, String.class);
                //通道响应数据
                String body = response1.getBody();
                log.info("流量订购状态查询响应内容：{}",body);
                response.getWriter().write(body);
                JSONObject bodyJson = JSONObject.parseObject(body);
                log.info("状态查询更新数据库：{}",bodyJson);
                String errorCode = bodyJson.containsKey("errorCode") ? bodyJson.getString("errorCode") : "";
                String errorMsg = bodyJson.containsKey("errorMsg") ? bodyJson.getString("errorMsg") : "";
                JSONArray listData = bodyJson.getJSONArray("listData");
                //TODO 记录
                if(StringUtils.hasText(errorCode)&&"100000".equals(errorCode)){
                    for (int i = 0; i < listData.size(); i++) {
                        JSONObject jsonObject1 = listData.getJSONObject(i);
                        jsonObject1.put("uid",jsonObject.get("uid"));
                        asyncUtils.update(jsonObject1);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                reader.close();
            }
//        }else {
//            //响应 ip限制信息
//            JSONObject json=new JSONObject();
//            json.put("respCode","400004");
//            json.put("orderNo","0");
//            json.put("respMsg","IP受限");
//            response.setContentType("Application/json;charset=UTF-8");
//            response.getWriter().write(json.toJSONString());
//        }

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
