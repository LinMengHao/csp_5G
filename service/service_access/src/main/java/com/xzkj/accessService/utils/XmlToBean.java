package com.xzkj.accessService.utils;

import com.alibaba.fastjson.JSONObject;
import com.xzkj.accessService.constants.GroupMsgResponseCode;
import com.xzkj.accessService.entity.msgModel.DeliveryInfo;
import com.xzkj.accessService.entity.msgModel.ResponseModel;
import com.xzkj.accessService.entity.xmlToModel.Data;
import com.xzkj.accessService.entity.xmlToModel.FileInfos;
import com.xzkj.accessService.entity.xmlToModel.Multimedia;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class XmlToBean {
    public static ResponseModel xmlToResponseModel(String s){
        ResponseModel responseModel=null;
        try {
            Document document= DocumentHelper.parseText(s);
            responseModel=new ResponseModel();
            //获取根节点
            Element rootElement = document.getRootElement();
            Element messageId = rootElement.element("messageId");
            Element clientCorrelator = rootElement.element("clientCorrelator");
            if (messageId != null) {
                responseModel.setMessageId(messageId.getText());
            }
            if (clientCorrelator!=null){
                responseModel.setClientCorrelator(clientCorrelator.getText());
            }
            if (rootElement.element("policyException")!=null){
                Element policyException = rootElement.element("policyException");
                Element exceptionId = policyException.element("exceptionId");
                Element text = policyException.element("text");
                Element variables = policyException.element("variables");
                responseModel.setVariables(variables.getText());
                responseModel.setText(text.getText());
                responseModel.setExceptionId(exceptionId.getText());
                responseModel.setResponseCode(GroupMsgResponseCode.ERROR);
            }else if(rootElement.element("serviceException")!=null){
                Element policyException = rootElement.element("serviceException");
                Element exceptionId = policyException.element("exceptionId");
                Element text = policyException.element("text");
                Element variables = policyException.element("variables");
                responseModel.setVariables(variables.getText());
                responseModel.setText(text.getText());
                responseModel.setExceptionId(exceptionId.getText());
                responseModel.setResponseCode(GroupMsgResponseCode.ERROR);
            }else {
                //群发响应节点
                List<DeliveryInfo> deliveryInfos = responseModel.getDeliveryInfos();
                Element deliveryInfoList = rootElement.element("deliveryInfoList");
                if(deliveryInfoList!=null){
                    List<Element> elements = deliveryInfoList.elements("deliveryInfo");
                    for (int i = 0; i < elements.size(); i++) {
                        DeliveryInfo deliveryInfo=new DeliveryInfo();
                        Element element = elements.get(i);
                        Element address = element.element("address");
                        deliveryInfo.setAddress(address.getText());
                        Element deliveryStatus = element.element("deliveryStatus");
                        deliveryInfo.setDeliveryStatus(deliveryStatus.getText());
                        Element policyException = element.element("policyException");
                        if(policyException!=null){
                            Element exceptionId = policyException.element("exceptionId");
                            Element text = policyException.element("text");
                            Element variables = policyException.element("variables");
                            deliveryInfo.setText(text.getText());
                            deliveryInfo.setVariables(variables.getText());
                            deliveryInfo.setExceptionId(exceptionId.getText());
                            responseModel.setResponseCode(GroupMsgResponseCode.SEGMENT);
                        }
                        deliveryInfos.add(deliveryInfo);
                    }
                }
            }
            System.out.println(responseModel.toString());
        }catch (DocumentException e){
            e.printStackTrace();
        }
        return responseModel;
    }


    //map转java对象
    public static Object mapToObject(Map<String, String> map, Class<?> beanClass) throws Exception {
        String jsonStr = JSONObject.toJSONString(map);
        return JSONObject.parseObject(jsonStr, beanClass);
    }

    //上行文件
    public static Multimedia xmlToMultimedia(String s){
        Multimedia multimedia=new Multimedia();
        try {
            //出现 不允许有匹配 "[xX][mM][lL]" 的处理指令目标 则表示<?xml version="1.0" encoding="UTF-8"?>没有顶格写
            Document document=DocumentHelper.parseText(s);
            Element file = document.getRootElement();
            String xmlns = file.attributeValue("xmlns");
            List<FileInfos> fileInfos = multimedia.getFileInfos();
            List<Element> elements = file.elements();
            for (int i = 0; i < elements.size(); i++) {
                FileInfos fileInfo=new FileInfos();
                Element element = elements.get(i);
                String type = element.attributeValue("type");
                fileInfo.setType(type);
                Element file_size = element.element("file-size");
                if(file_size!=null){
                    fileInfo.setFileSize(file_size.getText());
                }
                Element file_name = element.element("file-name");
                if(file_name!=null){
                    fileInfo.setFileName(file_name.getText());
                }
                Element content_type = element.element("content-type");
                if(content_type!=null){
                    fileInfo.setContentType(content_type.getText());
                }

                Element data = element.element("data");
                Data data1=new Data();
                if(data!=null){
                    if(StringUtils.hasText(data.attributeValue("url"))){
                        data1.setUrl(data.attributeValue("url"));
                    }
                    if(StringUtils.hasText(data.attributeValue("url"))){
                        data1.setUntil(data.attributeValue("until"));
                    }
                }
                fileInfo.setData(data1);
                fileInfos.add(fileInfo);
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return multimedia;
    }
}
