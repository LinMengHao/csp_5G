package com.xzkj.flowStore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xzkj.flowStore.controller.admin.bo.LoginBo;
import com.xzkj.flowStore.entity.SysUsers;

/**
 * <p>
 * 后台用户表 服务类
 * </p>
 *
 * @author dashan
 * @since 2019-03-31
 */
public interface SysUsersService extends IService<SysUsers> {


    SysUsers getByEmail(String email);

    SysUsers getByPhone(String phone);


    SysUsers login(LoginBo loginBo);
}
