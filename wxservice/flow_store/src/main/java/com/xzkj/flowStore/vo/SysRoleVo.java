package com.xzkj.flowStore.vo;

import com.google.common.collect.Lists;
import com.xzkj.flowStore.controller.admin.bo.SysMenuVo;
import com.xzkj.flowStore.entity.SysRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SysRoleVo extends SysRole {


    @ApiModelProperty(value = "菜单列表")
    private List<SysMenuVo> menuList = Lists.newArrayList();
}
