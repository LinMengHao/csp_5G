package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginBo {
    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("验证码")
    private String code;
}
