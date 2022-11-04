package com.xzkj.operatorService.entity.operatorModel;

import java.io.Serializable;

/**
 * 审核实体类
 */
public class AuthModel implements Serializable {
    /**
     * 被审核信息的操作流水号
     */
    private String authMessageId;
    /**
     * 审核结果：
     * 1-通过
     * 2-不通过
     */
    private String authStatus;
    /**
     * 审核原因
     */
    private String comment;
    /**
     * 审核人员账号信息
     */
    private String authPerson;
    /**
     * 审核时间，2020-04-04T23:59:00Z
     */
    private String authTime;
    /**
     * 操作流水号
     */
    private String messageId;
    /**
     * 审核类型：
     * 1-新增审核
     * 2-变更审核
     * 3-调试白名单审核
     */
    private String authType;
    /**
     * Chatbot ID，包含域名部分
     */
    private String chatbotId;
    /**
     * 被审核的非直签客户编码
     */
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
