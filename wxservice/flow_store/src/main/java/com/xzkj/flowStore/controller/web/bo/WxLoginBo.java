package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WxLoginBo  {

    @ApiModelProperty(value = "微信授权登录code")
    private String  code;

}
