package com.xzkj.flowStore.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.xzkj.flowStore.controller.admin.bo.LoginBo;
import com.xzkj.flowStore.controller.admin.bo.SysMenuVo;
import com.xzkj.flowStore.controller.admin.bo.SysUserSearchBo;
import com.xzkj.flowStore.controller.admin.bo.SysUsersVo;
import com.xzkj.flowStore.entity.SysMenu;
import com.xzkj.flowStore.entity.SysRole;
import com.xzkj.flowStore.entity.SysUsers;
import com.xzkj.flowStore.service.SysMenuService;
import com.xzkj.flowStore.service.SysRoleService;
import com.xzkj.flowStore.service.SysUsersService;
import com.xzkj.flowStore.utils.MD5Util;
import com.xzkj.flowStore.utils.MsgBean;
import com.xzkj.flowStore.utils.TokenUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Api(tags = "001.后台管理员列表")
@RestController
@RequestMapping("/admin/sysUsers")
public class SysUsersAction {


    @Autowired
    private SysUsersService sysUsersService;


    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;


    @PostMapping("/login")
    @ApiOperation("管理员登录接口")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "新增或修改管理员信息", response = SysUsersVo.class)})
    public MsgBean login(@RequestBody LoginBo loginBo) {

        String loginName = loginBo.getLoginName();

        String password = loginBo.getPassword();

        if (StringUtils.isBlank(loginName)) {
            return MsgBean.error("请输入用户名！");
        }

        if (StringUtils.isBlank(password) || password.length() < 6) {
            return MsgBean.error("请输入有效的密码！");
        }

        SysUsers users = sysUsersService.login(loginBo);

        if (users == null) {
            return MsgBean.error("用户名或密码错误！");
        }

        users.setToken(TokenUtil.makeToken(users.getId(), users.getPassword()));

        SysUsersVo vo = new SysUsersVo();
        BeanUtils.copyProperties(users, vo);

        initMenuList(vo);

        MsgBean msg = new MsgBean();
        msg.put("user", vo);
        msg.put("data", vo.getToken());
        return msg;
    }


    private void initMenuList(SysUsersVo usersVo) {
        if (StringUtils.isBlank(usersVo.getRoleIds())) {
            return;
        }

        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.in("id", usersVo.getRoleIds().split(","));
        wrapper.eq("state", 0);
        wrapper.orderByAsc("id");

        List<SysRole> roleList = sysRoleService.list(wrapper);

        if (roleList != null && roleList.size() > 0) {

            usersVo.setRoleList(roleList);

            Set<Long> menuIdSet = Sets.newHashSet();
            for (SysRole role : roleList) {
                if (role.getMenuIds() != null) {
                    String[] menuIds = role.getMenuIds().split(",");
                    for (String menuId : menuIds) {
                        menuIdSet.add(Long.parseLong(menuId));
                    }
                }
            }

            List<SysMenuVo> voList = Lists.newArrayList();

            if (menuIdSet.size() > 0) {

                QueryWrapper<SysMenu> menuWrapper = new QueryWrapper<>();
                menuWrapper.in("id", menuIdSet);
                menuWrapper.eq("state", 0);
                menuWrapper.gt("pid", 0);
                menuWrapper.orderByDesc("weight");
                List<SysMenu> menuList = sysMenuService.list(menuWrapper);


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
                usersVo.setMenuList(voList);
            }
        }
    }


    @PostMapping("/getUserInfo")
    @ApiOperation("通过token获取管理员信息")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "新增或修改管理员信息", response = MsgBean.class)})
    public MsgBean getUserInfo(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token) {

        if (StringUtils.isBlank(token)) {
            return MsgBean.error("请输入Token！");
        }


        Long uid = TokenUtil.getUIDFromToken(token);


        if (uid == null || uid <= 0) {
            return MsgBean.error("请输入有效的token！");
        }


        SysUsers users = sysUsersService.getById(uid);

        if (users == null) {
            return MsgBean.error("用户不存在！");
        }

        if (StringUtils.isBlank(users.getLogo())) {
            users.setLogo("http://www.fangao.cc/fgadmin/sources/img/logo-icon.7c14875.jpg");
        }
        users.setToken(TokenUtil.makeToken(users.getId(), users.getPassword()));

        SysUsersVo vo = new SysUsersVo();
        BeanUtils.copyProperties(users, vo);
        initMenuList(vo);
        return MsgBean.ok().putData(vo);
    }


    @PostMapping("/getUserInfoById")
    @ApiOperation("通过token获取管理员信息")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "新增或修改管理员信息", response = MsgBean.class)})
    public MsgBean getUserInfoById(@ApiParam("token") @RequestHeader(value = "token", required = false) String token,
                                   @ApiParam("id") @RequestParam(value = "id", required = false) Long id) {

        if (id == null || id <= 0) {
            return MsgBean.error("请输入有效的token！");
        }

        SysUsers users = sysUsersService.getById(id);

        if (users == null) {
            return MsgBean.error("用户不存在！");
        }

        if (StringUtils.isBlank(users.getLogo())) {
            users.setLogo("http://www.fangao.cc/fgadmin/sources/img/logo-icon.7c14875.jpg");
        }
        users.setToken(TokenUtil.makeToken(users.getId(), users.getPassword()));
        return MsgBean.ok().putData(users);
    }


    @PostMapping("/list")
    @ApiOperation("管理员列表")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "新增或修改管理员信息", response = MsgBean.class)})
    public MsgBean list(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody(required = false) SysUserSearchBo searchBo
    ) {

        if (searchBo == null) {
            searchBo = new SysUserSearchBo();
        }
        QueryWrapper<SysUsers> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(searchBo.getName()), "name", searchBo.getName());
        wrapper.like(StringUtils.isNotBlank(searchBo.getEmail()), "email", searchBo.getEmail());
        wrapper.like(StringUtils.isNotBlank(searchBo.getPhone()), "phone", searchBo.getPhone());
        wrapper.eq("state", 0);
        wrapper.orderByAsc("id");
        IPage<SysUsers> ipage = new Page<>(searchBo.getPageNo(), searchBo.getPageSize());
        sysUsersService.page(ipage, wrapper);


        IPage<SysUsersVo> voPage = new Page<>();
        BeanUtils.copyProperties(ipage, voPage);


        Set<Long> classesIdSet = Sets.newHashSet();

        Set<Long> roleIdSet = Sets.newHashSet();

        for (SysUsers sysUser : ipage.getRecords()) {
            if (StringUtils.isNotBlank(sysUser.getRoleIds())) {
                String[] ids = sysUser.getRoleIds().split(",");
                for (String id : ids) {
                    roleIdSet.add(Long.parseLong(id));
                }
            }

            if (StringUtils.isNotBlank(sysUser.getClassesIds())) {
                String[] ids = sysUser.getClassesIds().split(",");
                for (String id : ids) {
                    classesIdSet.add(Long.parseLong(id));
                }
            }
        }

        Map<Long, SysRole> roleMap = sysRoleService.getMapByIdSet(roleIdSet);


        List<SysUsersVo> voList = Lists.newArrayList();

        for (SysUsers sysUser : ipage.getRecords()) {

            SysUsersVo vo = new SysUsersVo();
            BeanUtils.copyProperties(sysUser, vo);

            List<SysRole> roleLsit = Lists.newArrayList();


            if (StringUtils.isNotBlank(sysUser.getRoleIds())) {
                String[] ids = sysUser.getRoleIds().split(",");
                for (String id : ids) {
                    SysRole role = roleMap.get(Long.parseLong(id));
                    if (role != null) {
                        roleLsit.add(role);
                    }
                }
            }


            vo.setRoleList(roleLsit);
            voList.add(vo);
        }

        voPage.setRecords(voList);
        return MsgBean.ok().putData(voPage);
    }


    @PostMapping("/save")
    @ApiOperation("新增或修改管理员信息")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "新增或修改管理员信息", response = MsgBean.class)})
    public MsgBean save(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody SysUsers users) {

        if (users.getId() == null) {
            if (StringUtils.isBlank(users.getName())) {
                return MsgBean.error("姓名不能为空！");
            }
            // if (StringUtils.isBlank(users.getEmail())) {
            //     return MsgBean.error("邮箱不能为空！");
            // }
            // if (StringUtils.isBlank(users.getPhone())) {
            //     return MsgBean.error("手机号不能为空！");
            // }
            if (StringUtils.isBlank(users.getPassword())) {
                return MsgBean.error("密码不能为空！");
            } else {
                users.setPassword(MD5Util.MD5(users.getPassword()));
            }

            if (StringUtils.isBlank(users.getRoleIds())) {
                return MsgBean.error("请设置用户的角色！");
            }

            String[] roleIds = users.getRoleIds().replace("，", ",").split(",");

            if (roleIds == null || roleIds.length == 0) {
                return MsgBean.error("请设置用户的角色！");
            }

            users.setRoleIds(Joiner.on(",").join(roleIds));

            SysUsers emailUser = sysUsersService.getByEmail(users.getEmail());

            if (emailUser != null) {
                return MsgBean.error("该邮箱已经绑定！");
            }


            SysUsers phoneUser = sysUsersService.getByPhone(users.getPhone());

            if (phoneUser != null) {
                return MsgBean.error("该手机号已经绑定！");
            }

//            //判断是否绑定了 老师
//            if (users.getTeacherId() != null && users.getTeacherId() > 0) {
//                Teacher teacher = teacherService.getById(users.getTeacherId());
//                if (teacher == null || teacher.getState() < 0) {
//                    return MsgBean.error("绑定的老师不存在！");
//                }
//                //如果该老师已经绑定了，则需要先解绑后再绑定
//
//                SysUsers sysUsers = sysUsersService.getByTeacherId(users.getTeacherId());
//                if (sysUsers != null) {
//                    return MsgBean.error("该老师已经绑定在【" + sysUsers.getName() + "】用户下！");
//                }
//            }
            boolean result = sysUsersService.saveOrUpdate(users);

            if (result) {
                return MsgBean.ok("操作成功！");
            } else {
                return MsgBean.error("操作失败！");
            }

        } else {


            if (StringUtils.isNotBlank(users.getEmail())) {

                SysUsers emailUser = sysUsersService.getByEmail(users.getEmail());

                if (emailUser != null && !emailUser.getId().equals(users.getId())) {
                    return MsgBean.error("该邮箱已经绑定！");
                }
            }

            if (StringUtils.isNotBlank(users.getPhone())) {
                SysUsers phoneUser = sysUsersService.getByPhone(users.getPhone());

                if (phoneUser != null && !phoneUser.getId().equals(users.getId())) {
                    return MsgBean.error("该手机号已经绑定！");
                }
            }


            if (StringUtils.isNotEmpty(users.getRoleIds())) {
                String[] roleIds = users.getRoleIds().replace("，", ",").split(",");
                if (roleIds == null || roleIds.length == 0) {
                    return MsgBean.error("请设置用户的角色！");
                }
                users.setRoleIds(Joiner.on(",").join(roleIds));
            }

            if (StringUtils.isNotBlank(users.getPassword())) {
                users.setPassword(MD5Util.MD5(users.getPassword()));
            }

            users.setUpdatedAt(null);
            boolean result = sysUsersService.saveOrUpdate(users);

            if (result) {
                return MsgBean.ok("操作成功！");
            } else {
                return MsgBean.error("操作失败！");
            }
        }
    }


    @PostMapping("/delete")
    @ApiOperation("删除后台用户")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "删除后台用户", response = MsgBean.class)})
    public MsgBean delete(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @ApiParam("id") @RequestParam(value = "id", required = false) Long id) {

        if (id == null || id <= 0) {
            return MsgBean.error("操作Id不能为空！");
        }

        SysUsers users = sysUsersService.getById(id);

        if (users == null) {
            return MsgBean.error("用户不存在！");
        }

        users.setState(-1);

        boolean result = sysUsersService.updateById(users);

        if (result) {
            return MsgBean.ok("操作成功！");
        } else {
            return MsgBean.error("操作失败！");
        }
    }


}

