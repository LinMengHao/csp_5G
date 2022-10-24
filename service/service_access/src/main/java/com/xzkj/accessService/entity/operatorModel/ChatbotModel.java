package com.xzkj.accessService.entity.operatorModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//机器人信息实体类
public class ChatbotModel implements Serializable {
    private static final long serialVersionUID = 7L;
    private String chatBotId;
    //Chatbot ID，包含域名部分
    private String chatbotId;
    /*
        11-新增审核不通过
        12-变更审核不通过
        20-管理平台新增审核中
        21-管理平台变更审核中
        24-上架审核中
        25-上架审核不通过
        26调试白名单审核
        27-调试白名单审核不通过
        30-在线
        31-已下线
        40-暂停
        41-黑名单
        42-已下线（关联的CSP被下线）
        50-调试
     */
    private String status;
    private String customerNum;
    private String agentCustomerNum;
    private String cspId;
    private String serviceCode;
    private String name;
    private String logo;
    private String sms;
    private String callback;
    private String email;
    private String website;
    private String tcPage;
    private String address;
    private String colour;
    private String backgroundImage;
    private List<String> category=new ArrayList<>();
    private String provider;
    private String providerSwitchCode;
    private String description;
    private String provinceCode;
    private String cityCode;
    private String officeCode;
    private String lon;
    private String lat;
    private String autograph;
    private String attachment;
    private String createTime;
    private String opTime;
    private String eTag;
    private String messageId;
    private String debugWhiteAddress;
    private String auditPerson;
    private String auditOpinion;
    private String auditTime;
    private String actualIssueIndustry;

    public ChatbotModel() {
    }

    public String getChatBotId() {
        return chatBotId;
    }

    public void setChatBotId(String chatBotId) {
        this.chatBotId = chatBotId;
    }

    public String getChatbotId() {
        return chatbotId;
    }

    public void setChatbotId(String chatbotId) {
        this.chatbotId = chatbotId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(String customerNum) {
        this.customerNum = customerNum;
    }

    public String getAgentCustomerNum() {
        return agentCustomerNum;
    }

    public void setAgentCustomerNum(String agentCustomerNum) {
        this.agentCustomerNum = agentCustomerNum;
    }

    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTcPage() {
        return tcPage;
    }

    public void setTcPage(String tcPage) {
        this.tcPage = tcPage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderSwitchCode() {
        return providerSwitchCode;
    }

    public void setProviderSwitchCode(String providerSwitchCode) {
        this.providerSwitchCode = providerSwitchCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAutograph() {
        return autograph;
    }

    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOpTime() {
        return opTime;
    }

    public void setOpTime(String opTime) {
        this.opTime = opTime;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDebugWhiteAddress() {
        return debugWhiteAddress;
    }

    public void setDebugWhiteAddress(String debugWhiteAddress) {
        this.debugWhiteAddress = debugWhiteAddress;
    }

    public String getAuditPerson() {
        return auditPerson;
    }

    public void setAuditPerson(String auditPerson) {
        this.auditPerson = auditPerson;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getActualIssueIndustry() {
        return actualIssueIndustry;
    }

    public void setActualIssueIndustry(String actualIssueIndustry) {
        this.actualIssueIndustry = actualIssueIndustry;
    }

    @Override
    public String toString() {
        return "ChatbotModel{" +
                "chatBotId='" + chatBotId + '\'' +
                ", chatbotId='" + chatbotId + '\'' +
                ", status='" + status + '\'' +
                ", customerNum='" + customerNum + '\'' +
                ", agentCustomerNum='" + agentCustomerNum + '\'' +
                ", cspId='" + cspId + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", sms='" + sms + '\'' +
                ", callback='" + callback + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", tcPage='" + tcPage + '\'' +
                ", address='" + address + '\'' +
                ", colour='" + colour + '\'' +
                ", backgroundImage='" + backgroundImage + '\'' +
                ", category=" + category +
                ", provider='" + provider + '\'' +
                ", providerSwitchCode='" + providerSwitchCode + '\'' +
                ", description='" + description + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", officeCode='" + officeCode + '\'' +
                ", lon='" + lon + '\'' +
                ", lat='" + lat + '\'' +
                ", autograph='" + autograph + '\'' +
                ", attachment='" + attachment + '\'' +
                ", createTime='" + createTime + '\'' +
                ", opTime='" + opTime + '\'' +
                ", eTag='" + eTag + '\'' +
                ", messageId='" + messageId + '\'' +
                ", debugWhiteAddress='" + debugWhiteAddress + '\'' +
                ", auditPerson='" + auditPerson + '\'' +
                ", auditOpinion='" + auditOpinion + '\'' +
                ", auditTime='" + auditTime + '\'' +
                ", actualIssueIndustry='" + actualIssueIndustry + '\'' +
                '}';
    }
}
