package com.xzkj.accessService.entity.xmlToModel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;

public class InboundMessage implements Serializable {
    private static final long serialVersionUID = 13L;
    @JacksonXmlProperty(localName = "destinationAddress")
    private String destinationAddress;

    @JacksonXmlProperty(localName = "senderAddress")
    private String senderAddress;

    @JacksonXmlProperty(localName = "origUser")
    private String origUser;

    @JacksonXmlProperty(localName = "resourceURL")
    private String resourceURL;

    @JacksonXmlProperty(localName = "link")
    private Link link;

    @JacksonXmlProperty(localName = "messageId")
    private String messageId;

    @JacksonXmlProperty(localName = "contentType")
    private String contentType;

    @JacksonXmlProperty(localName = "bodyText")
    @JacksonXmlCData
    private String bodyText;

    @JacksonXmlProperty(localName = "conversationID")
    private String conversationID;

    @JacksonXmlProperty(localName = "contributionID")
    private String contributionID;

    @JacksonXmlProperty(localName = "serviceCapability")
    private ServiceCapability serviceCapability;

    @JacksonXmlProperty(localName = "contentEncoding")
    private String contentEncoding;

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getOrigUser() {
        return origUser;
    }

    public void setOrigUser(String origUser) {
        this.origUser = origUser;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getContributionID() {
        return contributionID;
    }

    public void setContributionID(String contributionID) {
        this.contributionID = contributionID;
    }

    public ServiceCapability getServiceCapability() {
        return serviceCapability;
    }

    public void setServiceCapability(ServiceCapability serviceCapability) {
        this.serviceCapability = serviceCapability;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    @Override
    public String toString() {
        return "InboundMessage{" +
                "destinationAddress='" + destinationAddress + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", origUser='" + origUser + '\'' +
                ", resourceURL='" + resourceURL + '\'' +
                ", link=" + link.toString() +
                ", messageId='" + messageId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", bodyText='" + bodyText + '\'' +
                ", conversationID='" + conversationID + '\'' +
                ", contributionID='" + contributionID + '\'' +
                '}';
    }

    public InboundMessage() {
    }
}
