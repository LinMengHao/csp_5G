package com.xzkj.platform.sign.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * 签名映射关系对象 e_sign_related
 * 
 * @author linmenghao
 * @date 2023-02-06
 */
public class ESignRelated extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id主键 */
    private Long id;

    /** 本地签名ID，也就是父签名id */
    @Excel(name = "本地签名ID，也就是父签名id")
    private String signId;

    /** 通道id */
    @Excel(name = "通道id")
    private Long channelId;

    /** 通道侧签名id */
    @Excel(name = "通道侧签名id")
    private String channelSignId;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    /**  */
    @Excel(name = "描述")
    private String info;

    @Excel(name = "通道名称")
    private String channelName;


    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSignId(String signId) 
    {
        this.signId = signId;
    }

    public String getSignId() 
    {
        return signId;
    }
    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }
    public void setChannelSignId(String channelSignId) 
    {
        this.channelSignId = channelSignId;
    }

    public String getChannelSignId() 
    {
        return channelSignId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("signId", getSignId())
            .append("channelId", getChannelId())
            .append("channelSignId", getChannelSignId())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("info", getInfo())
            .toString();
    }
}
