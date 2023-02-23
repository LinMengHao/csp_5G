package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MessageSearchBo extends BaseBo {
    @ApiModelProperty("type")
    private Integer type;
}
