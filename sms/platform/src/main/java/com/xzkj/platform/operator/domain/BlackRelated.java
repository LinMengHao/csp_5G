package com.xzkj.platform.operator.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 黑名单组关系对象 e_black_related
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
public class BlackRelated extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 分组id */
    @Excel(name = "分组id")
    private Long groupId;

    /** 黑名单渠道 local-自有黑名单，JS-江苏黑名单，NJ-浙江黑名单，DYD-东云短信黑名单，DYV-东云语音黑名单 */
    @Excel(name = "黑名单渠道 local-自有黑名单，JS-江苏黑名单，NJ-浙江黑名单，DYD-东云短信黑名单，DYV-东云语音黑名单")
    private String channel;

    /** 高危1-选中2-未选中 */
    @Excel(name = "高危1-选中2-未选中")
    private Long riskHigh;

    /** 中危1-选中2-未选中 */
    @Excel(name = "中危1-选中2-未选中")
    private Long riskMedium;

    /** 低危1-选中2-未选中 */
    @Excel(name = "低危1-选中2-未选中")
    private Long riskLow;

    /** 私有库1-选中2-未选中 */
    @Excel(name = "私有库1-选中2-未选中")
    private Long riskPrivate;

    /** 优先级 */
    @Excel(name = "优先级")
    private Long priority;

    /** 过滤比例 */
    @Excel(name = "过滤比例")
    private BigDecimal filterRate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setGroupId(Long groupId) 
    {
        this.groupId = groupId;
    }

    public Long getGroupId() 
    {
        return groupId;
    }
    public void setChannel(String channel) 
    {
        this.channel = channel;
    }

    public String getChannel() 
    {
        return channel;
    }
    public void setRiskHigh(Long riskHigh) 
    {
        this.riskHigh = riskHigh;
    }

    public Long getRiskHigh() 
    {
        return riskHigh;
    }
    public void setRiskMedium(Long riskMedium) 
    {
        this.riskMedium = riskMedium;
    }

    public Long getRiskMedium() 
    {
        return riskMedium;
    }
    public void setRiskLow(Long riskLow) 
    {
        this.riskLow = riskLow;
    }

    public Long getRiskLow() 
    {
        return riskLow;
    }
    public void setRiskPrivate(Long riskPrivate) 
    {
        this.riskPrivate = riskPrivate;
    }

    public Long getRiskPrivate() 
    {
        return riskPrivate;
    }
    public void setPriority(Long priority) 
    {
        this.priority = priority;
    }

    public Long getPriority() 
    {
        return priority;
    }
    public void setFilterRate(BigDecimal filterRate) 
    {
        this.filterRate = filterRate;
    }

    public BigDecimal getFilterRate() 
    {
        return filterRate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("groupId", getGroupId())
            .append("channel", getChannel())
            .append("riskHigh", getRiskHigh())
            .append("riskMedium", getRiskMedium())
            .append("riskLow", getRiskLow())
            .append("riskPrivate", getRiskPrivate())
            .append("priority", getPriority())
            .append("filterRate", getFilterRate())
            .append("remark", getRemark())
            .append("updateTime", getUpdateTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
