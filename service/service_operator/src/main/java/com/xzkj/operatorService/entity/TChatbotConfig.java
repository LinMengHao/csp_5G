package com.xzkj.operatorService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TChatbotConfig implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * uuid
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * chatbotid
     */
    @TableField("chatbotId")
    private String chatbotId;

    /**
     * 操作类型：0-新增1-修改
     */
    @TableField("opType")
    private Integer opType;

    /**
     * 并发最大速率（每秒发送号码数）
     */
    private Integer concurrent;

    /**
     * 需转换为6位二进制，从右往左，最右位为第一位：： 第一位：0支持主动消息下发（即允许群发，主动发送1条消息也属于群发） 1不支持主动消息下发（即不允许群发） 第二位：0支持上行触发消息下发（即支持交互消息） 1不支持上行触发消息下发（即不支持交互消息，若不支持交互，则MaaP平台将拒绝Chatbot下行的所有带有InReplyTo-Contribution-id的消息，并返回HTTP 403 Forbidden响应。） 第三位：0容许回落 1禁止回落 第四位: 0支持上行UP1.0消息  1不支持上行UP1.0消息 第五位：0 允许上行  1不允许上行 第六位：预留 （注：上行触发的消息下发，消息体会携带inReplyTo-Contribution-ID字段）

     */
    @TableField("State")
    private Integer State;

    /**
     * 日最大消息下发量，0表示不限制
     */
    private Integer amount;

    /**
     * 月最大消息下发量，0表示不限制
     */
    @TableField("mAmount")
    private Integer mAmount;

    /**
     * 交互范围与Chatbot目录查询范围：
0–省内，
1–全网，
2–其它（预留）
     */
    @TableField("serviceRange")
    private Integer serviceRange;

    /**
     * 上传文件大小限制，单位为M，默认值为20，最大取值为500，与终端允许收发的最大文件大小保持一致。
     */
    @TableField("filesizeLimit")
    private Integer filesizeLimit;

    /**
     * 接入层对CSP的鉴权Token，通过sha256(password)得到，并通过base64传输
password规则：8-20位大小写字母、数字、特殊符号
     */
    @TableField("cspToken")
    private String cspToken;

    /**
     * 操作时间，例子2020-04-04T23:59:00Z
     */
    @TableField("opTime")
    private String opTime;

    /**
     * 版本号，从1开始递增
     */
    @TableField("eTag")
    private Integer eTag;

    /**
     * 操作流水号
     */
    @TableField("messageId")
    private String messageId;

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;


}
