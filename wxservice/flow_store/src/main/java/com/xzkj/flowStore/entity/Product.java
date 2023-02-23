package com.xzkj.flowStore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @TableField("title")
    private String title;

    /**
     * 子标题
     */
    @ApiModelProperty("子标题")
    @TableField("sub_title")
    private String subTitle;

    /**
     * 销售价格：单位分
     */
    @ApiModelProperty("销售价格：单位分")
    @TableField("amount")
    private Integer amount;

//    /**
//     * 价格描述
//     */
//    @ApiModelProperty("")
//    @TableField("amount_desc")
//    private String amountDesc;

    /**
     * 天数
     */
    @ApiModelProperty("天数")
    @TableField("day")
    private Integer day;

    /**
     * 类型：1-天，2-年
     */
    @ApiModelProperty("类型：1-天，2-年")
    @TableField("day_type")
    private Integer dayType;

    /**
     * 产品类型：0-个人产品，1-企业产品
     */
    @ApiModelProperty("产品类型：0-个人产品，1-企业产品")
    @TableField("type")
    private Integer type;

    /**
     * 服务
     */
    @ApiModelProperty("服务")
    @TableField("service")
    private String service;

    /**
     * 商品详情
     */
    @ApiModelProperty("商品详情")
    @TableField("content")
    private String content;

    /**
     * 商品图片
     */
    @ApiModelProperty("商品图片")
    @TableField("pic")
    private String pic;

    /**
     * 商品编码
     */
    @ApiModelProperty("商品编码")
    @TableField("code")
    private String code;

    /**
     * 状态：-1-已删除，0-已下架，1-已上架
     */
    @ApiModelProperty("状态：-1-已删除，0-已下架，1-已上架")
    @TableField("state")
    private Integer state;


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
