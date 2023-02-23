package com.xzkj.platform.operator.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 客户充值记录对象 e_recharge_customer
 * 
 * @author ruoyi
 * @date 2022-10-24
 */
public class RechargeCustomer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 公司id */
    @Excel(name = "公司id")
    private Long companyId;

    /** 账号id */
    @Excel(name = "账号id")
    private Long appId;

    /** 销售id */
    @Excel(name = "销售id")
    private Long salesId;

    /** 充值类型1-充值2-失败回补3-核减4-退款5-扣罚 */
    @Excel(name = "充值类型1-充值2-失败回补3-核减4-退款5-扣罚")
    private String changeType;

    /** 单价，单位分 */
    @Excel(name = "单价，单位分")
    private BigDecimal price;

    /** 充值条数 */
    @Excel(name = "充值条数")
    private Long changeNum;

    /** 交易方式1-对公账户2-银行转账3-支付宝4-微信5-现金 */
    @Excel(name = "充值类型1-充值2-失败回补3-核减4-退款5-扣罚")
    private String changeWay;

    /** 充值前余额 */
    @Excel(name = "余额")
    private Long balance;

    /** 操作员id */
    @Excel(name = "操作员id")
    private Long userId;
    /** 账号 */
    @Excel(name = "账号")
    private String appName;
    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;
    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;


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
    public void setSalesId(Long salesId) 
    {
        this.salesId = salesId;
    }

    public Long getSalesId() 
    {
        return salesId;
    }
    public void setChangeType(String changeType) 
    {
        this.changeType = changeType;
    }

    public String getChangeType() 
    {
        return changeType;
    }
    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }
    public void setChangeNum(Long changeNum) 
    {
        this.changeNum = changeNum;
    }

    public Long getChangeNum() 
    {
        return changeNum;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public String getChangeWay() {
        return changeWay;
    }

    public void setChangeWay(String changeWay) {
        this.changeWay = changeWay;
    }

    public Long getBalance() {
        return balance;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getUserId()
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("companyId", getCompanyId())
            .append("appId", getAppId())
            .append("salesId", getSalesId())
            .append("changeType", getChangeType())
            .append("price", getPrice())
            .append("changeNum", getChangeNum())
            .append("userId", getUserId())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .toString();
    }
}
