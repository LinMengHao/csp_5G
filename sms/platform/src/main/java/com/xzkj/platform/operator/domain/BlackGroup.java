package com.xzkj.platform.operator.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 黑名单组对象 e_black_group
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
public class BlackGroup extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 分组名称 */
    @Excel(name = "分组名称")
    private String title;

    /** 黑名单渠道数 */
    @Excel(name = "黑名单渠道数")
    private Long channelTotal;

    /** 状态：1-启用2-停用 */
    @Excel(name = "状态：1-启用2-停用")
    private Long status;

    /** 号码重复校验周期 */
    @Excel(name = "验重周期")
    private Long repeatDay;

    /** 号码重复率 */
    @Excel(name = "重复率")
    private Long repeatCount;

    /** 黑名单触发梯度 */
    @Excel(name = "触黑梯度")
    private Long blackCount;

    /** 黑名单触发率 */
    @Excel(name = "触黑率")
    private Double blackRate;

    /** 黑名单渠道列表 */
    private List<BlackRelated> channelList;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setChannelTotal(Long channelTotal) 
    {
        this.channelTotal = channelTotal;
    }

    public Long getChannelTotal() 
    {
        return channelTotal;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public List<BlackRelated> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<BlackRelated> channelList) {
        this.channelList = channelList;
    }

    public Long getRepeatDay() {
        return repeatDay;
    }

    public void setRepeatDay(Long repeatDay) {
        this.repeatDay = repeatDay;
    }

    public Long getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Long repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Long getBlackCount() {
        return blackCount;
    }

    public void setBlackCount(Long blackCount) {
        this.blackCount = blackCount;
    }

    public Double getBlackRate() {
        return blackRate;
    }

    public void setBlackRate(Double blackRate) {
        this.blackRate = blackRate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("channelTotal", getChannelTotal())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("updateTime", getUpdateTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
