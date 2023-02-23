package com.xzkj.platform.sign.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *  签名实体类
 */
public class ModelSign extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Excel(name="签名id")
    private String signId;


    @Excel(name = "公司id")
    private Long companyId;

    @Excel(name = "操作员id")
    private Long userId;

    @Excel(name = "用户名称")
    private String userName;

    @Excel(name = "公司名称")
    private String companyName;
    /** 账号id */
    @Excel(name = "账号id")
    private Long appId;

    @Excel(name = "账号名称")
    private String appName;

    @Excel(name = "省市")
    private String ecProvince;

    @Excel(name = "地市")
    private String ecCity;

    @Excel(name = "服务代码(扩展后的码号)")
    private String serviceCode;

    @Excel(name = "报备签名内容")
    private String reportSignContent;

    @Excel(name = "实际发送企业")
    private String ecName;

    @Excel(name = "信安行业属性")
    private String rcsIndustry;

    @Excel(name = "行业分类")
    private String industry;

    @Excel(name = "客户分类")
    private Integer customerType;

    @Excel(name = "操作类型")
    private Integer operatorType;

    @Excel(name = "文件路径")
    private String upLoadFile;

    @Excel(name = "备注")
    private String info;

    @Excel(name = "状态")
    private String status;

    private String backUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getSignId() {
        return signId;
    }

    public void setSignId(String signId) {
        this.signId = signId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public Long getCompanyId() {
        return companyId;
    }

    @Override
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEcProvince() {
        return ecProvince;
    }

    public void setEcProvince(String ecProvince) {
        this.ecProvince = ecProvince;
    }

    public String getEcCity() {
        return ecCity;
    }

    public void setEcCity(String ecCity) {
        this.ecCity = ecCity;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getReportSignContent() {
        return reportSignContent;
    }

    public void setReportSignContent(String reportSignContent) {
        this.reportSignContent = reportSignContent;
    }

    public String getEcName() {
        return ecName;
    }

    public void setEcName(String ecName) {
        this.ecName = ecName;
    }

    public String getRcsIndustry() {
        return rcsIndustry;
    }

    public void setRcsIndustry(String rcsIndustry) {
        this.rcsIndustry = rcsIndustry;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Integer getCustomerType() {
        return customerType;
    }

    public void setCustomerType(Integer customerType) {
        this.customerType = customerType;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public String getUpLoadFile() {
        return upLoadFile;
    }

    public void setUpLoadFile(String upLoadFile) {
        this.upLoadFile = upLoadFile;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("signId", signId)
                .append("companyId", companyId)
                .append("userId", userId)
                .append("userName", userName)
                .append("companyName", companyName)
                .append("appId", appId)
                .append("appName", appName)
                .append("ecProvince", ecProvince)
                .append("ecCity", ecCity)
                .append("serviceCode", serviceCode)
                .append("reportSignContent", reportSignContent)
                .append("ecName", ecName)
                .append("rcsIndustry", rcsIndustry)
                .append("industry", industry)
                .append("customerType", customerType)
                .append("operatorType", operatorType)
                .append("upLoadFile", upLoadFile)
                .append("backUrl", backUrl)
                .toString();
    }
}
