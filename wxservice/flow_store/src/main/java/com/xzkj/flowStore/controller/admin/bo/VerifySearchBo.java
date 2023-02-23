package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VerifySearchBo extends BaseBo {

    @ApiModelProperty("审核状态：为空-全部，0-审核中，1-审核成功，2-审核失败")
    private Integer state = -1;

    @ApiModelProperty("模糊搜索name")
    private String name;

    @ApiModelProperty("模糊搜索昵称")
    private String nick;

    @ApiModelProperty("模糊搜索email")
    private String email;

    @ApiModelProperty("模糊搜索phone")
    private String phone;




}
