package com.xzkj.accessService.service.impl;


import com.xzkj.accessService.conf.HttpsSkipRequestFactory;
import com.xzkj.accessService.entity.msgModel.FileInfo;
import com.xzkj.accessService.entity.msgModel.MessageModel;
import com.xzkj.accessService.entity.msgModel.ResponseModel;
import com.xzkj.accessService.entity.msgModel.TextMsgModel;
import com.xzkj.accessService.service.ISendService;
import com.xzkj.accessService.utils.HttpHeaderUtil;
import com.xzkj.accessService.utils.XMLUtil;
import com.xzkj.accessService.utils.XmlToBean;
import com.xzkj.utils.R;
import com.xzkj.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class SendServiceImpl implements ISendService {
    //打印数据到特定日志文件
    private static final Logger logger= LogManager.getLogger("shortMessageRollingFile");

    //https
    private static RestTemplate httpsTemplate=new RestTemplate(new HttpsSkipRequestFactory());
    @Autowired
    HttpHeaderUtil httpHeaderUtil;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    MessageModel messageModel;
    //默认处理
    @Override
    public R sendMsg(TextMsgModel msg) {
        //TODO 了解字段后，写通用方法

        return R.ok();
    }

    @Override
    public R sendTxtMsg(TextMsgModel textMsgModel) {
        long l = System.currentTimeMillis();
        log.info("消费开始时间："+l);
        log.info("消费者线程： "+Thread.currentThread().getName());
        System.out.println(textMsgModel.toString());
        Map<String, String> map = textMsgModel.getMap();
        //请求头 鉴权信息有效时间为24小时
        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel, Duration.ofHours(24));
        //不缓存鉴权信息
//        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel);
        //请求体
        //标签 带上需要的随机id
        String uuid32 = UUIDUtil.getUUID32();
        map.put("conversationID",uuid32);

        //消息内容请求体
        String xml = XMLUtil.txtTemplateXml(map, "msg:outboundMessageRequest", "urn:oma:xml:rest:netapi:messaging:1", "outboundIMMessage");
        HttpEntity<String> entity=new HttpEntity<String>(xml,headers);
        //请求携带机器人自身信息
         ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + textMsgModel.getServerRoot() +
                 "/messaging/group/" + textMsgModel.getApiVersion() + "/outbound/" + textMsgModel.getChatbotURI() + "/requests", entity, String.class);
//         配置文件绑定机器人（一次一个）
//         ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + messageModel.getServerRoot() +
//                "/messaging/group/" + messageModel.getApiVersion() + "/outbound/" + messageModel.getChatbotURI() + "/requests", entity, String.class);
        //响应数据
        //xml转化bean
        ResponseModel responseModel = XmlToBean.xmlToResponseModel(response.getBody().toString());
        log.info(response.getStatusCode().toString());
        log.info(response.getHeaders().toString());
        log.info(responseModel.toString());
        long l1 = System.currentTimeMillis();
        log.info("消费结束时间："+l1);
        log.info("消费时间: "+(l1-l));
        return R.ok();
    }

    @Override
    public R sendCardMsg(TextMsgModel textMsgModel) {
        System.out.println(textMsgModel.toString());
        Map<String, String> map = textMsgModel.getMap();
        //请求头 鉴权信息有效时间为24小时
        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel, Duration.ofHours(24));
//        不缓存
//        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel);
        //请求体
        //标签 带上需要的随机id
        String uuid32 = UUIDUtil.getUUID32();
        map.put("conversationID",uuid32);
        String xml = XMLUtil.txtTemplateXml(map, "msg:outboundMessageRequest", "urn:oma:xml:rest:netapi:messaging:1", "outboundIMMessage");
        HttpEntity<String> entity=new HttpEntity<String>(xml,headers);
        //http测试使用
        ResponseEntity<String> response = httpsTemplate.postForEntity("https://" +
                textMsgModel.getServerRoot() + "/messaging/group/" + textMsgModel.getApiVersion() + "/outbound/"
                + textMsgModel.getChatbotURI() + "/requests", entity, String.class);
        //         配置文件绑定机器人（一次一个）
