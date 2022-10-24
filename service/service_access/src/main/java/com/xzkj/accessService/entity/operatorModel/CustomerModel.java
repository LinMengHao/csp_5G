package com.xzkj.accessService.entity.operatorModel;

import java.io.Serializable;

public class CustomerModel implements Serializable {
    private String applyTime;
    private String customerName;
    private String customerContactPerson;
    private String contactPersonPhone;
    private String contactPersonEmail;
    private String belongRegionCode;
    private String belongAgentName;
    private String belongAgentCode;
    private String industryTypeCode;
    private String customerUrl;
    private String remarkText;
    private String contractCode;
    private String contractName;
    private String contractValidDate;
    private String contractInvalidDate;
    private String isRenewed;
    private String contractRenewDate;
    private String contractUrl;
    private String messageId;
    private String eTag;
    private String auditPerson;
    private String auditOpinion;
    private String auditTime;
    private String unifySocialCreditCodes;
    private String enterpriseOwnerName;
    private String certificateType;
    private String certificateCode;

    private String customerNum;

    private String status;

    public CustomerModel() {
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
