package com.xzkj.platform.sign.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.TreeEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * 签名对象 e_model_sign
 * 
 * @author linmenghao
 * @date 2023-01-31
 */
public class EModelSign extends TreeEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    @Excel(name = "通道id")
    private Long channelId;

    /** 签名id */
    @Excel(name = "签名id")
    private String signId;

    /** 公司id */
    private Long companyId;

    /** 账号id */
    private Long appId;

    @Excel(name = "用户名称")
    private String userName;

    @Excel(name = "公司名称")
    private String companyName;

    /** 账号名称 */
    @Excel(name = "账号名称")
    private String appName;

    /**  */
    private Long userId;

    /** 省份 */
    private String ecProvince;

    /** 地市 */
    private String ecCity;

    /** 服务代码(扩展后的码号 */
    @Excel(name = "服务代码(扩展后的码号")
    private String serviceCode;

    /** 报备签名内容 */
    @Excel(name = "报备签名内容")
    private String reportSignContent;

    /** 实际发送企业 */
    @Excel(name = "实际发送企业")
    private String ecName;

    /** 行业属性1、信安行业属性(至多只能选五个,最少一个)2、信安行业属性值对应的含义：1-公共管理和社会组织2-国际组织3-交通运输、仓储和邮政业4-建筑业5-电力、燃气及水的生产和供应业6-科学研究、技术服务和地质勘察业7-租赁和商务服务业8-房地产业9-金融业10-住宿和餐饮业11-批发和零售业12-信息传输、计算机服务和软件业13-文化、体育和娱乐业14-卫生、社会保障和社会福利业15-教育16-居民服务和其他服务业17-水利、环境和公共设施管理业18-制造业19-采矿业20-农、林、牧、渔21-国防 */
    @Excel(name = "行业属性1、信安行业属性(至多只能选五个,最少一个)2、信安行业属性值对应的含义：1-公共管理和社会组织2-国际组织3-交通运输、仓储和邮政业4-建筑业5-电力、燃气及水的生产和供应业6-科学研究、技术服务和地质勘察业7-租赁和商务服务业8-房地产业9-金融业10-住宿和餐饮业11-批发和零售业12-信息传输、计算机服务和软件业13-文化、体育和娱乐业14-卫生、社会保障和社会福利业15-教育16-居民服务和其他服务业17-水利、环境和公共设施管理业18-制造业19-采矿业20-农、林、牧、渔21-国防")
    private String rcsIndustry;

    /** 行业分类13个行业分类，请选择(只能选一个)1.党政军2.民生3.医疗器械、药店4.电商5-沿街商铺（中小）6-教育培训7-房地产8-游戏9-金融10-物流11-微商（个人）12-企业（大型）13-其他 */
    @Excel(name = "行业分类13个行业分类，请选择(只能选一个)1.党政军2.民生3.医疗器械、药店4.电商5-沿街商铺", readConverterExp = "中=小")
    private String industry;

    /** 客户分类：0：直签客户1：代理商 */
    @Excel(name = "客户分类：0：直签客户1：代理商")
    private Long customerType;

    /** 操作类型：1-新增，2-编辑，3-删除编辑和删除时签名编码必填 */
    private Long operatorType;

    /** 授权函(png格式或pdf格式文件 上传文件大小限制最高2M) */
    @Excel(name = "授权函(png格式或pdf格式文件 上传文件大小限制最高2M)")
    private String uploadFile;


    private String filepath;
    /** 备用 */
    @Excel(name = "备用")
    private String info;

    /** 回调地址 */
    private String backUrl;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    /** 父id */
    @Excel(name = "父id")
    private Long pid;
    @Excel(name = "来源")
    private Long source;

    @Excel(name = "策略")
    private String idea;

    /** 移动是否支持*/
    private Long toCmcc;
    /** 联通是否支持*/
    private Long toUnicom;
    /** 电信是否支持*/
    private Long toTelecom;
    /** 国际是否支持*/
    private Long toInternational;

    /** 父签名id*/
    private String pSignId;

    /** 通道侧签名id*/
    private String channelSignId;


    public String getChannelSignId() {
        return channelSignId;
    }

    public void setChannelSignId(String channelSignId) {
        this.channelSignId = channelSignId;
    }

    public String getIdea() {
        return idea;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }

    public String getpSignId() {
        return pSignId;
    }

    public void setpSignId(String pSignId) {
        this.pSignId = pSignId;
    }

    public Long getToCmcc() {
        return toCmcc;
    }

    public void setToCmcc(Long toCmcc) {
        this.toCmcc = toCmcc;
    }

    public Long getToUnicom() {
        return toUnicom;
    }

    public void setToUnicom(Long toUnicom) {
        this.toUnicom = toUnicom;
    }

    public Long getToTelecom() {
        return toTelecom;
    }

    public void setToTelecom(Long toTelecom) {
        this.toTelecom = toTelecom;
    }

    public Long getToInternational() {
        return toInternational;
    }

    public void setToInternational(Long toInternational) {
        this.toInternational = toInternational;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setSignId(String signId)
    {
        this.signId = signId;
    }

    public String getSignId() 
    {
        return signId;
    }
    public void setCompanyId(Long companyId) 
    {
        this.companyId = companyId;
    }

    public Long getCompanyId() 
    {
        return companyId;
    }
    public void setAppId(Long appId) 
    {
        this.appId = appId;
    }

    public Long getAppId() 
    {
        return appId;
    }
    public void setAppName(String appName) 
    {
        this.appName = appName;
    }

    public String getAppName() 
    {
        return appName;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setEcProvince(String ecProvince) 
    {
        this.ecProvince = ecProvince;
    }

    public String getEcProvince() 
    {
        return ecProvince;
    }
    public void setEcCity(String ecCity) 
    {
        this.ecCity = ecCity;
    }

    public String getEcCity() 
    {
        return ecCity;
    }
    public void setServiceCode(String serviceCode) 
    {
        this.serviceCode = serviceCode;
    }

    public String getServiceCode() 
    {
        return serviceCode;
    }
    public void setReportSignContent(String reportSignContent) 
    {
        this.reportSignContent = reportSignContent;
    }

    public String getReportSignContent() 
    {
        return reportSignContent;
    }
    public void setEcName(String ecName) 
    {
        this.ecName = ecName;
    }

    public String getEcName() 
    {
        return ecName;
    }
    public void setRcsIndustry(String rcsIndustry) 
    {
        this.rcsIndustry = rcsIndustry;
    }

    public String getRcsIndustry() 
    {
        return rcsIndustry;
    }
    public void setIndustry(String industry) 
    {
        this.industry = industry;
    }

    public String getIndustry() 
    {
        return industry;
    }
    public void setCustomerType(Long customerType) 
    {
        this.customerType = customerType;
    }

    public Long getCustomerType() 
    {
        return customerType;
    }
    public void setOperatorType(Long operatorType) 
    {
        this.operatorType = operatorType;
    }

    public Long getOperatorType() 
    {
        return operatorType;
    }
    public void setUploadFile(String uploadFile) 
    {
        this.uploadFile = uploadFile;
    }

    public String getUploadFile() 
    {
        return uploadFile;
    }
    public void setInfo(String info) 
    {
        this.info = info;
    }

    public String getInfo() 
    {
        return info;
    }
    public void setBackUrl(String backUrl) 
    {
        this.backUrl = backUrl;
    }

    public String getBackUrl() 
    {
        return backUrl;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setPid(Long pid) 
    {
        this.pid = pid;
    }

    public Long getPid() 
    {
        return pid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("channelId", channelId)
                .append("signId", signId)
                .append("companyId", companyId)
                .append("appId", appId)
                .append("userName", userName)
                .append("companyName", companyName)
                .append("appName", appName)
                .append("userId", userId)
                .append("ecProvince", ecProvince)
                .append("ecCity", ecCity)
                .append("serviceCode", serviceCode)
                .append("reportSignContent", reportSignContent)
                .append("ecName", ecName)
                .append("rcsIndustry", rcsIndustry)
                .append("industry", industry)
                .append("customerType", customerType)
                .append("operatorType", operatorType)
                .append("uploadFile", uploadFile)
                .append("filepath", filepath)
                .append("info", info)
                .append("backUrl", backUrl)
                .append("status", status)
                .append("pid", pid)
                .append("source", source)
                .append("toCmcc", toCmcc)
                .append("toUnicom", toUnicom)
                .append("toTelecom", toTelecom)
                .append("toInternational", toInternational)
                .append("pSignId", pSignId)
                .toString();
    }
}
