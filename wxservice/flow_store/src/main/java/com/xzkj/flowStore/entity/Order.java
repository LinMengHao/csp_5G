package com.xzkj.flowStore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
@Data
@TableName("`order`")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * user表ID
     */
    @ApiModelProperty("user表ID")
    @TableField("user_id")
    private Long userId;

    /**
     * 订单名称
     */
    @ApiModelProperty("订单名称")
    @TableField("order_name")
    private String orderName;

    /**
     * 订单编号
     */
    @ApiModelProperty("订单编号")
    @TableField("order_no")
    private String orderNo;

    /**
     * 外部订单号
     */
    @ApiModelProperty("外部订单号")
    @TableField("out_order_no")
    private String outOrderNo;

    /**
     * 金额：单位分
     */
    @ApiModelProperty("金额：单位分")
    @TableField("amount")
    private Integer amount;

    /**
     * 实际支付金额
     */
    @ApiModelProperty("实际支付金额：单位分")
    @TableField("bill_amount")
    private Integer billAmount;

    /**
     * product表id
     */
    @ApiModelProperty("商品id")
    @TableField("product_id")
    private Long productId;

    /**
     * 下单ip
     */
    @ApiModelProperty("ip")
    @TableField("ip")
    private String ip;

    /**
     * 支付方式：0-未知，1-支付宝，2-微信
     */
    @ApiModelProperty("支付方式：0-未知，1-支付宝，2-微信")
    @TableField("pay_type")
    private Integer payType;

    /**
     * 支付时间
     */
    @ApiModelProperty("支付时间")
    @TableField("paid_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime paidTime;

    /**
     * 状态：-1-已删除，0-未支付，1-已支付，2-已过期，3-已退款
     */
    @ApiModelProperty("状态：-1-已删除，0-未支付，1-已支付，2-已过期，3-已退款")
    @TableField("state")
    private Integer state;

    /**
     * 同步状态：0-未同步，1-同步中，2-同步成功，3-同步失败
     */
    @ApiModelProperty("同步状态：0-未同步，1-同步中，2-同步成功，3-同步失败")
    @TableField("sync_state")
    private Integer syncState;

    /**
     * 同步成功时间
     */
    @ApiModelProperty("同步成功时间")
    @TableField("sync_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime syncTime;


    @TableField("created_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;


}