//         ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + messageModel.getServerRoot() +
//                "/messaging/group/" + messageModel.getApiVersion() + "/outbound/" + messageModel.getChatbotURI() + "/requests", entity, String.class);
        //响应数据
        //xml转化bean
        ResponseModel responseModel = XmlToBean.xmlToResponseModel(response.getBody().toString());
        log.info(response.getStatusCode().toString());
        log.info(response.getHeaders().toString());
        log.info(responseModel.toString());
        return R.ok();
    }

    @Override
    public R sendFileMsg(TextMsgModel textMsgModel) {
        System.out.println(textMsgModel.toString());
        Map<String, String> map = textMsgModel.getMap();
        List<FileInfo> fileInfos = textMsgModel.getFileInfos();
        //请求头 鉴权信息有效时间为24小时
        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel, Duration.ofHours(24));
//        不缓存
//        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel);
        //请求体
        //标签 带上需要的随机id
        String uuid32 = UUIDUtil.getUUID32();
        map.put("conversationID",uuid32);
        String bodyText = XMLUtil.FileTemplateXml(fileInfos, "file", "urn:gsma:params:xml:ns:rcs:rcs:fthttp");
        map.put("bodyText",bodyText);
        String xml = XMLUtil.txtTemplateXml(map, "msg:outboundMessageRequest", "urn:oma:xml:rest:netapi:messaging:1", "outboundIMMessage");
        HttpEntity<String> entity=new HttpEntity<String>(xml,headers);
        ResponseEntity<String> response = httpsTemplate.postForEntity("https://" +
                textMsgModel.getServerRoot() + "/messaging/group/" + textMsgModel.getApiVersion() +
                "/outbound/" + textMsgModel.getChatbotURI() + "/requests", entity, String.class);
        //         配置文件绑定机器人（一次一个）
//         ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + messageModel.getServerRoot() +
//                "/messaging/group/" + messageModel.getApiVersion() + "/outbound/" + messageModel.getChatbotURI() + "/requests", entity, String.class);
        //响应数据
        //xml转化bean
        ResponseModel responseModel = XmlToBean.xmlToResponseModel(response.getBody().toString());
        log.info(response.getStatusCode().toString());
        log.info(response.getHeaders().toString());
        log.info(responseModel.toString());
        return R.ok();
    }

    @Override
    public R sendMenuMsg(TextMsgModel textMsgModel) {
        System.out.println(textMsgModel.toString());
        Map<String, String> map = textMsgModel.getMap();
        //请求头 鉴权信息有效时间为24小时
        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel, Duration.ofHours(24));
//        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel);
        //请求体
        //标签 带上需要的随机id
        String uuid32 = UUIDUtil.getUUID32();
        map.put("conversationID",uuid32);
        String xml = XMLUtil.txtTemplateXml(map, "msg:outboundMessageRequest", "urn:oma:xml:rest:netapi:messaging:1", "outboundIMMessage");
        HttpEntity<String> entity=new HttpEntity<String>(xml,headers);
        //https
        // ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + messageModel.getServerRoot() + "/messaging/group/" + messageModel.getApiVersion() + "/outbound/" + messageModel.getChatbotURI() + "/requests", entity, String.class);
        //http测试使用
        ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + textMsgModel.getServerRoot() + "/messaging/group/" + textMsgModel.getApiVersion() +
                "/outbound/" + textMsgModel.getChatbotURI() + "/requests", entity, String.class);
        //         配置文件绑定机器人（一次一个）
//         ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + messageModel.getServerRoot() +
//                "/messaging/group/" + messageModel.getApiVersion() + "/outbound/" + messageModel.getChatbotURI() + "/requests", entity, String.class);
        //响应数据
        //xml转化bean
        ResponseModel responseModel = XmlToBean.xmlToResponseModel(response.getBody().toString());
        log.info(response.getStatusCode().toString());
        log.info(response.getHeaders().toString());
        log.info(responseModel.toString());
        return R.ok();
    }

    @Override
    public R sendContributionMsg(TextMsgModel textMsgModel) {
        System.out.println(textMsgModel.toString());
        Map<String, String> map = textMsgModel.getMap();
        //请求头 鉴权信息有效时间为24小时
        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel, Duration.ofHours(24));
