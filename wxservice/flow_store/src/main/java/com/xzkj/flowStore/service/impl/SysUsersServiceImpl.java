package com.xzkj.flowStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.flowStore.controller.admin.bo.LoginBo;
import com.xzkj.flowStore.entity.SysUsers;
import com.xzkj.flowStore.mapper.SysUsersMapper;
import com.xzkj.flowStore.service.SysUsersService;
import com.xzkj.flowStore.utils.MD5Util;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author dashan
 * @since 2019-03-31
 */
@Service
public class SysUsersServiceImpl extends ServiceImpl<SysUsersMapper, SysUsers> implements SysUsersService {


    @Override
    public SysUsers getByEmail(String email) {

        QueryWrapper<SysUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email.trim());
        wrapper.eq("state", 0);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public SysUsers getByPhone(String phone) {
        QueryWrapper<SysUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("email", phone.trim());
        wrapper.eq("state", 0);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public SysUsers login(LoginBo loginBo) {
        QueryWrapper<SysUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("state", 0);
        wrapper.and(i -> i.eq("name", loginBo.getLoginName()).or().eq("email", loginBo.getLoginName()).or().eq("phone", loginBo.getLoginName()));
        SysUsers users = baseMapper.selectOne(wrapper);

        if ( users == null ) {
            return null;
        }

        if ( !users.getPassword().equals(MD5Util.MD5(loginBo.getPassword())) ) {
            return null;
        }
        return users;
    }
}
