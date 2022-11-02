package com.xzkj.operatorService.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
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
public class TChatbotVideo implements Serializable {

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
     * 视频短信服务代码
     */
    @TableField("servCode")
    private String servCode;

    /**
     * 视频短信下发速率
     */
    @TableField("smsBandWidth")
    private String smsBandWidth;

    /**
     * 视频短信是否支持异网（以BBOSS定义为准，1表示支持）：
1:支持
0:不支持
     */
    @TableField("otherCarrier")
    private String otherCarrier;

    /**
     * 视频短信报备签名 取视频短信平台线上报备签名
     */
    @TableField("videoSign")
    private String videoSign;

    /**
     * 视频短信产品订单编号ID（即productId
     */
    @TableField("prodId")
    private String prodId;

    /**
     * 视频短信接口账号
     */
    @TableField("apiUserName")
    private String apiUserName;

    /**
     * 视频短信接口密码, 采用MD5加密加盐
     */
    @TableField("apiPassword")
    private String apiPassword;

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;
}
