package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VerifyBo {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("审核：1-认证成功，2-认证失败")
    private Integer state;
    @ApiModelProperty("备注：认证失败原因")
    private String remark;
}
