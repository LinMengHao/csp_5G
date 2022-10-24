package com.xzkj.accessService.entity.xmlToModel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;

public class DeliveryInfo implements Serializable {
    private static final long serialVersionUID = 11L;
    @JacksonXmlProperty(localName = "address")
    private String address;

    @JacksonXmlProperty(localName = "messageId")
    private String messageId;

    @JacksonXmlProperty(localName = "deliveryStatus")
    private String deliveryStatus;

    @JacksonXmlProperty(localName = "description")
    private String description;

    @JacksonXmlProperty(localName = "text")
    private String text;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DeliveryInfo() {
    }
}
