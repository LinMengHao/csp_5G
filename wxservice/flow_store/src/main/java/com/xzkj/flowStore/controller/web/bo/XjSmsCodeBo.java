package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class XjSmsCodeBo {

    @ApiModelProperty("手机号")
    private String phone;
}
