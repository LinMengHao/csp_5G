package com.xzkj.operatorService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TCustomer implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * uuid
     */
    private String id;

    /**
     * 申请时间YYYY-MM-DD
     */
    @TableField("applyTime")
    private String applyTime;

    /**
     * 客户名称。同一个代理商下客户名称+归属区域唯一
     */
    @TableField("customerName")
    private String customerName;

    /**
     * 客户联系人
     */
    @TableField("customerContactPerson")
    private String customerContactPerson;

    /**
     * 联系人手机
     */
    @TableField("contactPersonPhone")
    private String contactPersonPhone;

    /**
     * 联系人邮箱
     */
    @TableField("contactPersonEmail")
    private String contactPersonEmail;

    /**
     * 归属区域编码，大区，省分，城市用逗号间隔
省份必须与代理商的省份一致
     */
    @TableField("belongRegionCode")
    private String belongRegionCode;

    /**
     * 代理商名称
     */
    @TableField("belongAgentName")
    private String belongAgentName;

    /**
     * 代理商编码
     */
    @TableField("belongAgentCode")
    private String belongAgentCode;

    /**
     * 行业类型编码。参见附录4.3行业编码的二级行业编码
     */
    @TableField("industryTypeCode")
    private String industryTypeCode;

    /**
     * 附件url通过地址返回实体附件
(支持的文件类型：
pdf,doc,docx,xls,xlsx,ppt,pptx,
jpg,jpeg,gif,rar,7z,zip 大小为10M）
     */
    @TableField("customerUrl")
    private String customerUrl;

    /**
     * 备注
     */
    @TableField("remarkText")
    private String remarkText;

    /**
     * 合同编号
     */
    @TableField("contractCode")
    private String contractCode;

    /**
     * 合同名称
     */
    @TableField("contractName")
    private String contractName;

    /**
     * 合同生效日期YYYY-MM-DD
     */
    @TableField("contractValidDate")
    private String contractValidDate;

    /**
     * 合同失效日期YYYY-MM-DD
     */
    @TableField("contractInvalidDate")
    private String contractInvalidDate;

    /**
     * 合同是否自动续签：1：是、0：否
     */
    @TableField("isRenewed")
    private String isRenewed;

    /**
     * 合同续签日期YYYY-MM-DD
     */
    @TableField("contractRenewDate")
    private String contractRenewDate;

    /**
     * 合同电子扫描件url通过地址返回实体附件
（附件类型支持：pdf、doc、docx、jpg、jpeg、gif、rar；大小限10M）
     */
    @TableField("contractUrl")
    private String contractUrl;

    /**
     * 操作流水号
     */
    @TableField("messageId")
    private String messageId;

    /**
     * 版本号
     */
    @TableField("eTag")
    private String eTag;

    /**
     * 审核人员
     */
    @TableField("auditPerson")
    private String auditPerson;

    /**
     * 审核意见
     */
    @TableField("auditOpinion")
    private String auditOpinion;

    /**
     * 审核时间YYYY-MM-DD
     */
    @TableField("auditTime")
    private String auditTime;

    /**
     * 统一社会信用代码
     */
    @TableField("unifySocialCreditCodes")
    private String unifySocialCreditCodes;

    /**
     * 企业责任人姓名
     */
    @TableField("enterpriseOwnerName")
    private String enterpriseOwnerName;

    /**
     * 企业责任人证件类型
证件类型：01-居民身份证、02-中国人民解放军军人身份证件、03-中国人民武装警察身份证件
     */
    @TableField("certificateType")
    private String certificateType;

    /**
     * 企业责任人证件号码
     */
    @TableField("certificateCode")
    private String certificateCode;


    /**
     * 状态：
11-新增审核不通过
12-变更审核不通过
20-新增审核中
22-待管理平台新增审核
23-待管理平台变更审核
     30-正常
     40-暂停

     */
    private String status;

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;
}
