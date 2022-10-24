package com.xzkj.accessService.entity.operatorModel;

import java.io.Serializable;

/**
 * 审核实体类
 */
public class AuthModel implements Serializable {
    private String authMessageId;
    private String authStatus;
    private String comment;
    private String authPerson;
    private String authTime;
    private String messageId;

    private String authType;

    private String chatbotId;

    private String customerNum;

    public AuthModel() {
    }

    public String getAuthMessageId() {
        return authMessageId;
    }

    public void setAuthMessageId(String authMessageId) {
        this.authMessageId = authMessageId;
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthPerson() {
        return authPerson;
    }

    public void setAuthPerson(String authPerson) {
        this.authPerson = authPerson;
    }

    public String getAuthTime() {
        return authTime;
    }

    public void setAuthTime(String authTime) {
        this.authTime = authTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getChatbotId() {
        return chatbotId;
    }

    public void setChatbotId(String chatbotId) {
        this.chatbotId = chatbotId;
    }

    public String getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(String customerNum) {
        this.customerNum = customerNum;
    }

    @Override
    public String toString() {
        return "AuthModel{" +
                "authMessageId='" + authMessageId + '\'' +
                ", authStatus='" + authStatus + '\'' +
                ", comment='" + comment + '\'' +
                ", authPerson='" + authPerson + '\'' +
                ", authTime='" + authTime + '\'' +
                ", messageId='" + messageId + '\'' +
                ", authType='" + authType + '\'' +
                ", chatbotId='" + chatbotId + '\'' +
                ", customerNum='" + customerNum + '\'' +
                '}';
    }
}
