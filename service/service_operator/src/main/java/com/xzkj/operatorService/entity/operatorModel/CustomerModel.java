package com.xzkj.operatorService.entity.operatorModel;

import java.io.Serializable;

public class CustomerModel implements Serializable {
    /**
     * 申请时间YYYY-MM-DD
     */
    private String applyTime;
    /**
     * 客户名称。同一个代理商下客户名称+归属区域唯一
     */
    private String customerName;
    /**
     * 客户联系人
     */
    private String customerContactPerson;
    /**
     * 联系人手机
     */
    private String contactPersonPhone;
    /**
     * 联系人邮箱
     */
    private String contactPersonEmail;
    /**
     * 归属区域编码，大区，省分，城市用逗号间隔
     * 省份必须与代理商的省份一致
     */
    private String belongRegionCode;
    /**
     * 代理商名称
     */
    private String belongAgentName;
    /**
     * 代理商编码
     */
    private String belongAgentCode;
    /**
     * 行业类型编码。参见附录4.3行业编码的二级行业编码
     */
    private String industryTypeCode;
    /**
     * 附件url通过地址返回实体附件
     * (支持的文件类型：
     * pdf,doc,docx,xls,xlsx,ppt,pptx,
     * jpg,jpeg,gif,rar,7z,zip 大小为10M）
     */
    private String customerUrl;
    /**
     * 备注
     */
    private String remarkText;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 合同名称
     */
    private String contractName;
    /**
     * 合同生效日期YYYY-MM-DD
     */
    private String contractValidDate;
    /**
     * 合同失效日期YYYY-MM-DD
     */
    private String contractInvalidDate;
    /**
     * 合同是否自动续签：1：是、0：否
     */
    private String isRenewed;
    /**
     * 合同续签日期YYYY-MM-DD
     */
    private String contractRenewDate;
    /**
     * 合同电子扫描件url通过地址返回实体附件
     * （附件类型支持：pdf、doc、docx、jpg、jpeg、gif、rar；大小限10M
     */
    private String contractUrl;
    /**
     * 操作流水号
     */
    private String messageId;
    /**
     * 版本号
     */
    private String eTag;
    /**
     * 审核人员
     */
    private String auditPerson;
    /**
     * 审核意见
     */
    private String auditOpinion;
    /**
     * 审核时间YYYY-MM-DD
     */
    private String auditTime;
    /**
     * 统一社会信用代码
     */
    private String unifySocialCreditCodes;
    /**
     * 企业责任人姓名
     */
    private String enterpriseOwnerName;
    /**
     * 企业责任人证件类型
     * 证件类型：01-居民身份证、02-中国人民解放军军人身份证件、03-中国人民武装警察身份证件
     */
    private String certificateType;
    /**
     * 企业责任人证件号码
     */
    private String certificateCode;
    /**
     * 客户编码
     */
    private String customerNum;

    private String status;
    /**
     * 服务代码
     * 必须归属非直签客户关联的代理商
     */
    private String ServiceCode;
    /**
     * 扩展码（服务代码+扩展码不超过20位）
     */
    private String extCode;
    /**
     * 操作类型
     * 1-分配 2-收回
     */
    private String type;

    public CustomerModel() {
    }

    public String getServiceCode() {
        return ServiceCode;
    }

    public void setServiceCode(String serviceCode) {
        ServiceCode = serviceCode;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContactPerson() {
        return customerContactPerson;
    }

    public void setCustomerContactPerson(String customerContactPerson) {
        this.customerContactPerson = customerContactPerson;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getBelongRegionCode() {
        return belongRegionCode;
    }

    public void setBelongRegionCode(String belongRegionCode) {
        this.belongRegionCode = belongRegionCode;
    }

    public String getBelongAgentName() {
        return belongAgentName;
    }

    public void setBelongAgentName(String belongAgentName) {
        this.belongAgentName = belongAgentName;
    }

    public String getBelongAgentCode() {
        return belongAgentCode;
    }

    public void setBelongAgentCode(String belongAgentCode) {
        this.belongAgentCode = belongAgentCode;
    }

    public String getIndustryTypeCode() {
        return industryTypeCode;
    }

    public void setIndustryTypeCode(String industryTypeCode) {
        this.industryTypeCode = industryTypeCode;
    }

    public String getCustomerUrl() {
        return customerUrl;
    }

    public void setCustomerUrl(String customerUrl) {
        this.customerUrl = customerUrl;
    }

    public String getRemarkText() {
        return remarkText;
    }

    public void setRemarkText(String remarkText) {
        this.remarkText = remarkText;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractValidDate() {
        return contractValidDate;
    }

    public void setContractValidDate(String contractValidDate) {
        this.contractValidDate = contractValidDate;
    }

    public String getContractInvalidDate() {
        return contractInvalidDate;
    }

    public void setContractInvalidDate(String contractInvalidDate) {
        this.contractInvalidDate = contractInvalidDate;
    }

    public String getIsRenewed() {
        return isRenewed;
    }

    public void setIsRenewed(String isRenewed) {
        this.isRenewed = isRenewed;
    }

    public String getContractRenewDate() {
        return contractRenewDate;
    }

    public void setContractRenewDate(String contractRenewDate) {
        this.contractRenewDate = contractRenewDate;
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
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

    public String getUnifySocialCreditCodes() {
        return unifySocialCreditCodes;
    }

    public void setUnifySocialCreditCodes(String unifySocialCreditCodes) {
        this.unifySocialCreditCodes = unifySocialCreditCodes;
    }

    public String getEnterpriseOwnerName() {
        return enterpriseOwnerName;
    }

    public void setEnterpriseOwnerName(String enterpriseOwnerName) {
        this.enterpriseOwnerName = enterpriseOwnerName;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getCertificateCode() {
        return certificateCode;
    }

    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }

    public String getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(String customerNum) {
        this.customerNum = customerNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CustomerModel{" +
                "applyTime='" + applyTime + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerContactPerson='" + customerContactPerson + '\'' +
                ", contactPersonPhone='" + contactPersonPhone + '\'' +
                ", contactPersonEmail='" + contactPersonEmail + '\'' +
                ", belongRegionCode='" + belongRegionCode + '\'' +
                ", belongAgentName='" + belongAgentName + '\'' +
                ", belongAgentCode='" + belongAgentCode + '\'' +
                ", industryTypeCode='" + industryTypeCode + '\'' +
                ", customerUrl='" + customerUrl + '\'' +
                ", remarkText='" + remarkText + '\'' +
                ", contractCode='" + contractCode + '\'' +
                ", contractName='" + contractName + '\'' +
                ", contractValidDate='" + contractValidDate + '\'' +
                ", contractInvalidDate='" + contractInvalidDate + '\'' +
                ", isRenewed='" + isRenewed + '\'' +
                ", contractRenewDate='" + contractRenewDate + '\'' +
                ", contractUrl='" + contractUrl + '\'' +
                ", messageId='" + messageId + '\'' +
                ", eTag='" + eTag + '\'' +
                ", auditPerson='" + auditPerson + '\'' +
                ", auditOpinion='" + auditOpinion + '\'' +
                ", auditTime='" + auditTime + '\'' +
                ", unifySocialCreditCodes='" + unifySocialCreditCodes + '\'' +
                ", enterpriseOwnerName='" + enterpriseOwnerName + '\'' +
                ", certificateType='" + certificateType + '\'' +
                ", certificateCode='" + certificateCode + '\'' +
                ", customerNum='" + customerNum + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
