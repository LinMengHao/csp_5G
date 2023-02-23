package com.xzkj.platform.operator.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 模板关系e_model_related对象 e_model_related
 * 
 * @author ruoyi
 * @date 2022-10-12
 */
public class ModelRelated extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 模板id */
    @Excel(name = "模板id")
    private String modelId;

    /** 通道id */
    @Excel(name = "通道id")
    private Long channelId;

    /** 通道名称 */
    @Excel(name = "通道名称")
    private String channelName;

    /** 通道模板id */
    @Excel(name = "通道模板id")
    private String channelModelId;

    /** 通道模板日限 */
    @Excel(name = "通道模板日限")
    private Long limitCount;

    /** 状态：1-启用2-停用 */
    @Excel(name = "状态：1-启用2-停用")
    private Long status;

    /** 变量参数映射key1=value1&key2=value2&客户变量key=通道变量key */
    @Excel(name = "变量参数映射")
    private String paramExt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setModelId(String modelId) 
    {
        this.modelId = modelId;
    }

    public String getModelId() 
    {
        return modelId;
    }
    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }
    public void setChannelModelId(String channelModelId) 
    {
        this.channelModelId = channelModelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelModelId()
    {
        return channelModelId;
    }
    public void setLimitCount(Long limitCount) 
    {
        this.limitCount = limitCount;
    }

    public Long getLimitCount() 
    {
        return limitCount;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setParamExt(String paramExt) 
    {
        this.paramExt = paramExt;
    }

    public String getParamExt() 
    {
        return paramExt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("modelId", getModelId())
            .append("channelId", getChannelId())
            .append("channelModelId", getChannelModelId())
            .append("limitCount", getLimitCount())
            .append("status", getStatus())
            .append("paramExt", getParamExt())
            .append("updateTime", getUpdateTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
