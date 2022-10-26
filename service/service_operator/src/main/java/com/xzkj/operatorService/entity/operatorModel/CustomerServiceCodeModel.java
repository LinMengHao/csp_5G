package com.xzkj.operatorService.entity.operatorModel;

import java.io.Serializable;

public class CustomerServiceCodeModel implements Serializable {
    private String serviceCode;
    private String extCode;
    private String customerNum;
    private String type;

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(String customerNum) {
        this.customerNum = customerNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CustomerServiceCodeModel{" +
                "serviceCode='" + serviceCode + '\'' +
                ", extCode='" + extCode + '\'' +
                ", customerNum='" + customerNum + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
