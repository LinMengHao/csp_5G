package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SavePhoneBo {

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("验证码(测试环境：9999)")
    private String code;
}
