package com.xzkj.accessService.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xzkj.accessService.constants.ResultCode;
import com.xzkj.accessService.entity.xmlToModel.*;
import com.xzkj.accessService.service.IReceiveService;
import com.xzkj.accessService.utils.XmlToBean;
import com.xzkj.utils.Base64Utils;
import com.xzkj.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Slf4j
@Service
public class ReceiveServiceImpl implements IReceiveService {
    //打印数据到特定日志文件
    private static final Logger logger= LogManager.getLogger("shortMessageRollingFile");

    @Autowired
    RestTemplate restTemplate;
    @Override
    public R notifyStatus(Messages messages) {
        List<DeliveryInfo> deliveryInfos = messages.getDeliveryInfos();
        //主叫地址
        String mainAddress = messages.getMainAddress();
        for (int i = 0; i < deliveryInfos.size(); i++) {
            DeliveryInfo deliveryInfo = deliveryInfos.get(i);
            //发送回执消息的发送方地址
            String address = deliveryInfo.getAddress();
            String status = deliveryInfo.getDeliveryStatus();
            if(ResultCode.MESSAGESENT.getCode().equals(status)){
                log.info("主叫地址： "+mainAddress+" "+"发送回执消息的发送方地址："+address+" "+ResultCode.MESSAGESENT.getMessage());
            }
            else if(ResultCode.MESSAGEDISPLAYED.getCode().equals(status)){
                log.info("主叫地址： "+mainAddress+" "+"发送回执消息的发送方地址："+address+" "+ResultCode.MESSAGEDISPLAYED.getMessage());
            }
            else if(ResultCode.DELIVERYIMPOSSIBLE.getCode().equals(status)){
                log.info("主叫地址： "+mainAddress+" "+"发送回执消息的发送方地址："+address+" "+ResultCode.DELIVERYIMPOSSIBLE.getMessage());
                if(StringUtils.hasText(deliveryInfo.getDescription())){
                    log.info("失败原因："+deliveryInfo.getDescription());
                }
                if(StringUtils.hasText(deliveryInfo.getText())){
                    log.info("备注："+deliveryInfo.getText());
                }
            }
            else if(ResultCode.DELIVEREDTONETWORK.getCode().equals(status)){
                if("SMS".equals(deliveryInfo.getDescription())){
                    log.info("主叫地址： "+mainAddress+" "+"发送回执消息的发送方地址："+address+" 已转短消息发送"+ResultCode.DELIVEREDTONETWORK.getMessage());
                }
                if("MMS".equals(deliveryInfo.getDescription())){
                    log.info("主叫地址： "+mainAddress+" "+"发送回执消息的发送方地址："+address+" 已彩信消息发送"+ResultCode.DELIVEREDTONETWORK.getMessage());
                }
            }
            else if(ResultCode.DELIVEREDTOTERMINAL.getCode().equals(status)){
                log.info("主叫地址： "+mainAddress+" "+"发送回执消息的发送方地址："+address+" "+ResultCode.DELIVEREDTOTERMINAL.getMessage()+" "+messages.getDescription());
            }

        }
        return R.ok();
    }

    /**
     * 接收上行消息
     * @param messages
     * @return
     */
    @Override
    public R receiveMsg(Messages messages) {
        InboundMessage inboundMessage = messages.getInboundMessage();
        String contentType = inboundMessage.getContentType();
        if(StringUtils.hasText(contentType)){
            String[] s=new String[2];
            String[] str = contentType.replace(" ", "").split(";");
            s[0]=str[0];
            if(str.length<2){
                s[1]="charset=UTF-8";
            }else {
                s[1]=str[1];
            }

            if(StringUtils.hasText(messages.getContentEncoding())||StringUtils.hasText(inboundMessage.getContentEncoding())){
                s[1]="base64";
            }

            if("text/plain".equals(s[0])){
                //纯文本消息
                if("UTF-8".equals(s[1].split("=")[1].toUpperCase())){
                    log.info("消息内容："+inboundMessage.getBodyText());
                }else {
                    //base64编码内容  解码
                    String decode = Base64Utils.decode(inboundMessage.getBodyText());
                    log.info("消息内容："+decode);
                    inboundMessage.setBodyText(decode);
                }
                //TODO 对接机器人业务 对接Rasa聊天机器人
                //组包，json
                JSONObject data=new JSONObject();
                //主叫
                data.put("sender",messages.getMainAddress());
                data.put("message",inboundMessage.getBodyText());
                System.out.println(data.toJSONString());
                HttpHeaders headers=new HttpHeaders();
                headers.set("Content-Type","application/json");
                HttpEntity<JSONObject>entity=new HttpEntity<>(data,headers);
                ResponseEntity<String> response = restTemplate.postForEntity("http://82.157.251.233:5005/webhooks/rest/webhook", entity, String.class);
                log.info(response.getBody());


                return R.ok().data("data",inboundMessage.getBodyText());
            }else if("application/vnd.gsma.rcs-ft-http+xml".equals(s[0])){
                //媒体文件消息
                Multimedia multimedia=null;
                if("UTF-8".equals(s[1].split("=")[1])){
                    //
                    multimedia = XmlToBean.xmlToMultimedia(inboundMessage.getBodyText());
                    //...
                }else {
                    //base64编码内容  解码
                    String decode = Base64Utils.decode(inboundMessage.getBodyText());
                    //解析xml文件，整理媒体文件信息
                    multimedia = XmlToBean.xmlToMultimedia(decode);
                }
                //TODO 对接机器人业务
                return R.ok().data("data",multimedia);
            }else if("application/vnd.gsma.botsuggestion.response.v1.0+json".equals(s[0])){
                //基于建议回复/操作消息的回复
                if("UTF-8".equals(s[1].split("=")[1])){
                    log.info("消息内容："+inboundMessage.getBodyText());
                }else {
                    //base64编码内容  解码
                    String decode = Base64Utils.decode(inboundMessage.getBodyText());
                    inboundMessage.setBodyText(decode);
                }
                //TODO 对接机器人业务
                return R.ok().data("data",inboundMessage.getBodyText());
            }
        }
        return R.ok();
    }

    @Override
    public R withdrawNotify(Messages messages) {
        //根据status判断
        if("RevokeFailed".equals(messages.getStatus())){
            log.info("撤销失败");
        }else {
            log.info("撤销成功");
        }
        //TODO 对接机器人业务
        return R.ok();
    }

    @Override
    public R auditFile(Multimedia multimedia) {
        List<FileInfos> fileInfos = multimedia.getFileInfos();
        String authstatus = multimedia.getAuthstatus();
        String tid = multimedia.getTid();
        if(fileInfos != null && fileInfos.size() > 0){
            for (int i = 0; i < fileInfos.size(); i++) {
                FileInfos fileInfo = fileInfos.get(i);
                fileInfo.setTid(tid);
                fileInfo.setStatus(authstatus);
            }
            log.info(fileInfos.toString());
            //TODO 对接机器人业务
            return R.ok();
        }
        return R.error();
    }
}
