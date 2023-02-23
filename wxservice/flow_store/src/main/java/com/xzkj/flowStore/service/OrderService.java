package com.xzkj.flowStore.service;

import com.xzkj.flowStore.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
public interface OrderService extends IService<Order> {

    boolean paySuccess(String orderNo, String outTradeNo, int intValue);
}
