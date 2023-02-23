package com.xzkj.flowStore.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.xzkj.flowStore.controller.admin.bo.MenuSearchBo;
import com.xzkj.flowStore.controller.admin.bo.SysMenuVo;
import com.xzkj.flowStore.entity.SysMenu;
import com.xzkj.flowStore.entity.SysRole;
import com.xzkj.flowStore.service.SysMenuService;
import com.xzkj.flowStore.service.SysRoleService;
import com.xzkj.flowStore.utils.MsgBean;
import com.xzkj.flowStore.vo.SysRoleVo;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统角色表 前端控制器
 * </p>
 *
 * @author dashan
 * @since 2020-01-06
 */
@Api(tags = "002.后台权限管理")
@RestController
@RequestMapping("/admin/role")
public class SysRoleAction {


    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;


    @PostMapping("/list")
    @ApiOperation("菜单列表")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "菜单列表", response = SysRole.class)})
    public MsgBean list(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody(required = false) MenuSearchBo searchBo) {

        if (searchBo == null) {
            searchBo = new MenuSearchBo();
        }

        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(searchBo.getName()), "name", searchBo.getName());
        wrapper.eq("state", 0);
        IPage<SysRole> ipage = new Page<>(searchBo.getPageNo(), searchBo.getPageSize());

        sysRoleService.page(ipage, wrapper);


        IPage<SysRoleVo> voPage = new Page<>();
        BeanUtils.copyProperties(ipage, voPage);

        List<SysRoleVo> voList = Lists.newArrayList();
        for (SysRole role : ipage.getRecords()) {
            SysRoleVo vo = new SysRoleVo();
            BeanUtils.copyProperties(role, vo);
            vo.setMenuList(getMenuAll(role.getMenuIds()));
            voList.add(vo);
        }
        voPage.setRecords(voList);

        return MsgBean.ok().putData(voPage);
    }

    //ids 都是二级菜单id
    private List<SysMenuVo> getMenuAll(String ids) {

        List<SysMenuVo> voList = Lists.newArrayList();

        List<String> idList = Splitter.on(",").splitToList(ids);

        if (idList == null || idList.size() == 0) {
            return voList;
        }

        //先查询二级菜单
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        wrapper.in("id", idList);
        wrapper.eq("state", 0);
        wrapper.gt("pid", 0);
        wrapper.orderByDesc("weight");
        List<SysMenu> menuList = sysMenuService.list(wrapper);

        if (menuList != null && menuList.size() > 0) {
            Set<Long> pidSet = Sets.newHashSet();
            for (SysMenu menu : menuList) {
                pidSet.add(menu.getPid());
            }

            //查询一级菜单
            QueryWrapper<SysMenu> wrapper2 = new QueryWrapper<>();
            wrapper2.in("id", pidSet);
            wrapper2.eq("state", 0);
            wrapper2.eq("pid", 0);
            wrapper2.orderByDesc("weight");
            List<SysMenu> firstList = sysMenuService.list(wrapper2);

            if (firstList != null && firstList.size() > 0) {
                for (SysMenu menu : firstList) {
                    SysMenuVo vo = new SysMenuVo();
                    BeanUtils.copyProperties(menu, vo);
                    List<SysMenuVo> subList = Lists.newArrayList();
                    for (SysMenu secondMenu : menuList) {
                        if (secondMenu.getPid().equals(menu.getId())) {
                            SysMenuVo sub = new SysMenuVo();
                            BeanUtils.copyProperties(secondMenu, sub);
                            subList.add(sub);
                        }
                    }
                    vo.setMenuList(subList);
                    voList.add(vo);
                }
            }
        }
        return voList;
    }


    @PostMapping("/save")
    @ApiOperation("新增或修改角色")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "新增或修改角色信息", response = MsgBean.class)})
    public MsgBean save(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody SysRole role) {

        if (role.getId() == null) {


            if (StringUtils.isBlank(role.getName())) {
                return MsgBean.error("角色名称不能为空！");
            }

            SysRole role1 = sysRoleService.getByName(role.getName());

            if (role1 != null) {
                return MsgBean.error("该角色已经存在！");
            }

            if (StringUtils.isBlank(role.getMenuIds())) {
                return MsgBean.error("请设置角色的菜单权限！");
            }

            String[] menuIds = role.getMenuIds().replace("，", ",").split(",");

            if (menuIds == null || menuIds.length == 0) {
                return MsgBean.error("请设置角色的菜单权限！");
            }

            for (String menuId : menuIds) {
                if (!StringUtils.isNumeric(menuId)) {
                    return MsgBean.error("请正确的设置菜单权限！");
                }
            }

            role.setMenuIds(Joiner.on(",").join(menuIds));

            boolean result = sysRoleService.save(role);

            if (result) {
                return MsgBean.ok("操作成功！");
            } else {
                return MsgBean.error("操作失败！");
            }

        } else {

            SysRole role1 = sysRoleService.getByName(role.getName());
            if (role1 != null && !role1.getId().equals(role.getId())) {
                return MsgBean.error("该角色已经存在！");
            }

            if (StringUtils.isNotEmpty(role.getMenuIds())) {
                String[] menuIds = role.getMenuIds().replace("，", ",").split(",");

                if (menuIds == null || menuIds.length == 0) {
                    return MsgBean.error("请设置角色的菜单权限！");
                }
                role.setMenuIds(Joiner.on(",").join(menuIds));
            }
            role.setUpdatedAt(null);
            boolean result = sysRoleService.saveOrUpdate(role);

            if (result) {
                return MsgBean.ok("操作成功！");
            } else {
                return MsgBean.error("操作失败！");
            }
        }
    }

    @GetMapping("/getAllRole")
    @ApiOperation("根据角色Id查询菜单列表")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "根据角色Id查询菜单列表", response = SysMenu.class)})
    public MsgBean getMenus(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("state", 0);
        wrapper.orderByAsc("id");
        List<SysRole> list = sysRoleService.list(wrapper);
        return MsgBean.ok().putData(list);
    }
}

