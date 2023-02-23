package com.xzkj.platform.operator.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import com.xzkj.platform.common.core.domain.entity.SysDept;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 路由信息e_route_base对象 e_route_base
 * 
 * @author ruoyi
 * @date 2022-09-18
 */
public class RouteBase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 公司id */
    @Excel(name = "公司id")
    private Long companyId;

    /** 应用id */
    @Excel(name = "应用id")
    private Long appId;

    /** 是否支持移动 */
    @Excel(name = "是否支持移动")
    private String toCmcc;

    /** 是否支持联通 */
    @Excel(name = "是否支持联通")
    private String toUnicom;

    /** 是否支持电信 */
    @Excel(name = "是否支持电信")
    private String toTelecom;

    /** 是否支持国际 */
    @Excel(name = "是否支持国际")
    private String toInternational;

    /** 签名，多个以半角逗号分隔 */
    @Excel(name = "签名，多个以半角逗号分隔")
    private String signName;

    /** 省份，多个以半角逗号分隔-预留字段 */
    @Excel(name = "省份，多个以半角逗号分隔-预留字段")
    private String province;

    /** 优先级 */
    @Excel(name = "优先级")
    private Long priority;

    /** 分发比例 */
    @Excel(name = "分发比例")
    private BigDecimal disprate;

    /** 通道id */
    @Excel(name = "通道id")
    private Long channelId;

    /** 通道日限，0表示不限制 */
    @Excel(name = "通道日限，0表示不限制")
    private Long channelLimit;

    /** 通道接入码 */
    @Excel(name = "通道接入码")
    private String channelCaller;

    /** 状态：1-启用2-停用 */
    @Excel(name = "状态：1-启用2-停用")
    private Long status;

    /** 描述 */
    @Excel(name = "描述")
    private String info;

    /** 支持的运营商 */
    @Excel(name = "支持的运营商")
    private String toProvide;

    /** 通道名称 */
    @Excel(name = "通道名称")
    private String channelName;
    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 应用名称 */
    @Excel(name = "应用id")
    private String appName;

    private SysDept dept;

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
    public void setToCmcc(String toCmcc) 
    {
        this.toCmcc = toCmcc;
    }

    public String getToCmcc() 
    {
        return toCmcc;
    }
    public void setToUnicom(String toUnicom) 
    {
        this.toUnicom = toUnicom;
    }

    public String getToUnicom() 
    {
        return toUnicom;
    }
    public void setToTelecom(String toTelecom) 
    {
        this.toTelecom = toTelecom;
    }

    public String getToTelecom() 
    {
        return toTelecom;
    }
    public void setToInternational(String toInternational) 
    {
        this.toInternational = toInternational;
    }

    public String getToInternational() 
    {
        return toInternational;
    }
    public void setSignName(String signName) 
    {
        this.signName = signName;
    }

    public String getSignName() 
    {
        return signName;
    }
    public void setProvince(String province) 
    {
        this.province = province;
    }

    public String getProvince() 
    {
        return province;
    }
    public void setPriority(Long priority) 
    {
        this.priority = priority;
    }

    public Long getPriority() 
    {
        return priority;
    }
    public void setDisprate(BigDecimal disprate) 
    {
        this.disprate = disprate;
    }

    public BigDecimal getDisprate() 
    {
        return disprate;
    }
    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }
    public void setChannelLimit(Long channelLimit) 
    {
        this.channelLimit = channelLimit;
    }

    public Long getChannelLimit() 
    {
        return channelLimit;
    }
    public void setChannelCaller(String channelCaller) 
    {
        this.channelCaller = channelCaller;
    }

    public String getChannelCaller() 
    {
        return channelCaller;
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

    public String getToProvide() {
        return toProvide;
    }

    public void setToProvide(String toProvide) {
        this.toProvide = toProvide;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
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
    public SysDept getDept()
    {
        return dept;
    }

    public void setDept(SysDept dept)
    {
        this.dept = dept;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("companyId", getCompanyId())
            .append("appId", getAppId())
            .append("toCmcc", getToCmcc())
            .append("toUnicom", getToUnicom())
            .append("toTelecom", getToTelecom())
            .append("toInternational", getToInternational())
            .append("signName", getSignName())
            .append("province", getProvince())
            .append("priority", getPriority())
            .append("disprate", getDisprate())
            .append("channelId", getChannelId())
            .append("channelLimit", getChannelLimit())
            .append("channelCaller", getChannelCaller())
            .append("status", getStatus())
            .append("info", getInfo())
            .append("updateTime", getUpdateTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
