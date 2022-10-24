package com.xzkj.smsService.service.impl;

import com.xzkj.smsService.entity.TStudent;
import com.xzkj.smsService.mapper.TStudentMapper;
import com.xzkj.smsService.service.TStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-18
 */
@Service
public class TStudentServiceImpl extends ServiceImpl<TStudentMapper, TStudent> implements TStudentService {
    @Autowired
    TStudentMapper mapper;

    @Override
    public List<TStudent> findAll() {
       List<TStudent> list= mapper.findAll();
        return list;
    }
}
