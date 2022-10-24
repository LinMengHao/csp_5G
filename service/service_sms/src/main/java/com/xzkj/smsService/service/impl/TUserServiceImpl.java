package com.xzkj.smsService.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.smsService.entity.TUser;
import com.xzkj.smsService.mapper.TUserMapper;
import com.xzkj.smsService.service.TUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-13
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements TUserService {
    @Autowired
    TUserMapper mapper;


    @Override
    public List<TUser> findAll() {
        List<TUser> list=mapper.findAll();
        return list;
    }
}