//        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel);
        //请求体
        //标签 带上需要的随机id
        String uuid32 = UUIDUtil.getUUID32();
        map.put("conversationID",uuid32);
        //获取上行信息交互id
        String inReplyToContribution = textMsgModel.getContributionID();
        map.put("inReplyToContribution",inReplyToContribution);
        String xml = XMLUtil.txtTemplateXml(map, "msg:outboundMessageRequest", "urn:oma:xml:rest:netapi:messaging:1", "outboundIMMessage");
        HttpEntity<String> entity=new HttpEntity<String>(xml,headers);
        //https
        ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + textMsgModel.getServerRoot() + "/messaging/interaction/" + textMsgModel.getApiVersion() +
                "/outbound/" + textMsgModel.getChatbotURI() + "/requests", entity, String.class);
//        ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + messageModel.getServerRoot() + "/messaging/group/"
//                 + messageModel.getApiVersion() + "/outbound/" + messageModel.getChatbotURI() + "/requests", entity, String.class);
        //xml转化bean
        ResponseModel responseModel = XmlToBean.xmlToResponseModel(response.getBody().toString());
        log.info(response.getStatusCode().toString());
        log.info(response.getHeaders().toString());
        log.info(responseModel.toString());
        return R.ok();
    }

    @Override
    public R sendUpMsg(TextMsgModel textMsgModel) {
        System.out.println(textMsgModel.toString());
        Map<String, String> map = textMsgModel.getMap();
        List<FileInfo> fileInfos = textMsgModel.getFileInfos();
        //请求头 鉴权信息有效时间为24小时
        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel, Duration.ofHours(24));
//        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel);
        //请求体
        //标签 带上需要的随机id
        String uuid32 = UUIDUtil.getUUID32();
        map.put("conversationID",uuid32);
        String bodyText = XMLUtil.FileTemplateXml(fileInfos, "file", "urn:gsma:params:xml:ns:rcs:rcs:fthttp");
        map.put("bodyText",bodyText);
        String xml = XMLUtil.txtTemplateXml(map, "msg:outboundMessageRequest", "urn:oma:xml:rest:netapi:messaging:1", "outboundIMMessage");
        HttpEntity<String> entity=new HttpEntity<String>(xml,headers);
        //https
        ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + textMsgModel.getServerRoot() + "/messaging/group/" + textMsgModel.getApiVersion() +
                "/outbound/" + textMsgModel.getChatbotURI() + "/requests", entity, String.class);
//        ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + messageModel.getServerRoot() + "/messaging/group/" + messageModel.getApiVersion() +
//                "/outbound/" + messageModel.getChatbotURI() + "/requests", entity, String.class);
        //xml转化bean
        ResponseModel responseModel = XmlToBean.xmlToResponseModel(response.getBody().toString());
        log.info(response.getStatusCode().toString());
        log.info(response.getHeaders().toString());
        log.info(responseModel.toString());
        return R.ok();
    }

    @Override
    public R withdraw(TextMsgModel textMsgModel) {
        System.out.println(textMsgModel.toString());
        //请求头 鉴权信息有效时间为24小时
        HttpHeaders headers = httpHeaderUtil.getHttpHeadersByText(textMsgModel, Duration.ofHours(24));
        //请求体
        Document document= DocumentHelper.createDocument();
        Element root = document.addElement("msg:messageStatusReport", "urn:oma:xml:rest:netapi:messaging:1");
        Element status = root.addElement("status");
        status.setText("RevokeRequested");
        Element address = root.addElement("address");
        address.setText("tel:+86"+textMsgModel.getAddress());
        String xmlStr = XMLUtil.getXmlStr(document, "UTF-8", false);
        HttpEntity<String> entity=new HttpEntity<String>(xmlStr,headers);
        //https
        ResponseEntity<String> response = httpsTemplate.exchange("https://" + textMsgModel.getServerRoot() + "/messaging/" + textMsgModel.getApiVersion() +
                "/outbound/" + textMsgModel.getChatbotURI() + "/requests/"+textMsgModel.getMessageId()+"/status", HttpMethod.PUT, entity, String.class);
//        ResponseEntity<String> response = httpsTemplate.exchange("https://" + messageModel.getServerRoot() + "/messaging/" + messageModel.getApiVersion() +
//                "/outbound/" + messageModel.getChatbotURI() + "/requests/"+textMsgModel.getMessageId()+"/status", HttpMethod.PUT, entity, String.class);
        log.info(response.getStatusCode().toString());
        log.info(response.getHeaders().toString());
        return R.ok();
    }
}
