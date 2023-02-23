package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserSimpleBo {

    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "昵称")
    private String nick;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "头像")
    private String cover;

    @ApiModelProperty(value = "学校名称")
    private String schoolName="";
}
