package com.xzkj.flowPassthrough.entity;

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
 * @since 2023-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderPass implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户标识
     */
    private Long uid;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 流量包编号
     */
    private String pkgcode;

    /**
     * 商户回调地址
     */
    private String returl;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 描述备注
     */
    private String info;

    /**
     * 商户订单号
     */
    private String saleorderno;

    /**
     * 平台订单
     */
    @TableField("orderNo")
    private String orderNo;

    /**
     * 充值状态
     */
    private Integer chargeState;

    /**
     * 充值描述
     */
    private String stateMsg;


    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;

    /**
     * 用户名称
     */
    private String usrName;

    /**
     * 日志时间
     */
    private String logDate;

    /**
     * 记录表
     */
    private String tableName;
}
