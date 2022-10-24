package com.xzkj.smsService.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xzkj.smsService.entity.TUser;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-13
 */
public interface TUserService extends IService<TUser> {

    List<TUser> findAll();
}
