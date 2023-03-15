package com.xzkj.flowacl.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzkj.flowacl.controller.bo.UserSearchBo;
import com.xzkj.flowacl.entity.AclUser;
import com.xzkj.flowacl.service.AclRoleService;
import com.xzkj.flowacl.service.AclUserService;
import com.xzkj.flowacl.utils.MsgBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LinMengHao
 * @since 2023-03-08
 */
@RestController
@RequestMapping("/flowacl/acl-user")
public class AclUserController {
    @Autowired
    private AclUserService userService;
    @Autowired
    private AclRoleService roleService;

    @GetMapping("{page}/{limit}")
    public MsgBean list(@PathVariable("page") Long page, @PathVariable("limit") Long limit, UserSearchBo bo){
        Page<AclUser> page1= new Page<>(page,limit);
        QueryWrapper<AclUser> wrapper=new QueryWrapper<>();

        return MsgBean.ok();
    }

}

