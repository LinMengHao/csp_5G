package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WxMinUpdateInfoBo {
    @ApiModelProperty("昵称")
    private String nick;

    @ApiModelProperty("头像")
    private String cover;
}
