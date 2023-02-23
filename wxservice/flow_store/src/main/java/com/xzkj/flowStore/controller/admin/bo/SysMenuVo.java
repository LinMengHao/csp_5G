package com.xzkj.flowStore.controller.admin.bo;

import com.google.common.collect.Lists;
import com.xzkj.flowStore.entity.SysMenu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class SysMenuVo extends SysMenu {

    
    @ApiModelProperty(value = "子菜单列表")
    private List<SysMenuVo> menuList = Lists.newArrayList();
}
