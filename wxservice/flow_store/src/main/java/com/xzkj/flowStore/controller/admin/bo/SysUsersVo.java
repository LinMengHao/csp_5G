package com.xzkj.flowStore.controller.admin.bo;

import com.google.common.collect.Lists;
import com.xzkj.flowStore.entity.SysRole;
import com.xzkj.flowStore.entity.SysUsers;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SysUsersVo extends SysUsers {


    @ApiModelProperty(value = "权限菜单")
    private List<SysMenuVo> menuList = Lists.newArrayList();


    @ApiModelProperty(value = "角色列表")
    private List<SysRole> roleList = Lists.newArrayList();


}
