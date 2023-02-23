package com.xzkj.flowStore.utils.bo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author dashan
 * @date 2019/6/20 12:48 AM
 */
public class QqLoginBo implements Serializable {


    private static final long serialVersionUID = -1L;


    @ApiModelProperty(value = "QQ授权登录code")
    private String  code;

    @ApiModelProperty(value = "授权登录回掉地址")
    private String redirectUri;


    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
