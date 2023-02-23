package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class MenuSearchBo extends BaseBo {


    @ApiModelProperty(value = "名称模糊匹配")
    private String name;


}
