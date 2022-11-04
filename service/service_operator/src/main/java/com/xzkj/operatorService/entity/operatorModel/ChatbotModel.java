package com.xzkj.operatorService.entity.operatorModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//机器人信息实体类
public class ChatbotModel implements Serializable {
    private static final long serialVersionUID = 7L;
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
    /**
     * Chatbot归属的EC ID（客户编码），当agentCustomerNum非空时，此字段为非直签客户编码
     */
    private String customerNum;
    /**
     * 归属代理商的EC集团客户编码（对于BBOSS直签客户，如该客户下的订单“是否为CSP平台”字段值为“是”时，则此客户属于代理商），
     * 如Chatbot所属客户为非直签客户时，该字段必填
     */
    private String agentCustomerNum;
    /**
     * Chatbot归属的CSP平台 ID
     */
    private String cspId;
    /**
     * 接入层对CSP平台的鉴权Token，通过sha256(password)得到，并通过base64传输
     * password规则：8-20位大小写字母、数字、特殊符号
     */
    private String cspToken;
    /**
     * 服务代码，新增时必填
     */
    private String serviceCode;
    /**
     *操作类型：0-新增1-修改
     */
    private Integer opType;
    /**
     * Chatbot名称，新增时必填
     */
    private String name;
    /**
     * 机器人logo图标的url。（支持的文件类型：png、jpg、jpeg，尺寸400*400,附件大小限50K,）
     */
    private String logo;
    /**
     * 短信端口号。（系统自动取ChatbotId数字部分）
     */
    private String sms;
    /**
     * 认证状态
     * 0：未认证，默认
     * 1：认证
     */
    private String isAuth;
    /**
     * 认证主体名，isauth为1时必填。
     */
    private String authName;
    /**
     * 认证有效期，isauth为1时必填。例子:2020-04-04T23:59:00Z
     */
    private String authExpires;
    /**
     * 认证机构，isauth为1时必填
     */
    private String authOrg;
    /**
     *回叫号码
     */
    private String callback;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 主页地址
     */
    private String website;
    /**
     *包含条款和条件内容的网页地址
     */
    private String tcPage;
    /**
     * 办公地址。不能录入中英文中括号(【】[])
     */
    private String address;
    /**
     * 气泡颜色，例子#1E90FF。
     */
    private String colour;
    /**
     * 背景图片url地址（支持的文件类型：png、jpg、jpeg,附件大小限20K,）
     */
    private String backgroundImage;
    /**
     * 机器人分类，使用字符串数组，每种分类最长50字节。需要携带多个分类时，应携带多个category参数。
     */
    private List<String> category=new ArrayList<>();
    /**
     * 机器人提供者信息
     */
    private String provider;
    /**
     * 提供者开关，默认为1 (1-显示 0-不显示) csp新增chatbot时会有该参数
     */
    private String providerSwitchCode;
    /**
     * 机器人描述信息
     */
    private String description;
    /**
     * Chatbot固定菜单Json内容，采用Base64编码，编码前的内容长度不超过2048字节
     */
    private String menu;
    /**
     * 归属省份编号，新增时必填。参见附录4.2
     * 必须与归属CSP平台的省份一致
     */
    private String provinceCode;
    /**
     * 归属地市编码，新增时必填，参见附录4.3
     */
    private String cityCode;
    /**
     * 归属大区编号，新增时必填。参见附录4.1
     */
    private String officeCode;
    /**
     * 地理位置经度
     */
    private String lon;
    /**
     * 地理位置纬度
     */
    private String lat;
    /**
     * 机器人版本号，默认是2
     */
    private String version;
    /**
     * 签名
     */
    private String autograph;
    /**
     * 附件url地址。返回实体附件（支持的文件类型：pdf;doc;jpg;jpeg;gif;docx;rar;zip，大小限5M）
     */
    private String attachment;
    /**
     * 并发最大速率（每秒发送号码数
     */
    private Integer concurrent;
    /**
     * 需转换为6位二进制，从右往左，最右位为第一位：
     * 第一位：0支持主动消息下发（即允许群发，主动发送1条消息也属于群发）1不支持主动消息下发（即不允许群发）
     * 第二位：0支持上行触发消息下发（即支持交互消息）1不支持上行触发消息下发（即不支持交互消息，若不支持交互，则MaaP平台将拒绝Chatbot下行的所有带有InReplyTo-Contribution-id的消息，并返回HTTP 403 Forbidden响应。）
     * 第三位：0容许回落 1禁止回落
     * 第四位: 0支持上行UP1.0消息 1不支持上行UP1.0消息
     * 第五位：0允许上行 1不允许上行
     * 第六位：预留
     * （注：上行触发的消息下发，消息体会携带inReplyTo-Contribution-ID字段）
     */
    private Integer state;
    /**
     * 日最大消息下发量，0表示不限制
     */
    private Integer amount;
    /**
     * 月最大消息下发量，0表示不限制
     */
    private Integer mAmount;
    /**
     * 交互范围与Chatbot目录查询范围：
     * 0–省内，
     * 1–全网，
     * 2–其它（预留）
     */
    private Integer serviceRanage;
    /**
     * 上传文件大小限制，默认20M，与终端允许收发的最大文件大小保持一致
     */
    private Integer filesizeLimit;
    /**
     * Chatbot创建时间，新增时必填
     */
    private String createTime;
    /**
     * 操作时间，新增时必填，例子2020-04-04T23:59:00Z
     */
    private String opTime;
    /**
     * 版本号，从1开始递增
     */
    private String eTag;
    /**
     * 操作流水号
     */
    private String messageId;
    /**
     * 调试白名单，多个手机号用逗号隔开。只能是数字和逗号，逗号前后需为11位数字  csp平台新增时会有，yd运营平台同步时，可能没有
     */
    private String debugWhiteAddress;
    /**
     * csp审核人员
     */
    private String auditPerson;
    /**
     * csp审核意见
     */
    private String auditOpinion;
    /**
     * csp审核时间YYYY-MM-DD
     */
    private String auditTime;
    /**
     * 实际下发行业：
     * 1-党政军
     * 2-民生
     * 3-金融
     * 4-物流
     * 5-游戏
     * 6-电商
     * 7-微商（个人）
     * 8-沿街商铺（中小）
     * 9-企业（大型）
     * 10-教育培训
     * 11-房地产
     * 12-医疗器械、药店
     * 13-其他
     */
    private String actualIssueIndustry;
    /**
     *测试报告URl，根据地址获取实体附件（附件类型支持：pdf、doc、docx、jpg、jpeg、gif、docx、rar；大小限10M）
     */
    private String testReportUrl;
    /**
     *上架申请说明
     */
    private String putAwayExplain;
    /**
     * 调试白名单列表。只能是数字和逗号，逗号前后需为11位数字
     */
    private String chatBotWhiteList;

