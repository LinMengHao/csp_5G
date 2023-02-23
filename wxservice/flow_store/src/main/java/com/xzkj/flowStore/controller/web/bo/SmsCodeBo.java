package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SmsCodeBo {
    @ApiModelProperty("类型：1-登陆")
    private Integer type;

    @ApiModelProperty("手机号")
    private String phone;
}
