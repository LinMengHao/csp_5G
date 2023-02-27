package com.xzkj.flowPassthrough.interceptor;

import com.alibaba.fastjson.JSON;
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
public class OrderFlowInterceptor implements HandlerInterceptor {
    @Value("${flow.sendip}")
    private String[] ip;
    @Autowired
    AsyncUtils asyncUtils;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("流量订购拦截器线程名称"+Thread.currentThread().getName());
        long l = System.currentTimeMillis();
        //获取请求url，将chabotURI从路径中提取出来
        String requestURL = RequestUtils.getIp(request);
        BufferedReader reader=null;
        boolean flag=false;
        for (int i = 0; i < ip.length; i++) {
            //判断ip
            if(requestURL.contains(ip[i])){
                flag=true;
            }
        }
        if(flag){
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
                jsonObject= JSON.parseObject(str.toString());
                log.info("商户流量订购内容：{}",jsonObject);
                HttpHeaders headers=new HttpHeaders();
                headers.set("Content-Type","Application/json;charset=UTF-8");
                HttpEntity entity=new HttpEntity(jsonObject,headers);
                ResponseEntity<String> response1 = restTemplate.postForEntity("http://localhost:9004/flowPassthrough/order-pass/test", entity, String.class);
                //通道响应数据
                String body = response1.getBody();
                log.info("流量订购响应内容：{}",body);
                response.getWriter().write(body);

                //TODO 记录
                JSONObject bodyJson = JSONObject.parseObject(body);
                String respCode=bodyJson.containsKey("respCode")?bodyJson.getString("respCode"):"";
                String orderNo=bodyJson.containsKey("orderNo")?bodyJson.getString("orderNo"):"";
                String respMsg=bodyJson.containsKey("respMsg")?bodyJson.getString("respMsg"):"";
                jsonObject.put("orderNo",orderNo);
                jsonObject.put("status",2);
                jsonObject.put("info",respMsg);
                if(StringUtils.hasText(respCode)&&"100000".equals(respCode)){
                    jsonObject.put("status",1);
                }
                log.info("流量订购入库数据：{}",jsonObject);
                asyncUtils.insert(jsonObject);

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                reader.close();
            }
        }else {
            //响应 ip限制信息
            JSONObject json=new JSONObject();
            json.put("respCode","400004");
            json.put("orderNo","0");
            json.put("respMsg","IP受限");
            response.setContentType("Application/json;charset=UTF-8");
            response.getWriter().write(json.toJSONString());
        }

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
