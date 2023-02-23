package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EducationSearchBo extends BaseBo {

    @ApiModelProperty("状态：不传：全部，0-审核中，1-认证成功，2-认证失败")
    private Integer state;
}
