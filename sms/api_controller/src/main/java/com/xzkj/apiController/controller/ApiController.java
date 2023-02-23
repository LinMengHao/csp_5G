package com.xzkj.apiController.controller;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.apiController.service.IApiService;
import com.xzkj.apiController.util.MD5Utils;
import com.xzkj.apiController.util.PublicUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping(value="mms")
public class ApiController {
    public static Logger logger = LoggerFactory.getLogger("ApiController");
    @Resource
    private IApiService apiService;
    //@Autowired
    //private HelloServiceClient helloServiceClientImpl;

    @RequestMapping("/hello")
    public String hello(@RequestParam String name){
        System.out.println("------------------"+name);
        return "7777";//helloServiceClient.hello(name);
    }

    @RequestMapping("submit")
    @ResponseBody
    public String mmsSend(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.mmsSend(body,ipAddress);

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsSend,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }
    @RequestMapping("list")
    @ResponseBody
    public String mmsList(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.mmsList(body,ipAddress);

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsList,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }

    @RequestMapping("modelOld/submit")
    @ResponseBody
    public String modelSubmit(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.modelSubmit(body);

        long duration = System.currentTimeMillis()-begin;
        logger.info("modelSubmit,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        //TODO 异步 邮件提醒
        return result;
    }

    @RequestMapping("modelOld/one")
    @ResponseBody
    public String modelOne(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.modelOne(body,ipAddress);

        long duration = System.currentTimeMillis()-begin;
        logger.info("modelOne,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }

    @RequestMapping("sign/submit")
    @ResponseBody
    public String signSubmit(HttpServletRequest request, HttpServletResponse response, @RequestBody String body){
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.signSubmit(body);

        long duration = System.currentTimeMillis()-begin;
        logger.info("modelSubmit,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        //TODO 异步 邮件提醒
        return result;
    }

    @RequestMapping("sign/one")
    @ResponseBody
    public String signOne(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.signOne(body,ipAddress);

        long duration = System.currentTimeMillis()-begin;
        logger.info("modelOne,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }

    /**
     * 模版报备
     * @param request
     * @param response
     * @param body
     * @return
     */
    @RequestMapping("model/submitToChannel")
    @ResponseBody
    public String modelSubmitToChannel(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();
        logger.info("模版数据：{}",body);
        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.modelSubmitToChannel(body);

        long duration = System.currentTimeMillis()-begin;
        logger.info("modelSubmitToChannel,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }

    /**
     * 签名报备
     * @param request
     * @param response
     * @param body
     * @return
     */
    @RequestMapping("sign/submitToChannel")
    @ResponseBody
    public String signSubmitToChannel(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();
        logger.info("签名数据：{}",body);
        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.signSubmitToChannel(body);

        long duration = System.currentTimeMillis()-begin;
        logger.info("signSubmitToChannel,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }

    @RequestMapping("report")
    @ResponseBody
    public String mmsReport(HttpServletRequest request, HttpServletResponse response, @RequestParam String table,@RequestParam String ids) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.mmsReport(table,ids,ipAddress);

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsSend,table:{},ids:{},ipAddress:{},result:{},duration:{}",table,ids,ipAddress,result,duration);
        return result;
    }

    /**
     * 上传她思上海电信白名单  应急版本
     * @param request
     * @param body
     * @return
     */
    @RequestMapping("shanghaidianxin/whitelist")
    @ResponseBody
    public String whitelist(HttpServletRequest request,  @RequestBody String body){
        long begin = System.currentTimeMillis();
        logger.info("白名单码号：{}",body);
        String ipAddress = PublicUtil.getClientIp(request);
        JSONObject json=JSONObject.parseObject(body);
        String phoneStr=json.getString("Phones");
        String[] phones = phoneStr.split(",");
        JSONObject jsonObject=new JSONObject();
        String siId="22100670010006";
        String key="ViEW4aoQNU";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        String authenticator = MD5Utils.MD5Encode(siId+date+key).toUpperCase();
        jsonObject.put("SiID",siId);
        jsonObject.put("Authenticator",authenticator);
        jsonObject.put("Date",date);
        jsonObject.put("Method","whitelist");
        jsonObject.put("Type",1);
        jsonObject.put("Phones",phones);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        JSONObject result=null;
        try {
            HttpPost httppost = new HttpPost("http://124.126.120.102:8896/sapi/whitelist");
            httppost.setConfig(RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build());
            httppost.setHeader("Content-Type","application/json");
            StringEntity reqEntity = new StringEntity(jsonObject.toString(),"utf-8");
            httppost.setEntity(reqEntity);
            System.err.println("executing request " + httppost.getRequestLine());

            CloseableHttpResponse response = httpclient.execute(httppost);
            System.out.println("response:"+response);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String responseEntityStr = EntityUtils.toString(response.getEntity());
                    System.out.println("响应参数：" + responseEntityStr);
                    System.out.println("Response content length: " + resEntity.getContentLength());
                    result=JSONObject.parseObject(responseEntityStr);
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long duration = System.currentTimeMillis()-begin;
        logger.info("上海电信白名单提交,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result.toJSONString();
    }

    /**
     * 上传修治上海电信白名单  应急版本
     * @param request
     * @param body
     * @return
     */
    @RequestMapping("xzshanghaidianxin/whitelist")
    @ResponseBody
    public String xzwhitelist(HttpServletRequest request,  @RequestBody String body){
        long begin = System.currentTimeMillis();
        logger.info("白名单码号：{}",body);
        String ipAddress = PublicUtil.getClientIp(request);
        JSONObject json=JSONObject.parseObject(body);
        String phoneStr=json.getString("Phones");
        String[] phones = phoneStr.split(",");
        JSONObject jsonObject=new JSONObject();
        String siId="22100700020006";
        String key="HZt7pD1wRo";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        String authenticator = MD5Utils.MD5Encode(siId+date+key).toUpperCase();
        jsonObject.put("SiID",siId);
        jsonObject.put("Authenticator",authenticator);
        jsonObject.put("Date",date);
        jsonObject.put("Method","whitelist");
        jsonObject.put("Type",1);
        jsonObject.put("Phones",phones);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        JSONObject result=null;
        try {
            HttpPost httppost = new HttpPost("http://124.126.120.102:8896/sapi/whitelist");
            httppost.setConfig(RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build());
            httppost.setHeader("Content-Type","application/json");
            StringEntity reqEntity = new StringEntity(jsonObject.toString(),"utf-8");
            httppost.setEntity(reqEntity);
            System.err.println("executing request " + httppost.getRequestLine());

            CloseableHttpResponse response = httpclient.execute(httppost);
            System.out.println("response:"+response);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String responseEntityStr = EntityUtils.toString(response.getEntity());
                    System.out.println("响应参数：" + responseEntityStr);
                    System.out.println("Response content length: " + resEntity.getContentLength());
                    result=JSONObject.parseObject(responseEntityStr);
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long duration = System.currentTimeMillis()-begin;
        logger.info("修治上海电信白名单提交,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result.toJSONString();
    }

    /**
     * 新版模版api上传
     * @param request
     * @param response
     * @param body
     * @return
     */
    @RequestMapping("model/submit")
    @ResponseBody
    public String modelNewSubmit(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.modelNewSubmit(body);

        long duration = System.currentTimeMillis()-begin;
        logger.info("modelSubmit,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        //TODO 异步 邮件提醒
        return result;
    }

    /**
     * 新版模版api查询
     * @param request
     * @param response
     * @param body
     * @return
     */
    @RequestMapping("model/one")
    @ResponseBody
    public String modelNewOne(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = apiService.modelNewOne(body,ipAddress);

        long duration = System.currentTimeMillis()-begin;
        logger.info("modelOne,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }

    @RequestMapping("test/report")
    @ResponseBody
    public String test(HttpServletRequest request, HttpServletResponse response, @RequestBody String body){
        logger.info("test 模版/签名回调数据测试,body:{}",body);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code","000");
        return jsonObject.toString();
    }
}
