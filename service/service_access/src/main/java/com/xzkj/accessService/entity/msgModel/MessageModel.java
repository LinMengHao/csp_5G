package com.xzkj.accessService.entity.msgModel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//信息体基类(用于测试，实际使用，作为基类，接受参数)
@Component
@PropertySource("classpath:conf/msg.properties")
public class MessageModel  implements Serializable {
    private static final long serialVersionUID = 2L;
    //服务器基础URL
    @Value("${msg.serverRoot}")
    private String serverRoot;

    //客户端想使用的API版本号
    @Value("${msg.apiVersion}")
    private String apiVersion;

    //Chatbot的SIP URI
    @Value("${msg.chatbotURI}")
    private String chatbotURI;

    @Value("${msg.cspid}")
    private String cspid;

    @Value("${msg.csptoken}")
    private String csptoken;

    //鉴权token
    private String authorization;

    //接受方地址TEL-URI（电话），1个以上接收方时填 00000
    @Value("${msg.address}")
    private String address;

    //接收方地址TEL-URI。
    //群发消息中可携带接收方地址数组，最多支持100个号码。
    private List<String> destinationAddress=new ArrayList<>();

    //，号隔开电话，用于保存到destinationAddress
    @Value("${msg.testAddress}")
    private String testAddress;

    //发送方地址，填写Chatbot的URI
    @Value("${msg.senderAddress}")
    private String senderAddress;

    /*
    消息类型：
        1）text/plain 普通文本(包含地位位置消息)。5G消息系统中默认Chatbot下发位置信息采用Geolocation fallback SMS方式。
        2）application/vnd.gsma.rcs-ft-http+xml，普通文件消息，可以是图片、音频、视频、名片等消息。
        3）application/vnd.gsma.botmessage.v1.0+json chatbot卡片消息。
        4）multipart/mixed; boundary="[delimiter]" 携带悬浮菜单的消息
     */
    @Value("${msg.contentType}")
    private String contentType;

    //唯一标识主被叫用户间的一个聊天对话，建议采用UUID

    private String conversationID;

    //唯一标识一个聊天会话，建议采用UUID
    private String contributionID;

    //ServiceCapability标签下的字段  值为 ChatbotSA
    @Value("${msg.capabilityId}")
    private String capabilityId;

    //ServiceCapability标签下的字段 版本号，如 +g.gsma.rcs.botversion=&quot;#=1&quot;
    @Value("${msg.version}")
    private String version;



    //消息内容
    @Value("${msg.bodyText}")
    private String bodyText;

    //reportRequest的值组合，中间用，号结合
    @Value("${msg.reports}")
    private String reports;

    /*
    状态事件报告列表，每个状态事件的可选值为:
        Sent: 消息已发送到网络中的下一节点
        Delivered: 消息以发送到被叫侧用户
        Displayed: 消息已在被叫侧终端上显示
        Failed: 消息没有发送到被叫侧
        Interworking: 消息转短信或转彩信（注：此处不区分索取转短信和彩信递送报告。转短信与转彩信递送报告在回复的递送报告中的description中描述）
     */

    private List<String> reportRequest=new ArrayList<>();

    //用户端关联数据，需要networkAPI在响应中带回内容
    @Value("${msg.clientCorrelator}")
    private String clientCorrelator;

    @Value("${msg.shortMessageSupported}")
    private String shortMessageSupported;

    @Value("${msg.storeSupported}")
    private String storeSupported;

    @Value("${msg.multimediaMessageSupported}")
    private String multimediaMessageSupported;

    //GMT 时间
    private String Date;

    public MessageModel() {
    }

    public String getShortMessageSupported() {
        return shortMessageSupported;
    }

    public void setShortMessageSupported(String shortMessageSupported) {
        this.shortMessageSupported = shortMessageSupported;
    }

    public String getStoreSupported() {
        return storeSupported;
    }

    public void setStoreSupported(String storeSupported) {
        this.storeSupported = storeSupported;
    }

    public String getMultimediaMessageSupported() {
        return multimediaMessageSupported;
    }

    public void setMultimediaMessageSupported(String multimediaMessageSupported) {
        this.multimediaMessageSupported = multimediaMessageSupported;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getReports() {
        return reports;
    }

    public void setReports(String reports) {
        this.reports = reports;
    }

    public List<String> getReportRequest() {
        return reportRequest;
    }

    public void setReportRequest(List<String> reportRequest) {
        this.reportRequest = reportRequest;
    }

    public String getClientCorrelator() {
        return clientCorrelator;
    }

    public void setClientCorrelator(String clientCorrelator) {
        this.clientCorrelator = clientCorrelator;
    }

    public String getTestAddress() {
        return testAddress;
    }

    public void setTestAddress(String testAddress) {
        this.testAddress = testAddress;
    }

    public String getCspid() {
        return cspid;
    }

    public void setCspid(String cspid) {
        this.cspid = cspid;
    }
    public String getCsptoken() {
        return csptoken;
    }

    public void setCsptoken(String csptoken) {
        this.csptoken = csptoken;
    }

    public String getServerRoot() {
        return serverRoot;
    }

    public void setServerRoot(String serverRoot) {
        this.serverRoot = serverRoot;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getChatbotURI() {
        return chatbotURI;
    }

    public void setChatbotURI(String chatbotURI) {
        this.chatbotURI = chatbotURI;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(List<String> destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }
}
