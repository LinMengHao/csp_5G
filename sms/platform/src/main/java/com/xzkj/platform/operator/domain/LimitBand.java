package com.xzkj.platform.operator.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 频次限制对象 e_limit_band
 * 
 * @author ruoyi
 * @date 2022-12-04
 */
public class LimitBand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 限制类型 1-客户全局 2-通道全局 3-客户账号 4-通道账号 */
    @Excel(name = "限制类型 1-客户全局 2-通道全局 3-客户账号 4-通道账号")
    private Integer limitType;

    /** 公司id，0-未知 */
    @Excel(name = "公司id，0-未知")
    private Long companyId;

    /** 账号id */
    @Excel(name = "账号id")
    private Long appId;

    /** 运营商id，0-未知 */
    @Excel(name = "运营商id，0-未知")
    private Long spId;

    /** 通道id */
    @Excel(name = "通道id")
    private Long channelId;

    /** 天数 */
    @Excel(name = "天数")
    private Long days;

    /** 次数 */
    @Excel(name = "次数")
    private Long times;

    /** 操作员id */
    @Excel(name = "操作员id")
    private Long userId;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;
    /** 应用名称 */
    @Excel(name = "应用id")
    private String appName;
    /** 账号名称 */
    @Excel(name = "账号名称")
    private String accountName;

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
    public void setLimitType(Integer limitType)
    {
        this.limitType = limitType;
    }

    public Integer getLimitType()
    {
        return limitType;
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
    public void setSpId(Long spId) 
    {
        this.spId = spId;
    }

    public Long getSpId() 
    {
        return spId;
    }
    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }
    public void setDays(Long days) 
    {
        this.days = days;
    }

    public Long getDays() 
    {
        return days;
    }
    public void setTimes(Long times) 
    {
        this.times = times;
    }

    public Long getTimes() 
    {
        return times;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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
            .append("limitType", getLimitType())
            .append("companyId", getCompanyId())
            .append("appId", getAppId())
            .append("spId", getSpId())
            .append("channelId", getChannelId())
            .append("days", getDays())
            .append("times", getTimes())
            .append("remark", getRemark())
            .append("userId", getUserId())
            .append("updateTime", getUpdateTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
