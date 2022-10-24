package com.xzkj.accessService.entity.msgModel;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextMsgModel implements Serializable {
    private static final long serialVersionUID = 6L;
    private String id;
    //服务器基础URL
    private String serverRoot;

    //客户端想使用的API版本号
    private String apiVersion;

    //Chatbot的SIP URI
    private String chatbotURI;

    //状态报告路径
    private String callbackURL;

    private String cspid;

    private String csptoken;

    private String messageId;

    //鉴权token
    private String authorization;

    //接受方地址TEL-URI（电话），1个以上接收方时填 00000
    private String address;

    //接收方地址TEL-URI。
    //群发消息中可携带接收方地址数组，最多支持100个号码。
    private List<String> destinationAddress=new ArrayList<>();

    //，号隔开电话，用于保存到destinationAddress


    //发送方地址，填写Chatbot的URI
    private String senderAddress;

    /*
    消息类型：
        1）text/plain 普通文本(包含地位位置消息)。5G消息系统中默认Chatbot下发位置信息采用Geolocation fallback SMS方式。
        2）application/vnd.gsma.rcs-ft-http+xml，普通文件消息，可以是图片、音频、视频、名片等消息。
        3）application/vnd.gsma.botmessage.v1.0+json chatbot卡片消息。
        4）multipart/mixed; boundary="[delimiter]" 携带悬浮菜单的消息
     */
    private String contentType;

    //唯一标识主被叫用户间的一个聊天对话，建议采用UUID

    private String conversationID;

    //唯一标识一个聊天会话，建议采用UUID
    private String contributionID;

    //ServiceCapability标签下的字段  值为 ChatbotSA
    private String capabilityId;

    //ServiceCapability标签下的字段 版本号，如 +g.gsma.rcs.botversion=&quot;#=1&quot;
    private String version;



    //消息内容
    private String bodyText;

    //文件上传id
    private String tid;

    //文件公网URL地址
    private String fileURL;

    //下行消息类型
    private String type;



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
    private String clientCorrelator;


    private String shortMessageSupported;


    private String storeSupported;


    private String multimediaMessageSupported;

    //GMT 时间
    private String Date;

    private Map<String,String> map=new HashMap<>();

    //状态报告通知参数
    private Map<String,String> notify=new HashMap<>();

    private List<FileInfo> fileInfos=new ArrayList<>();

    //  主叫地址
    private String callingAddress;

    private String contentEncoding;

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getCallingAddress() {
        return callingAddress;
    }

    public void setCallingAddress(String callingAddress) {
        this.callingAddress = callingAddress;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Map<String, String> getNotify() {
        return notify;
    }

    public void setNotify(Map<String, String> notify) {
        this.notify = notify;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public List<FileInfo> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(List<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TextMsgModel{" +
                "serverRoot='" + serverRoot + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                ", chatbotURI='" + chatbotURI + '\'' +
                ", callbackURL='" + callbackURL + '\'' +
                ", cspid='" + cspid + '\'' +
                ", csptoken='" + csptoken + '\'' +
                ", messageId='" + messageId + '\'' +
                ", authorization='" + authorization + '\'' +
                ", address='" + address + '\'' +
                ", destinationAddress=" + destinationAddress +
                ", senderAddress='" + senderAddress + '\'' +
                ", contentType='" + contentType + '\'' +
                ", conversationID='" + conversationID + '\'' +
                ", contributionID='" + contributionID + '\'' +
                ", capabilityId='" + capabilityId + '\'' +
                ", version='" + version + '\'' +
                ", bodyText='" + bodyText + '\'' +
                ", tid='" + tid + '\'' +
                ", fileURL='" + fileURL + '\'' +
                ", reportRequest=" + reportRequest +
                ", clientCorrelator='" + clientCorrelator + '\'' +
                ", shortMessageSupported='" + shortMessageSupported + '\'' +
                ", storeSupported='" + storeSupported + '\'' +
                ", multimediaMessageSupported='" + multimediaMessageSupported + '\'' +
                ", Date='" + Date + '\'' +
                ", map=" + map +
                ", notify=" + notify +
                ", fileInfos=" + fileInfos +
                ", callingAddress='" + callingAddress + '\'' +
                '}';
    }
}
