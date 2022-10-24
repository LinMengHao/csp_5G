package com.xzkj.accessService.entity.msgModel;





import com.xzkj.accessService.constants.GroupMsgResponseCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//响应解析

public class ResponseModel implements Serializable {

    private static final long serialVersionUID = 4L;

    private String code;

    private String message;

    private GroupMsgResponseCode responseCode=GroupMsgResponseCode.ALL;

    private String messageId;

    private String clientCorrelator;

    private List<DeliveryInfo> deliveryInfos=new ArrayList<>();

    private String exceptionId;
    private String text;
    private String variables;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GroupMsgResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(GroupMsgResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public String getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public ResponseModel() {
    }

    public List<DeliveryInfo> getDeliveryInfos() {
        return deliveryInfos;
    }

    public void setDeliveryInfos(List<DeliveryInfo> deliveryInfos) {
        this.deliveryInfos = deliveryInfos;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getClientCorrelator() {
        return clientCorrelator;
    }

    public void setClientCorrelator(String clientCorrelator) {
        this.clientCorrelator = clientCorrelator;
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", responseCode=" + responseCode +
                ", messageId='" + messageId + '\'' +
                ", clientCorrelator='" + clientCorrelator + '\'' +
                ", deliveryInfos=" + deliveryInfos +
                ", exceptionId='" + exceptionId + '\'' +
                ", text='" + text + '\'' +
                ", variables='" + variables + '\'' +
                '}';
    }
}
