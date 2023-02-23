package com.xzkj.flowStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.flowStore.entity.PhoneRecord;
import com.xzkj.flowStore.mapper.PhoneRecordMapper;
import com.xzkj.flowStore.service.PhoneRecordService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 手机记录表 服务实现类
 * </p>
 *
 * @author dashan
 * @since 2020-09-27
 */
@Service
public class PhoneRecordServiceImpl extends ServiceImpl<PhoneRecordMapper, PhoneRecord> implements PhoneRecordService {

    @Override
    public PhoneRecord getByPhone(String phone) {
        QueryWrapper<PhoneRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        wrapper.eq("state", 0);
        wrapper.last("limit 1");
        return baseMapper.selectOne(wrapper);
    }
}
