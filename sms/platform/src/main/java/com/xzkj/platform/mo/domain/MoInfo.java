package com.xzkj.platform.mo.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;

/**
 * 上行对象 mms_mo_demo
 * 
 * @author lmh
 * @date 2023-03-09
 */
public class MoInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private List<String> timeList=new ArrayList<>();
    /** id */
    private Long id;

    /** 上行手机号 */
    @Excel(name = "上行手机号")
    private String mobile;

    /** 服务码号 */
    @Excel(name = "服务码号")
    private String serviceCode;

    /** 上行内容 */
    @Excel(name = "上行内容")
    private String content;

    /** 通道id */
    @Excel(name = "通道id")
    private Long channelId;

    /** 上行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上行时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;

    /** 账号id */
    @Excel(name = "账号id")
    private Long appId;

    /** 公司id */
    @Excel(name = "公司id")
    private Long companyId;

    /** 运营商 1 移动 2 电信 3 联通 */
    @Excel(name = "运营商 1 移动 2 电信 3 联通")
    private Long ispCode;

    /** 下发时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下发时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    /** 1 回调成功 2 待回调 */
    @Excel(name = "1 回调成功 2 待回调")
    private Long status;

    private String moId;

    private String companyName;
    private String appName;

    private String logDate;


    private String startTime ;


    private String endTime;

    public List<String> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<String> timeList) {
        this.timeList = timeList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLogDate() {
        return logDate;
    }


    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getMoId() {
        return moId;
    }

    public void setMoId(String moId) {
        this.moId = moId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getMobile() 
    {
        return mobile;
    }
    public void setServiceCode(String serviceCode) 
    {
        this.serviceCode = serviceCode;
    }

    public String getServiceCode() 
    {
        return serviceCode;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }
    public void setReceiveTime(Date receiveTime) 
    {
        this.receiveTime = receiveTime;
    }

    public Date getReceiveTime() 
    {

        return receiveTime;
    }
    public void setAppId(Long appId) 
    {
        this.appId = appId;
    }

    public Long getAppId() 
    {
        return appId;
    }

    @Override
    public Long getCompanyId() {
        return this.companyId;
    }

    @Override
    public void setCompanyId(Long companyId) {
        this.companyId=companyId;
    }

    public void setIspCode(Long ispCode)
    {
        this.ispCode = ispCode;
    }

    public Long getIspCode() 
    {
        return ispCode;
    }
    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("mobile", getMobile())
            .append("serviceCode", getServiceCode())
            .append("content", getContent())
            .append("channelId", getChannelId())
            .append("receiveTime", getReceiveTime())
            .append("appId", getAppId())
            .append("companyId", getCompanyId())
            .append("ispCode", getIspCode())
            .append("sendTime", getSendTime())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