    public ChatbotModel() {
    }

    public String getChatBotWhiteList() {
        return chatBotWhiteList;
    }

    public void setChatBotWhiteList(String chatBotWhiteList) {
        this.chatBotWhiteList = chatBotWhiteList;
    }

    public String getTestReportUrl() {
        return testReportUrl;
    }

    public void setTestReportUrl(String testReportUrl) {
        this.testReportUrl = testReportUrl;
    }

    public String getPutAwayExplain() {
        return putAwayExplain;
    }

    public void setPutAwayExplain(String putAwayExplain) {
        this.putAwayExplain = putAwayExplain;
    }

    public String getChatbotId() {
        return chatbotId;
    }

    public void setChatbotId(String chatbotId) {
        this.chatbotId = chatbotId;
    }

    public String getCspToken() {
        return cspToken;
    }

    public void setCspToken(String cspToken) {
        this.cspToken = cspToken;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public String getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(String isAuth) {
        this.isAuth = isAuth;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public String getAuthExpires() {
        return authExpires;
    }

    public void setAuthExpires(String authExpires) {
        this.authExpires = authExpires;
    }

    public String getAuthOrg() {
        return authOrg;
    }

    public void setAuthOrg(String authOrg) {
        this.authOrg = authOrg;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(Integer concurrent) {
        this.concurrent = concurrent;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getmAmount() {
        return mAmount;
    }

    public void setmAmount(Integer mAmount) {
        this.mAmount = mAmount;
    }

    public Integer getServiceRanage() {
        return serviceRanage;
    }

    public void setServiceRanage(Integer serviceRanage) {
        this.serviceRanage = serviceRanage;
    }

    public Integer getFilesizeLimit() {
        return filesizeLimit;
    }

    public void setFilesizeLimit(Integer filesizeLimit) {
        this.filesizeLimit = filesizeLimit;
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
