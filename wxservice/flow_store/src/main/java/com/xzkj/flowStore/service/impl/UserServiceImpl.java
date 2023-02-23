package com.xzkj.flowStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.flowStore.entity.User;
import com.xzkj.flowStore.mapper.UserMapper;
import com.xzkj.flowStore.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User getByMinOpenId(String openid) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("min_open_id", openid);
        wrapper.ge("state", 0);
        wrapper.last("limit 1");
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public User getByUnionId(String unionId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("union_id", unionId);
        wrapper.ge("state", 0);
        wrapper.last("limit 1");
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public User createWxMinNewUser(String openid, String unionId, String sessionKey) {
        User users = new User();
        users.setMinOpenId(openid);
        users.setUnionId(unionId);
        users.setMinSessionKey(sessionKey);
        baseMapper.insert(users);
        return users;
    }

    @Override
    public User getByPhone(String phone) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        wrapper.ge("state", 0);
        wrapper.last("limit 1");
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void updateSessionKey(Long id, String session_key) {
        User user = new User();
        user.setId(id);
        user.setMinSessionKey(session_key);
        baseMapper.updateById(user);
    }

    @Override
    public synchronized String getAutoNick() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        wrapper.last("limit 1");
        User user = baseMapper.selectOne(wrapper);
        if (user == null) {
            return "星际001";
        } else {
            return "星际" + (user.getId() + 1);
        }
    }
}
