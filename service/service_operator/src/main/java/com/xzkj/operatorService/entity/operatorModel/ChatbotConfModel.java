package com.xzkj.operatorService.entity.operatorModel;

import java.io.Serializable;

public class ChatbotConfModel implements Serializable {
    private String chatbotId;
    private int opType;
    private int concurrent;
    private int State;
    private int amount;
    private int mAmount;
    private int serviceRange;
    private int filesizeLimit;
    private int eTag;
    private String cspToken;
    private String opTime;
    private String messageId;

    public ChatbotConfModel() {
    }

    public String getChatbotId() {
        return chatbotId;
    }

    public void setChatbotId(String chatbotId) {
        this.chatbotId = chatbotId;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public int getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(int concurrent) {
        this.concurrent = concurrent;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getmAmount() {
        return mAmount;
    }

    public void setmAmount(int mAmount) {
        this.mAmount = mAmount;
    }

    public int getServiceRange() {
        return serviceRange;
    }

    public void setServiceRange(int serviceRange) {
        this.serviceRange = serviceRange;
    }

    public int getFilesizeLimit() {
        return filesizeLimit;
    }

    public void setFilesizeLimit(int filesizeLimit) {
        this.filesizeLimit = filesizeLimit;
    }

    public int geteTag() {
        return eTag;
    }

    public void seteTag(int eTag) {
        this.eTag = eTag;
    }

    public String getCspToken() {
        return cspToken;
    }

    public void setCspToken(String cspToken) {
        this.cspToken = cspToken;
    }

    public String getOpTime() {
        return opTime;
    }

    public void setOpTime(String opTime) {
        this.opTime = opTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "ChatbotConfModel{" +
                "chatbotId='" + chatbotId + '\'' +
                ", opType=" + opType +
                ", concurrent=" + concurrent +
                ", State=" + State +
                ", amount=" + amount +
                ", mAmount=" + mAmount +
                ", serviceRange=" + serviceRange +
                ", filesizeLimit=" + filesizeLimit +
                ", eTag=" + eTag +
                ", cspToken='" + cspToken + '\'' +
                ", opTime='" + opTime + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
