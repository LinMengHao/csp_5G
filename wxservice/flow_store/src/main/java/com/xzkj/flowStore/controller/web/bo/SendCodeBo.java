package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendCodeBo {

    @ApiModelProperty("手机号")
    private String phone;


}
