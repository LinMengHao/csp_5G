package com.xzkj.aclService.controller;


import com.xzkj.aclService.entity.SysUser;
import com.xzkj.utils.R;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 部门表 前端控制器
 * </p>
 *
 * @author LinMengHao
 * @since 2022-11-02
 */
@RestController
@RequestMapping("/aclService/sys-dept")
public class SysDeptController {
    /**
     * 用户列表
     * @param page
     * @param limit
     * @param user
     * @return
     */
    @GetMapping("list/{page}/{limit}")
    public R list(@PathVariable Long page, @PathVariable Long limit, SysUser user){
        return R.ok();
    }
}

