package com.xzkj.flowPassthrough.service.impl;

import com.xzkj.flowPassthrough.entity.OrderPass;
import com.xzkj.flowPassthrough.mapper.OrderPassMapper;
import com.xzkj.flowPassthrough.service.OrderPassService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LinMengHao
 * @since 2023-02-24
 */
@Service
public class OrderPassServiceImpl extends ServiceImpl<OrderPassMapper, OrderPass> implements OrderPassService {
    @Autowired
    OrderPassMapper orderPassMapper;
    @Override
    public int insert(OrderPass orderPass) {
        return orderPassMapper.insert(orderPass);
    }
}
