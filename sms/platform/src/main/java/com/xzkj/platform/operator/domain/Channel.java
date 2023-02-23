package com.xzkj.platform.operator.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 通道信息对象 t_channel
 * 
 * @author admin
 * @date 2022-09-25
 */
public class Channel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /**  */
    @Excel(name = "")
    private String channelName;

    /**  */
    @Excel(name = "")
    private String status;

    /**  */
    @Excel(name = "")
    private String toCmcc;

    /**  */
    @Excel(name = "")
    private String toUnicom;

    /**  */
    @Excel(name = "")
    private String toTelecom;

    /**  */
    @Excel(name = "")
    private String svcAddr;

    /**  */
    @Excel(name = "")
    private Long svcPort;

    /**  */
    @Excel(name = "")
    private String account;

    /**  */
    @Excel(name = "")
    private String password;

    /**  */
    @Excel(name = "")
    private String serviceCode;

    /**  */
    @Excel(name = "")
    private String enterpriseCode;

    /**  */
    @Excel(name = "")
    private String variant;

    /**  */
    @Excel(name = "")
    private String sourceMent;

    /**  */
    @Excel(name = "")
    private String haveReport;

    /**  */
    @Excel(name = "")
    private String haveMo;

    /**  */
    @Excel(name = "")
    private String extras;

    /** 最大连接数 */
    @Excel(name = "最大连接数")
    private Long linkMax;

    /** 每连接最大发送速度(条/秒) */
    @Excel(name = "每连接最大发送速度(条/秒)")
    private Long linkSpeed;

    /** 通道单价 */
    @Excel(name = "通道单价")
    private Double channelPrice;

    /** 每个模块对应线程,存放形式:模块号:线程数,模块号:线程数 */
    @Excel(name = "每个模块对应线程,存放形式:模块号:线程数,模块号:线程数")
    private String model;

    /**  */
    @Excel(name = "")
    private String modelUpload;

    /**  */
    @Excel(name = "")
    private String token;

    /** 签名查询表 */
    @Excel(name = "签名查询表")
    private String signTable;

    /** D是直连通道，T是三方通道 */
    @Excel(name = "D是直连通道，T是三方通道")
    private String channelFlag;

    /**  */
    @Excel(name = "")
    private String modelAdress;

    /** push推送，pull拉取 */
    @Excel(name = "push推送，pull拉取")
    private String modelAccess;

    /** 模板状态拉取地址 */
    @Excel(name = "模板状态拉取地址")
    private String pullAdress;

    /** 模板报备action */
    @Excel(name = "模板报备action")
    private String modelAction;

    /** .控速：数字字段，表示该通道每秒最多可以提交多少条视频短信 */
    @Excel(name = ".控速：数字字段，表示该通道每秒最多可以提交多少条视频短信")
    private Long sendLimit;

    /** 数字字段，表示每条出现多少条超时，通道暂停 */
    @Excel(name = "数字字段，表示每条出现多少条超时，通道暂停")
    private String timeoutCount;

    /** 忽略省份 */
    @Excel(name = "忽略省份")
    private String ignoreProvince;

    /** 支持的运营商 */
    @Excel(name = "支持的运营商")
    private String toProvide;
    /** 回执/上行/状态 */
    @Excel(name = "回执/上行/状态")
    private String haveRMS;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setChannelName(String channelName) 
    {
        this.channelName = channelName;
    }

    public String getChannelName() 
    {
        return channelName;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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
    public void setSvcAddr(String svcAddr) 
    {
        this.svcAddr = svcAddr;
    }

    public String getSvcAddr() 
    {
        return svcAddr;
    }
    public void setSvcPort(Long svcPort) 
    {
        this.svcPort = svcPort;
    }

    public Long getSvcPort() 
    {
        return svcPort;
    }
    public void setAccount(String account) 
    {
        this.account = account;
    }

    public String getAccount() 
    {
        return account;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setServiceCode(String serviceCode) 
    {
        this.serviceCode = serviceCode;
    }

    public String getServiceCode() 
    {
        return serviceCode;
    }
    public void setEnterpriseCode(String enterpriseCode) 
    {
        this.enterpriseCode = enterpriseCode;
    }

    public String getEnterpriseCode() 
    {
        return enterpriseCode;
    }
    public void setVariant(String variant) 
    {
        this.variant = variant;
    }

    public String getVariant() 
    {
        return variant;
    }
    public void setSourceMent(String sourceMent) 
    {
        this.sourceMent = sourceMent;
    }

    public String getSourceMent() 
    {
        return sourceMent;
    }
    public void setHaveReport(String haveReport) 
    {
        this.haveReport = haveReport;
    }

    public String getHaveReport() 
    {
        return haveReport;
    }
    public void setHaveMo(String haveMo) 
    {
        this.haveMo = haveMo;
    }

    public String getHaveMo() 
    {
        return haveMo;
    }
    public void setExtras(String extras) 
    {
        this.extras = extras;
    }

    public String getExtras() 
    {
        return extras;
    }
    public void setLinkMax(Long linkMax) 
    {
        this.linkMax = linkMax;
    }

    public Long getLinkMax() 
    {
        return linkMax;
    }
    public void setLinkSpeed(Long linkSpeed) 
    {
        this.linkSpeed = linkSpeed;
    }

    public Long getLinkSpeed() 
    {
        return linkSpeed;
    }
    public void setChannelPrice(Double channelPrice)
    {
        this.channelPrice = channelPrice;
    }

    public Double getChannelPrice()
    {
        return channelPrice;
    }
    public void setModel(String model) 
    {
        this.model = model;
    }

    public String getModel() 
    {
        return model;
    }
    public void setModelUpload(String modelUpload) 
    {
        this.modelUpload = modelUpload;
    }

    public String getModelUpload() 
    {
        return modelUpload;
    }
    public void setToken(String token) 
    {
        this.token = token;
    }

    public String getToken() 
    {
        return token;
    }
    public void setSignTable(String signTable) 
    {
        this.signTable = signTable;
    }

    public String getSignTable() 
    {
        return signTable;
    }
    public void setChannelFlag(String channelFlag) 
    {
        this.channelFlag = channelFlag;
    }

    public String getChannelFlag() 
    {
        return channelFlag;
    }
    public void setModelAdress(String modelAdress) 
    {
        this.modelAdress = modelAdress;
    }

    public String getModelAdress() 
    {
        return modelAdress;
    }
    public void setModelAccess(String modelAccess) 
    {
        this.modelAccess = modelAccess;
    }

    public String getModelAccess() 
    {
        return modelAccess;
    }
    public void setPullAdress(String pullAdress) 
    {
        this.pullAdress = pullAdress;
    }

    public String getPullAdress() 
    {
        return pullAdress;
    }
    public void setModelAction(String modelAction) 
    {
        this.modelAction = modelAction;
    }

    public String getModelAction() 
    {
        return modelAction;
    }
    public void setSendLimit(Long sendLimit) 
    {
        this.sendLimit = sendLimit;
    }

    public Long getSendLimit() 
    {
        return sendLimit;
    }
    public void setTimeoutCount(String timeoutCount) 
    {
        this.timeoutCount = timeoutCount;
    }

    public String getTimeoutCount() 
    {
        return timeoutCount;
    }
    public void setIgnoreProvince(String ignoreProvince) 
    {
        this.ignoreProvince = ignoreProvince;
    }

    public String getIgnoreProvince() 
    {
        return ignoreProvince;
    }

    public String getToProvide() {
        return toProvide;
    }

    public void setToProvide(String toProvide) {
        this.toProvide = toProvide;
    }

    public String getHaveRMS() {
        return haveRMS;
    }

    public void setHaveRMS(String haveRMS) {
        this.haveRMS = haveRMS;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
           // .append("channelId", getChannelId())
            .append("channelName", getChannelName())
            .append("status", getStatus())
            .append("toCmcc", getToCmcc())
            .append("toUnicom", getToUnicom())
            .append("toTelecom", getToTelecom())
            .append("svcAddr", getSvcAddr())
            .append("svcPort", getSvcPort())
            .append("account", getAccount())
            .append("password", getPassword())
            .append("serviceCode", getServiceCode())
            .append("enterpriseCode", getEnterpriseCode())
            .append("variant", getVariant())
            .append("sourceMent", getSourceMent())
            .append("haveReport", getHaveReport())
            .append("haveMo", getHaveMo())
            .append("extras", getExtras())
            .append("linkMax", getLinkMax())
            .append("linkSpeed", getLinkSpeed())
            .append("channelPrice", getChannelPrice())
            .append("model", getModel())
            .append("modelUpload", getModelUpload())
            .append("token", getToken())
            .append("signTable", getSignTable())
            .append("channelFlag", getChannelFlag())
            .append("modelAdress", getModelAdress())
            .append("modelAccess", getModelAccess())
            .append("pullAdress", getPullAdress())
            .append("modelAction", getModelAction())
            .append("sendLimit", getSendLimit())
            .append("timeoutCount", getTimeoutCount())
            .append("ignoreProvince", getIgnoreProvince())
            .toString();
    }
}
