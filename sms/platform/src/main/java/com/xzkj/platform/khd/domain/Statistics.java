package com.xzkj.platform.khd.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.xzkj.platform.common.annotation.Excel;

import com.xzkj.platform.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Statistics extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /** 成功率 */
    @Excel(name = "成功率")
    private String successRate;

    /** 失败率 */
    @Excel(name = "失败率")
    private String failureRate;

    /** 未知率 */
    @Excel(name = "未知率")
    private String unknownRate;

    /** 未知率 */
    @Excel(name = "触黑率")
    private String blackRate;

    /** 未知率 */
    @Excel(name = "限制率")
    private String limitRate;

    /** 自增id */
    @Excel(name = "自增id")
    private Integer id;

    /** 日期 非数据库的映射关系*/
    @Excel(name = "日期")
    private String logDate;

    /** 帐户名称 */
    @Excel(name = "帐户名称")
    private String appName;

    /** 账户id */
    @Excel(name = "账户id")
    private Integer appId;

    /** 通道id */
    @Excel(name = "通道id")
    private Integer channelId;

    /** 提交总数 */
    @Excel(name = "提交总数")
    private Integer sendTotal;

    /** 状态成功 */
    @Excel(name = "状态成功")
    private Integer reportDelivrd;

    /** 状态失败 */
    @Excel(name = "状态失败")
    private Integer reportUndeliv;

    /** 黑名单 */
    @Excel(name = "黑名单")
    private Integer reportBlack;

    /** 状态未知 */
    @Excel(name = "状态未知")
    private Integer reportUnknown;


    /** 状态未知 */
    @Excel(name = "点击")
    private Integer loadDelivrd;


    /** 更新时间 */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 开始时间 */
    @Excel(name = "开始时间")
    private String startTime;

    /** 结束时间 */
    @Excel(name = "结束时间")
    private String endTime;


    /** 统计类型 */
    @Excel(name = "统计类型")
    private String statisticType;

}

