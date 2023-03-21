package com.xzkj.platform.servicecode.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;

/**
 * 服务码号管理对象 e_code_related
 * 
 * @author linmenghao
 * @date 2023-03-16
 */
public class ECodeRelated extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 服务码号 */
    @Excel(name = "服务码号")
    private String serviceCode;

    /** 真实接入码 */
    @Excel(name = "真实接入码")
    private String accessExt;

    /** 虚拟接入码 */
    @Excel(name = "虚拟接入码")
    private String accessVirtualExt;

    /** 账号扩展码 */
    @Excel(name = "账号扩展码")
    private String appExt;

    /** 模板扩展码 */
    @Excel(name = "模板扩展码")
    private String modelExt;

    /** 账号id */
    @Excel(name = "账号id")
    private Long appId;

    /** 账号名称 */
    @Excel(name = "账号名称")
    private String appName;

    /** 公司id */
    @Excel(name = "公司id")
    private Long companyId;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 签名id */
    @Excel(name = "签名id")
    private String signId;

    /** 模板id */
    @Excel(name = "模板id")
    private String modelId;

    /** 运营商 1.移动  2.电信 3.联通 4.其他 */
    @Excel(name = "运营商 1.移动  2.电信 3.联通 4.其他")
    private Long ispCode;

    /** 通道id */
    @Excel(name = "通道id")
    private Long channelId;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    /** 备注 */
    @Excel(name = "备注")
    private String info;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setServiceCode(String serviceCode) 
    {
        this.serviceCode = serviceCode;
    }

    public String getServiceCode() 
    {
        return serviceCode;
    }
    public void setAccessExt(String accessExt) 
    {
        this.accessExt = accessExt;
    }

    public String getAccessExt() 
    {
        return accessExt;
    }
    public void setAccessVirtualExt(String accessVirtualExt) 
    {
        this.accessVirtualExt = accessVirtualExt;
    }

    public String getAccessVirtualExt() 
    {
        return accessVirtualExt;
    }
    public void setAppExt(String appExt) 
    {
        this.appExt = appExt;
    }

    public String getAppExt() 
    {
        return appExt;
    }
    public void setModelExt(String modelExt) 
    {
        this.modelExt = modelExt;
    }

    public String getModelExt() 
    {
        return modelExt;
    }
    public void setAppId(Long appId) 
    {
        this.appId = appId;
    }

    public Long getAppId() 
    {
        return appId;
    }
    public void setAppName(String appName) 
    {
        this.appName = appName;
    }

    public String getAppName() 
    {
        return appName;
    }
    public void setCompanyId(Long companyId) 
    {
        this.companyId = companyId;
    }

    public Long getCompanyId() 
    {
        return companyId;
    }
    public void setCompanyName(String companyName) 
    {
        this.companyName = companyName;
    }

    public String getCompanyName() 
    {
        return companyName;
    }
    public void setSignId(String signId) 
    {
        this.signId = signId;
    }

    public String getSignId() 
    {
        return signId;
    }
    public void setModelId(String modelId) 
    {
        this.modelId = modelId;
    }

    public String getModelId() 
    {
        return modelId;
    }
    public void setIspCode(Long ispCode) 
    {
        this.ispCode = ispCode;
    }

    public Long getIspCode() 
    {
        return ispCode;
    }
    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
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
            .append("serviceCode", getServiceCode())
            .append("accessExt", getAccessExt())
            .append("accessVirtualExt", getAccessVirtualExt())
            .append("appExt", getAppExt())
            .append("modelExt", getModelExt())
            .append("appId", getAppId())
            .append("appName", getAppName())
            .append("companyId", getCompanyId())
            .append("companyName", getCompanyName())
            .append("signId", getSignId())
            .append("modelId", getModelId())
            .append("ispCode", getIspCode())
            .append("channelId", getChannelId())
            .append("status", getStatus())
            .append("info", getInfo())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
