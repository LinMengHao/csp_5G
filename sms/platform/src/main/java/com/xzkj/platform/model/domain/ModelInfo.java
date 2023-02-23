package com.xzkj.platform.model.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 模板信息e_model_info对象 e_model_info
 * 
 * @author ruoyi
 * @date 2022-11-24
 */
public class ModelInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    /** 模版父id */
    private String pid;

    /** 所属账号拓展码 */
    private Long appExt;

    /** 模版审核拓展码 */
    private Long modelExt;

    /** 移动是否支持*/
    private Integer toCmcc;
    /** 联通是否支持*/
    private Integer toUnicom;
    /** 电信是否支持*/
    private Integer toTelecom;
    /** 国际是否支持*/
    private Integer toInternational;

    /** 拓展码号 */
    private String extNum;

    /** 提交通道侧通道id */
    private String channelId;

    /** 主键id */
    private Long id;

    /** 公司id */
    @Excel(name = "公司id")
    private Long companyId;

    /** 账号id */
    @Excel(name = "账号id")
    private Long appId;

    /** 签名id */
    @Excel(name = "签名id")
    private String signId;

    /** 模板id */
    @Excel(name = "模板id")
    private String modelId;

    /** 模板标题 */
    @Excel(name = "模板标题")
    private String title;

    /** 变量标志：1-变量模板 2-普通模板 */
    @Excel(name = "变量标志：1-变量模板 2-普通模板")
    private Long variate;

    /** 状态：1-审核成功2-审核拒绝3-待审核4-草稿 */
    @Excel(name = "状态：1-审核成功2-审核拒绝3-待审核4-审核中")
    private Long status;

    /** 状态描述 */
    @Excel(name = "状态描述")
    private String info;

    /** 来源：1-API 2-管理端 3-客户端 */
    @Excel(name = "来源：1-API 2-管理端 3-客户端")
    private Long source;

    /** 推送地址 */
    private String backUrl;

    /** 操作员id */
    @Excel(name = "操作员id")
    private Long userId;


    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 应用名称 */
    @Excel(name = "应用id")
    private String appName;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;

    /** 通道侧模版ID */
    @Excel(name = "通道侧模版ID")
    private String channelModelId;

    public ModelInfo() {
    }

    public String getChannelModelId() {
        return channelModelId;
    }

    public void setChannelModelId(String channelModelId) {
        this.channelModelId = channelModelId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Long getAppExt() {
        return appExt;
    }

    public void setAppExt(Long appExt) {
        this.appExt = appExt;
    }

    public Long getModelExt() {
        return modelExt;
    }

    public void setModelExt(Long modelExt) {
        this.modelExt = modelExt;
    }

    public Integer getToCmcc() {
        return toCmcc;
    }

    public void setToCmcc(Integer toCmcc) {
        this.toCmcc = toCmcc;
    }

    public Integer getToUnicom() {
        return toUnicom;
    }

    public void setToUnicom(Integer toUnicom) {
        this.toUnicom = toUnicom;
    }

    public Integer getToTelecom() {
        return toTelecom;
    }

    public void setToTelecom(Integer toTelecom) {
        this.toTelecom = toTelecom;
    }

    public Integer getToInternational() {
        return toInternational;
    }

    public void setToInternational(Integer toInternational) {
        this.toInternational = toInternational;
    }

    public String getExtNum() {
        return extNum;
    }

    public void setExtNum(String extNum) {
        this.extNum = extNum;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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
    public void setSignId(String signId) 
    {
        this.signId = signId;
    }

    public String getSignId() 
    {
        return signId;
    }
    public void setModelId(String modelId) 
    {
        this.modelId = modelId;
    }

    public String getModelId() 
    {
        return modelId;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setVariate(Long variate) 
    {
        this.variate = variate;
    }

    public Long getVariate() 
    {
        return variate;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setInfo(String info) 
    {
        this.info = info;
    }

    public String getInfo() 
    {
        return info;
    }
    public void setSource(Long source) 
    {
        this.source = source;
    }

    public Long getSource() 
    {
        return source;
    }
    public void setBackUrl(String backUrl) 
    {
        this.backUrl = backUrl;
    }

    public String getBackUrl() 
    {
        return backUrl;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("companyId", getCompanyId())
            .append("appId", getAppId())
            .append("signId", getSignId())
            .append("modelId", getModelId())
            .append("title", getTitle())
            .append("variate", getVariate())
            .append("status", getStatus())
            .append("info", getInfo())
            .append("source", getSource())
            .append("backUrl", getBackUrl())
            .append("userId", getUserId())
            .append("updateTime", getUpdateTime())
            .append("createTime", getCreateTime())
            .append("modelExt", getModelExt())
            .append("appExt", getAppExt())
            .append("channelId", getChannelId())
            .append("pid", getPid())
            .toString();
    }
}
