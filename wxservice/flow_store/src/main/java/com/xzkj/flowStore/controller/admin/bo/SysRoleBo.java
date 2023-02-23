package com.xzkj.flowStore.controller.admin.bo;

import com.xzkj.flowStore.entity.SysRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class SysRoleBo extends SysRole {


    @ApiModelProperty(value = "菜单Id")
    private String menuIds;




}
