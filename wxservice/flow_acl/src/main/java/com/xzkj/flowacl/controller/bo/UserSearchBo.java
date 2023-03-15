package com.xzkj.flowacl.controller.bo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
@Data
public class UserSearchBo {
    private String id;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 加密盐
     */
    private String slat;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户签名
     */
    private String token;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    /**
     * 描述
     */
    private String info;


    private Date createTime;


    private Date updateTime;
}
