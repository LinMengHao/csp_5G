package com.xzkj.accessService.entity.xmlToModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "msg")
public class Messages implements Serializable {
    private static final long serialVersionUID = 15L;
    //当前请求消息id
    @JsonIgnore
    private String id;
    //主叫地址
    @JsonIgnore
    private String mainAddress;
    @JacksonXmlProperty(localName = "inboundMessage")
    private InboundMessage inboundMessage;

    @JacksonXmlProperty(localName = "deliveryInfo")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DeliveryInfo> deliveryInfos=new ArrayList<>();

    @JacksonXmlProperty(localName = "description")
    private String description;

    @JacksonXmlProperty(localName = "link")
    private Link link;

    @JacksonXmlProperty(localName = "messageId")
    private String messageId;

    @JacksonXmlProperty(localName = "address")
    private String address;

    @JacksonXmlProperty(localName = "status")
    private String status;

    @JacksonXmlProperty(localName = "contentEncoding")
    private String contentEncoding;

    @JsonIgnore
    private String chatbotURI;

    public Messages() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(String mainAddress) {
        this.mainAddress = mainAddress;
    }

    public InboundMessage getInboundMessage() {
        return inboundMessage;
    }

    public void setInboundMessage(InboundMessage inboundMessage) {
        this.inboundMessage = inboundMessage;
    }

    public List<DeliveryInfo> getDeliveryInfos() {
        return deliveryInfos;
    }

    public void setDeliveryInfos(List<DeliveryInfo> deliveryInfos) {
        this.deliveryInfos = deliveryInfos;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getChatbotURI() {
        return chatbotURI;
    }

    public void setChatbotURI(String chatbotURI) {
        this.chatbotURI = chatbotURI;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "id='" + id + '\'' +
                ", mainAddress='" + mainAddress + '\'' +
                ", inboundMessage=" + inboundMessage +
                ", deliveryInfos=" + deliveryInfos +
                ", description='" + description + '\'' +
                ", link=" + link +
                ", messageId='" + messageId + '\'' +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                ", contentEncoding='" + contentEncoding + '\'' +
                ", chatbotURI='" + chatbotURI + '\'' +
                '}';
    }
}
