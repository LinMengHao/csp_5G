package com.xzkj.operatorService.controller.operator;


import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Files;


import com.xzkj.operatorService.constants.Keys;
import com.xzkj.operatorService.entity.operatorModel.*;
import com.xzkj.operatorService.utils.HttpHeaderUtil;
import com.xzkj.operatorService.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("xiuzhi/bj_mobile/MsgSync/oc/v1")
public class OperatorReceiveController {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    HttpHeaderUtil httpHeaderUtil;

    @Autowired
    Keys keys;

//===============================================Chatbot接口======================================================================


    @PostMapping("oc/v1/syncchatbot")
    public OperatorResponse syncChatBot(@RequestBody ChatbotModel chatbotModel, HttpServletRequest request){
        //使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }

        if("0".equals(keys.getIsEncrypt())){
            //TODO 请求体解密 如果不用解密则直接使用参数
            BufferedReader reader = null;
            try {
                reader=request.getReader();
                StringBuilder s=new StringBuilder();
                String s1=null;
                while ((s1=reader.readLine())!=null){
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文："+dencrypt);
                OrderModel order = JSONObject.parseObject(dencrypt, OrderModel.class);
                System.out.println("解密后:"+order.toString());

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info("运营平台对Chatbot进行新增、修改操作时，将生效后信息同步至CSP平台："+chatbotModel.toString());
        }

        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }
    /**
     * 订购信息（运营->CSP）
     * @param orderModel
     * @param request
     * @return
     */
    @PostMapping("oc/v1/syncproduct")
    public OperatorResponse getOrderInfo(@RequestBody OrderModel orderModel, HttpServletRequest request){
        //使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }

        if("0".equals(keys.getIsEncrypt())){
            //TODO 请求体解密 如果不用解密则直接使用参数
            BufferedReader reader = null;
            try {
                reader=request.getReader();
                StringBuilder s=new StringBuilder();
                String s1=null;
                while ((s1=reader.readLine())!=null){
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文："+dencrypt);
                OrderModel order = JSONObject.parseObject(dencrypt, OrderModel.class);
                System.out.println("解密后:"+order.toString());

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info("订购信息同步消息内容："+orderModel.toString());
        }

        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

    /**
     * chatbot状态变更(运营平台->csp)
     * @return
     */
    @PostMapping("oc/v1/status")
    public OperatorResponse statusChange(@RequestBody ChatbotModel chatbotModel, HttpServletRequest request){

        //使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }

        if("0".equals(keys.getIsEncrypt())){
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader=request.getReader();
                StringBuilder s=new StringBuilder();
                String s1=null;
                while ((s1=reader.readLine())!=null){
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文："+dencrypt);
                ChatbotModel order = JSONObject.parseObject(dencrypt, ChatbotModel.class);
                System.out.println("解密后:"+order.toString());

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info("chatbot状态变更同步消息内容："+chatbotModel.toString());
        }


        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

    /**
     * 运营平台把注销的Chatbot同步给CSP平台(运营->csp)
     */
    @PostMapping("oc/v1/cancel")
    public OperatorResponse cancel(@RequestBody ChatbotModel chatbotModel,HttpServletRequest request){
        //使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }

        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                ChatbotModel order = JSONObject.parseObject(dencrypt, ChatbotModel.class);
                System.out.println("解密后:" + order.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info("运营平台把注销的Chatbot同步给CSP平台内容："+chatbotModel.toString());
        }
        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

    /**
     * 固定菜单审核结果通知（运营平台—>CSP平台)
     * @param authModel
     * @param request
     * @return
     */
    @PostMapping("oc/v1/authnotification")
    public OperatorResponse authnotification(@RequestBody AuthModel authModel, HttpServletRequest request){
//使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }

        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                AuthModel order = JSONObject.parseObject(dencrypt, AuthModel.class);
                System.out.println("解密后:" + order.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info(" 运营平台—>CSP平台 固定菜单审核结果通知内容："+authModel.toString());
        }


        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

    /**
     * 2.7.9 Chatbot审核（运营平台—>CSP平台）
     * @param authModel
     * @param request
     * @return
     */
    @PostMapping("oc/v1/audit")
    public OperatorResponse audit(@RequestBody AuthModel authModel,HttpServletRequest request){
//使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }

        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                AuthModel order = JSONObject.parseObject(dencrypt, AuthModel.class);
                System.out.println("解密后:" + order.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info("运营平台把Chatbot新增和变更的审核结果同步给CSP平台 内容："+authModel.toString());
        }

        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }


    /**
     * Chatbot 新增/变更配置信息（运营平台—>CSP平台）
     * @param chatbotModel
     * @param request
     * @return
     */
    @PostMapping("oc/v1/syncconfigchatbot")
    public OperatorResponse syncconfigchatbot(@RequestBody ChatbotModel chatbotModel, HttpServletRequest request){
//使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }
        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                ChatbotConfModel order = JSONObject.parseObject(dencrypt, ChatbotConfModel.class);
                System.out.println("解密后:" + order.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            //10进制转6位二进制
            String state = binaryString(chatbotModel.getState(), 6);
            /*
            CSP平台自行转换为6位二进制，
                从右往左，最右位为第一位：
                第一位：0支持主动消息下发（即允许群发，主动发送1条消息也属于群发） 1不支持主动消息下发（即不允许群发）
                第二位：0支持上行触发消息下发（即支持交互消息） 1不支持上行触发消息下发（即不支持交互消息，若不支持交互，则MaaP平台将拒绝Chatbot下行的所有带有InReplyTo-Contribution-id的消息，并返回HTTP 403 Forbidden响应。）
                第三位：0容许回落 1禁止回落
                第四位: 0支持上行UP1.0消息 1不支持上行UP1.0消息
                第五位：0 允许上行  1不允许上行
                第六位：0允许回落UP1.0  1不允许回落UP1.0
                （注：上行触发的消息下发，消息体会携带inReplyTo-Contribution-ID字段）
             */
            log.info("将chatbot配置信息同步CSP平台 内容："+chatbotModel.toString());
        }


        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

    /**
     * TODO 暂时不用
     * ChatBot视频短信平台配置信息（运营平台—>CSP平台）
     * @param confModel
     * @param request
     * @return
     */
    @PostMapping("oc/v1/syncvideoconf")
    public OperatorResponse syncvideoconf(@RequestBody SmsConfModel confModel,HttpServletRequest request){
//使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }
        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                SmsConfModel order = JSONObject.parseObject(dencrypt, SmsConfModel.class);
                System.out.println("解密后:" + order.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info("运营平台收到视频短信平台返回的 “线上报备签名”消息后，给Chatbot关联的CSP平台同步视频短信平台的配置信息和归属EC的接口账号、接口密码 内容："+confModel.toString());
        }

        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

//==================================================非直签客户接口===================================================

    /**
     * 2.8.1非直签客户新增（运营平台—>csp平台）
     * @param customerModel
     * @param request
     * @return
     */
    @PostMapping("oc/v1/client/new")
    public OperatorResponse clientNew(@RequestBody CustomerModel customerModel,HttpServletRequest request){

        //使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }
        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                CustomerModel order = JSONObject.parseObject(dencrypt, CustomerModel.class);
                System.out.println("解密后:" + order.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            //url 下载文件，可以调研downloadFiles下载到本地
            log.info("运营平台页面注册的非直签客户，在业务管理平台审核通过后，同步此消息给CSP平台 内容："+customerModel.toString());
        }

        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

    /**
     * 2.8.2非直签客户新增（CSP平台<—运营平台）
     * @param customerModel
     * @param request
     * @return
     */
    @PostMapping("oc/v1/client/change")
    public OperatorResponse clientChange(@RequestBody CustomerModel customerModel,HttpServletRequest request){

        //使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }
        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                CustomerModel order = JSONObject.parseObject(dencrypt, CustomerModel.class);
                System.out.println("解密后:" + order.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            //url 下载文件，可以调研downloadFiles下载到本地
            log.info("运营平台页面修改的非直签客户，在业务管理平台审核通过后，同步此消息给CSP平台 内容："+customerModel.toString());
        }


        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }


    /**
     * 2.8.3非直签客户审核（运营平台—>CSP平台）
     * @param authModel
     * @param request
     * @return
     */
    @PostMapping("oc/v1/client/audit")
    public OperatorResponse clientAudit(@RequestBody AuthModel authModel,HttpServletRequest request){
//使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }
        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                AuthModel order = JSONObject.parseObject(dencrypt, AuthModel.class);
                System.out.println("解密后:" + order.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info("运营平台把非直签客户注册和变更的审核结果同步给CSP平台 内容："+authModel.toString());
        }


        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

    /**
     * 2.8.4非直签客户状态变更（运营平台—>CSP平台）
     * @param customerModel
     * @param request
     * @return
     */
    @PostMapping("oc/v1/client/status")
    public OperatorResponse clientStatusChange(@RequestBody CustomerModel customerModel,HttpServletRequest request){

        //使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }

        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                CustomerModel order = JSONObject.parseObject(dencrypt, CustomerModel.class);
                System.out.println("解密后:" + order.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info("非直签客户状态发生变化后，运营平台把最新状态同步给CSP平台 内容："+customerModel.toString());
        }


        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

    /**
     * 2.8.5代理商分配服务代码（运营平台—>CSP平台）
     * @param
     * @param request
     * @return
     */
    @PostMapping("oc/v1/client/allotServiceCode")
    public OperatorResponse allotServiceCode(@RequestBody CustomerModel customerModel,HttpServletRequest request){

        //使用过滤器鉴权，下面代码可以去掉
        //鉴权
        boolean b = verify(request);
        if(!b){
            return OperatorResponse.error().resultDesc("鉴权失败");
        }

        if("0".equals(keys.getIsEncrypt())) {
            //TODO 请求体解密
            BufferedReader reader = null;
            try {
                reader = request.getReader();
                StringBuilder s = new StringBuilder();
                String s1 = null;
                while ((s1 = reader.readLine()) != null) {
                    s.append(s1);
                }
                String dencrypt = RSAUtils.dencrypt(s.toString(), keys.getXzcspPrivateKey());
                System.out.println("解密报文：" + dencrypt);
                CustomerServiceCodeModel order = JSONObject.parseObject(dencrypt, CustomerServiceCodeModel.class);
                System.out.println("解密后:" + order.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            log.info("代理商在运营平台给非直签客户分配服务代码后，运营平台将信息同步给CSP平台 内容："+customerModel.toString());
        }


        //TODO 存日志，存数据，同步数据
        return OperatorResponse.ok();
    }

    /**
     * 下载文件（运营->csp）
     * @param url
     * @param response
     * @throws IOException
     */
    @GetMapping("oc/v1/downloadFile")
    public void downloadFile(@RequestParam String url, HttpServletResponse response,HttpServletRequest request) throws IOException {
        boolean b = verify(request);
        if(!b){
            return ;
        }
        //TODO 路径问题在过程中总结规律
        String[] split = url.split("/");
        String filename = split[split.length - 1];
        File file=new File(url);
        byte[] bytes = Files.toByteArray(file);
        response.setHeader("Content-Disposition", "form-data; name=\"File\";filename="+filename);
//        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.close();
    }


    /**
     * 下载运营文件保存本地，并将返回文件本地地址
     * @param url
     * @return
     */
    public String downloadFiles(String url){
        String filePath="";
        HttpHeaders header = httpHeaderUtil.getHttpHeaderByRAS();
        HttpEntity<CustomerServiceCodeModel> entity=new HttpEntity<>(header);
        ResponseEntity<byte[]> response = restTemplate.exchange("http://183.233.87.225:8092/iodd/v1/downloadImage?attachFileId="+url, HttpMethod.GET, entity, byte[].class);
        FileOutputStream out=null;
        try{
            byte[] body=response.getBody();
            //获取文件名
            HttpHeaders headers1 = response.getHeaders();
            List<String> list = headers1.get("Content-Disposition");
            String s2 = list.get(0);
            String[] split = s2.split(";");
            String filename=null;
            for (String value : split) {
                if (value.trim().startsWith("filename")) {
                    filename = value.substring(value.indexOf('=') + 1).trim();
                }
                if(StringUtils.hasText(filename)){
                    if(body!=null){
                        String file="./FILE/KING/"+filename;
                        File dest = new File(file);
                        // 检测是否存在目录
                        if (!dest.exists()) {
                            dest.mkdirs();// 新建文件夹
                        }
                        out = new FileOutputStream(dest);
                        out.write(body,0,body.length);
                        out.flush();
                        filePath=file;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return filePath;
    }



    /**
     * 鉴权认证方法
     */
    public boolean verify(HttpServletRequest request){
        String authorization = request.getHeader("Authorization").split(" ")[1];
        String timestamp = request.getHeader("Timestamp");
        String requestId = request.getHeader("Request-ID");
        String appId = request.getHeader("App-ID");
        //通过appid找到token，先写死测试，后面可以使用名单这种方式
        String token="b4e10cf1b467e25247400a454c5099971448aeea798921dd94524af25224ba82";
        String sign=token+timestamp+requestId;
        boolean b = RSAUtils.verifySign(keys.getXzcspPublicKey(), sign, authorization);
        return b;
    }

    /**
     * 10进制转n位二进制
     * @param num
     * @param n
     * @return
     */
    public static String binaryString(int num,int n) {
        StringBuilder result = new StringBuilder();
        int flag = 1 << (n-1);
        for (int i = 0; i < n; i++) {
            int val = (flag & num) == 0 ? 0 : 1;
            result.append(val);
            num <<= 1;
        }
        return result.toString();
    }

    @RequestMapping("test")
    public void test(@RequestBody OrderModel orderModel){
        try {
            String s = JSONObject.toJSONString(orderModel);
            System.out.println("明文："+s);
            JSONObject jsonObject = JSONObject.parseObject(s);
            String encrypt = RSAUtils.encrypt(jsonObject.toString(), keys.getXzcspPublicKey());
            System.out.println("加密报文："+encrypt);
            restTemplate.postForEntity("http://localhost:8888/xiuzhi/bj_mobile/MsgSync/oc/v1/syncproduct",encrypt,String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
