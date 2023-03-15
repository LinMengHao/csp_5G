package com.xzkj.platform.khd.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.xzkj.platform.common.annotation.Excel;

import com.xzkj.platform.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 下行日志的显示查询对象 mms_sender
 * 
 * @author lixl
 * @date 2022-01-24
 */
@Data

public class MmsSender extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private List<String> timeList=new ArrayList<>();
    /** 下发通道名称 */
    private String channelName;

    /** 回调客户状态 */
    private String callback;

    /** 批量提交id */
    @Excel(name = "批量提交id")
    private String batchId;

    /**  */
    private String linkId;

    /** 彩信主题 */
    @Excel(name = "彩信主题")
    private String subject;
    /**  */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /**  */
    @Excel(name = "回执状态")
    private String status;
    /**  */
    @Excel(name = "账户id")
    private Long appId;
    /**  */
    @Excel(name = "短信签名")
    private String signName;
    /**  */
    @Excel(name = "状态描述")
    private String info;
    /**  */
    @Excel(name = "消息id")
    private String messageId;

    /**  */
    @Excel(name = "发送号码")
    private String destNumber;

    /**  */
//    @JsonFormat(pattern = "yyyy-MM-dd Hh")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "回执时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reportTime;

    /** 附件保存路径 */
    @Excel(name = "附件保存路径")
    private String savePath;

    /** 服务号 */
    @Excel(name = "服务号")
    private String serviceCode;

    /** 彩信版本 */
    @Excel(name = "彩信版本")
    private String mm7version;

    /** 帐户名称 */
    @Excel(name = "帐户名称")
    private String appName;

    /** submited已提交,matched已匹配,pushed已推送 */
    @Excel(name = "submited已提交,matched已匹配,pushed已推送")
    private String dataStatus;

    /** 通道响应messagid */
    @Excel(name = "通道响应messagid")
    private String channelMsgId;

    /** 日期 非数据库的映射关系*/
    @Excel(name = "日期")
    private String logDate;

    @Excel(name = "提交开始开始时间")
    private String startTime ;

    @Excel(name = "提交结束开始时间")
    private String endTime;

    @Excel(name = "通道id")
    private String channelId;

    private Long companyId;

}
