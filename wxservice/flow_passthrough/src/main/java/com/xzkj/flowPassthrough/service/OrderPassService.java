package com.xzkj.flowPassthrough.service;

import com.xzkj.flowPassthrough.entity.OrderPass;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LinMengHao
 * @since 2023-02-24
 */
public interface OrderPassService extends IService<OrderPass> {

    int insert(OrderPass orderPass);

    boolean updateByTableName(OrderPass orderPass);
}
