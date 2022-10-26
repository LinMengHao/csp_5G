package com.xzkj.operatorService.entity.operatorModel;

import java.io.Serializable;

public class SmsConfModel implements Serializable {
    private String chatbotId;
    private String servCode;
    private String smsBandWidth;
    private String otherCarrier;
    private String videoSign;
    private String prodId;
    private String apiUserName;
    private String apiPassword;

    public String getChatbotId() {
        return chatbotId;
    }

    public void setChatbotId(String chatbotId) {
        this.chatbotId = chatbotId;
    }

    public String getServCode() {
        return servCode;
    }

    public void setServCode(String servCode) {
        this.servCode = servCode;
    }

    public String getSmsBandWidth() {
        return smsBandWidth;
    }

    public void setSmsBandWidth(String smsBandWidth) {
        this.smsBandWidth = smsBandWidth;
    }

    public String getOtherCarrier() {
        return otherCarrier;
    }

    public void setOtherCarrier(String otherCarrier) {
        this.otherCarrier = otherCarrier;
    }

    public String getVideoSign() {
        return videoSign;
    }

    public void setVideoSign(String videoSign) {
        this.videoSign = videoSign;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getApiUserName() {
        return apiUserName;
    }

    public void setApiUserName(String apiUserName) {
        this.apiUserName = apiUserName;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public SmsConfModel() {
    }

    @Override
    public String toString() {
        return "SmsConfModel{" +
                "chatbotId='" + chatbotId + '\'' +
                ", servCode='" + servCode + '\'' +
                ", smsBandWidth='" + smsBandWidth + '\'' +
                ", otherCarrier='" + otherCarrier + '\'' +
                ", videoSign='" + videoSign + '\'' +
                ", prodId='" + prodId + '\'' +
                ", apiUserName='" + apiUserName + '\'' +
                ", apiPassword='" + apiPassword + '\'' +
                '}';
    }
}
