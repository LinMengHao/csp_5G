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
 * 用户表
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
@Data
@TableName(value = "`user`")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    @TableField("nick")
    private String nick;

    /**
     * 真实姓名
     */
    @ApiModelProperty("真实姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty("密码")
    @TableField("password")
    private String password;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    @TableField("cover")
    private String cover;

    @ApiModelProperty("微信小程序openId")
    @TableField("min_open_id")
    private String minOpenId;


    @ApiModelProperty("微信小程序sessionKey")
    @TableField("min_session_key")
    private String minSessionKey;


    @ApiModelProperty("微信")
    @TableField("union_id")
    private String unionId;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @TableField("phone")
    private String phone;

    /**
     * 手机号
     */
    @ApiModelProperty("ip")
    @TableField("ip")
    private String ip;

    @ApiModelProperty("星际userId")
    @TableField("xj_user_id")
    private String xjUserId;

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
