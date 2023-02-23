package com.xzkj.flowStore.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.xzkj.flowStore.controller.admin.bo.MenuSearchBo;
import com.xzkj.flowStore.controller.admin.bo.SysMenuVo;
import com.xzkj.flowStore.entity.SysMenu;
import com.xzkj.flowStore.service.SysMenuService;
import com.xzkj.flowStore.utils.MsgBean;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 系统菜单表 前端控制器
 * </p>
 *
 * @author dashan
 * @since 2020-01-06
 */
@Api(tags = "003.后台权限管理")
@RestController
@RequestMapping("/admin/menu")
public class SysMenuAction {


    @Autowired
    private SysMenuService sysMenuService;


    @PostMapping("/list")
    @ApiOperation("菜单列表")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "菜单列表", response = SysMenuVo.class)})
    public MsgBean list(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody(required = false) MenuSearchBo searchBo) {

        if (searchBo == null) {
            searchBo = new MenuSearchBo();
        }

        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(searchBo.getName()), "name", searchBo.getName());
        wrapper.eq("pid", 0);
        wrapper.eq("state", 0);
        wrapper.orderByDesc("weight");
        IPage<SysMenu> ipage = new Page<>(searchBo.getPageNo(), searchBo.getPageSize());
        sysMenuService.page(ipage, wrapper);


        Set<Long> idSet = Sets.newHashSet();

        for (SysMenu menu : ipage.getRecords()) {
            idSet.add(menu.getId());
        }

        Map<Long, List<SysMenuVo>> map = getMenuMap(idSet);


        List<SysMenuVo> voList = Lists.newArrayList();
        for (SysMenu menu : ipage.getRecords()) {
            SysMenuVo vo = new SysMenuVo();
            BeanUtils.copyProperties(menu, vo);
            if (map.containsKey(menu.getId())) {
                vo.setMenuList(map.get(menu.getId()));
            }
            voList.add(vo);
        }

        IPage<SysMenuVo> voPage = new Page<>();
        BeanUtils.copyProperties(ipage, voPage);

        voPage.setRecords(voList);
        return MsgBean.ok().putData(voPage);
    }


    private Map<Long, List<SysMenuVo>> getMenuMap(Set<Long> idSet) {

        Map<Long, List<SysMenuVo>> map = Maps.newHashMap();
        if (idSet == null || idSet.size() == 0) {
            return map;
        }

        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        wrapper.in("pid", idSet);
        wrapper.eq("state", 0);
        wrapper.orderByDesc("weight");

        List<SysMenu> list = sysMenuService.list(wrapper);

        if (list != null && list.size() > 0) {
            for (SysMenu menu : list) {
                SysMenuVo sysMenuVo = new SysMenuVo();
                BeanUtils.copyProperties(menu, sysMenuVo);
                if (map.containsKey(menu.getPid())) {
                    map.get(menu.getPid()).add(sysMenuVo);
                } else {
                    List<SysMenuVo> voList = Lists.newArrayList();
                    voList.add(sysMenuVo);
                    map.put(menu.getPid(), voList);
                }
            }
        }
        return map;
    }


    @PostMapping("/save")
    @ApiOperation("新增或修改菜单")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "新增或修改菜单信息", response = MsgBean.class)})
    public MsgBean save(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody SysMenu menu) {

        if (menu.getId() == null) {

            if (StringUtils.isBlank(menu.getName())) {
                return MsgBean.error("菜单名称不能为空！");
            }
            if (StringUtils.isBlank(menu.getIcon())) {
                return MsgBean.error("菜单图标不能为空！");
            }
            if (StringUtils.isBlank(menu.getUrl())) {
                return MsgBean.error("菜单路径不能为空！");
            }

            boolean result = sysMenuService.save(menu);

            if (result) {
                return MsgBean.ok("操作成功！");
            } else {
                return MsgBean.error("操作失败！");
            }

        } else {
            menu.setUpdatedAt(null);
            boolean result = sysMenuService.saveOrUpdate(menu);

            if (result) {
                return MsgBean.ok("操作成功！");
            } else {
                return MsgBean.error("操作失败！");
            }
        }
    }


    @GetMapping("/getByRoleId")
    @ApiOperation("根据角色Id查询菜单列表")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "根据角色Id查询菜单列表", response = SysMenu.class)})
    public MsgBean getMenus(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @ApiParam("roleId") @RequestParam(value = "roleId", required = false) Long roleId) {

        if (roleId == null || roleId <= 0) {
            return MsgBean.error("roleId不正确！");
        }
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("pid", roleId);
        wrapper.eq("state", 0);
        wrapper.orderByDesc("weight");
        List<SysMenu> list = sysMenuService.list(wrapper);
        return MsgBean.ok().putData(list);
    }


    @GetMapping("/getAllMenus")
    @ApiOperation("根据角色Id查询菜单列表")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "根据角色Id查询菜单列表", response = SysMenu.class)})
    public MsgBean getAllMenus(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token) {

        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("state", 0);
        wrapper.orderByAsc("pid");
        wrapper.orderByDesc("weight");
        List<SysMenu> list = sysMenuService.list(wrapper);

        Map<Long, List<SysMenuVo>> map = Maps.newTreeMap();


        List<SysMenuVo> voList = Lists.newArrayList();

        for (SysMenu menu : list) {

            SysMenuVo vo = new SysMenuVo();
            BeanUtils.copyProperties(menu, vo);

            if (menu.getPid() == 0) {
                voList.add(vo);
            } else {
                if (map.containsKey(menu.getPid())) {
                    map.get(menu.getPid()).add(vo);
                } else {
                    map.put(menu.getPid(), Lists.newArrayList(vo));
                }
            }
        }

        for (SysMenuVo vo :voList ){
            if (map.containsKey(vo.getId())) {
                vo.setMenuList(map.get(vo.getId()));
            }
        }
        return MsgBean.ok().putData(voList);
    }
}

