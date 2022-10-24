package com.xzkj.accessService.entity.operatorModel;

import java.io.Serializable;

/**
 * 对接运营平台 订购消息实体类
 */
public class OrderModel implements Serializable {
    private static final long serialVersionUID = 9L;
    //EC集团客户编码，EC ID
    private String customerNum;
    //产品订单号
    private String prodordSkuNum;
    //产品订购关系ID，计费编码
    private String prodistSkuNum;
    //集团客户联系人
    private String contactName;
    //集团客户联系人手机号码
    private String contactPhone;
    /*
       大区：
        01-华北
        02-东北
        03-华东北
        04-华东南
        05-华南
        06-华中
        07-西南
        08-西北
     */
    private String officeCode;
    //产品类型：
    //01-5G消息
    private String prodType;
    /*
        服务代码类型：
            01-5G消息专有码号
            02-全网短信自携号
            03-省内短信自携号
            04-已有全网短信码号
            05-已有省内短信码号
     */
    private String serviceCodeType;
    /*
        服务代码，“服务代码类型”为03、05，则数字服务代码后加省份后缀，格式为数字+省份后缀；其他则格式为数字
     */
    private String serviceCode;
    //是否为CSP平台
    //1-是
    //0-否
    private String cspFlag;
    //CSP平台名称，CSP平台Flag等于1时必填
    private String cspName;
    //CSP平台编码，CSP平台Flag等于1时必填
    private String cspId;
    /*
        操作类型：
            1-新增
            2-修改
            3-取消
            4-暂停
            5-恢复
     */
    private String opType;
    //操作时间，新增时必填，例子2020-04-04T23:59:00Z
    private String opTime;
    //版本号，从1开始递增
    private String eTag;
    //操作流水号
    private String messageId;



    public OrderModel() {
    }

    public String getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(String customerNum) {
        this.customerNum = customerNum;
    }

    public String getProdordSkuNum() {
        return prodordSkuNum;
    }

    public void setProdordSkuNum(String prodordSkuNum) {
        this.prodordSkuNum = prodordSkuNum;
    }

    public String getProdistSkuNum() {
        return prodistSkuNum;
    }

    public void setProdistSkuNum(String prodistSkuNum) {
        this.prodistSkuNum = prodistSkuNum;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getProdType() {
        return prodType;
    }

    public void setProdType(String prodType) {
        this.prodType = prodType;
    }

    public String getServiceCodeType() {
        return serviceCodeType;
    }

    public void setServiceCodeType(String serviceCodeType) {
        this.serviceCodeType = serviceCodeType;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getCspFlag() {
        return cspFlag;
    }

    public void setCspFlag(String cspFlag) {
        this.cspFlag = cspFlag;
    }

    public String getCspName() {
        return cspName;
    }

    public void setCspName(String cspName) {
        this.cspName = cspName;
    }

    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
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

    @Override
    public String toString() {
        return "OrderModel{" +
                "customerNum='" + customerNum + '\'' +
                ", prodordSkuNum='" + prodordSkuNum + '\'' +
                ", prodistSkuNum='" + prodistSkuNum + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", officeCode='" + officeCode + '\'' +
                ", prodType='" + prodType + '\'' +
                ", serviceCodeType='" + serviceCodeType + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", cspFlag='" + cspFlag + '\'' +
                ", cspName='" + cspName + '\'' +
                ", cspId='" + cspId + '\'' +
                ", opType='" + opType + '\'' +
                ", opTime='" + opTime + '\'' +
                ", eTag='" + eTag + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
