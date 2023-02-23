package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dashan
 * @date 2019/4/27 3:36 PM
 */
@Data
public class UserSearchBo extends BaseBo {

    @ApiModelProperty("模糊搜索name")
    private String name;

    @ApiModelProperty("模糊搜索昵称")
    private String nick;

//    @ApiModelProperty("模糊搜索email")
//    private String email;

    @ApiModelProperty("模糊搜索phone")
    private String phone;

}
