package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author dashan
 * @date 2019/4/6 3:19 PM
 */
@ApiModel
public class UserLoginBo implements Serializable {

    private static final long serialVersionUID = -1L;


    @ApiModelProperty(value = "登陆方式：1-账号登陆，2-验证码登陆")
    private Integer type;

    @ApiModelProperty(value = "手机号、邮箱、姓名都可以能录")
    private String username;


    @ApiModelProperty(value = "密码或者验证码")
    private String password;


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
