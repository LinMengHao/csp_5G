package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdatePhoneBo {

    @ApiModelProperty("微信unionId")
    private String unionId;

    @ApiModelProperty("微信openId")
    private String openId;

    @ApiModelProperty("手机号加密字符串")
    private String phoneData;

    @ApiModelProperty("ivB64")
    private String iv;
}
