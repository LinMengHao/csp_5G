package com.xzkj.accessService.entity.msgModel;

import java.io.Serializable;

//群发响应属性实体
public class DeliveryInfo implements Serializable {
    private static final long serialVersionUID = 18L;
    private String address;
    private String deliveryStatus;
    private String exceptionId;
    private String text;
    private String variables;

    public DeliveryInfo() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
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

    @Override
    public String toString() {
        return "DeliveryInfo{" +
                "address='" + address + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", exceptionId='" + exceptionId + '\'' +
                ", text='" + text + '\'' +
                ", variables='" + variables + '\'' +
                '}';
    }
}
