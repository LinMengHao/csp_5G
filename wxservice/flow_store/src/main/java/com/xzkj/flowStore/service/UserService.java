package com.xzkj.flowStore.service;

import com.xzkj.flowStore.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
public interface UserService extends IService<User> {

    User getByMinOpenId(String openid);

    User getByUnionId(String unionid);

    User createWxMinNewUser(String openid, String unionid,String sessionKey);

    User getByPhone(String phone);

    void updateSessionKey(Long id, String session_key);

    String getAutoNick();
}
