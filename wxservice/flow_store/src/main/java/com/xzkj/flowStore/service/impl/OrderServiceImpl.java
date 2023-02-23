package com.xzkj.flowStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.flowStore.entity.Order;
import com.xzkj.flowStore.entity.Product;
import com.xzkj.flowStore.entity.UserRights;
import com.xzkj.flowStore.mapper.OrderMapper;
import com.xzkj.flowStore.service.OrderService;
import com.xzkj.flowStore.service.ProductService;
import com.xzkj.flowStore.service.UserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private UserRightsService userRightsService;

    @Autowired
    private ProductService productService;

    @Override
    public boolean paySuccess(String orderNo, String outTradeNo, int billAmount) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        Order order = baseMapper.selectOne(wrapper);

        if (order == null) {
            log.error("支付成功后，数据库中未找到数据 {}", orderNo);
            return false;
        } else if (order.getState() != 0) {
            log.error("支付成功后，订单状态不等于0 {}", orderNo);
            return false;
        } else {
            Order update = new Order();
            update.setId(order.getId());
            update.setOutOrderNo(outTradeNo);
            update.setBillAmount(billAmount);
            update.setPaidTime(LocalDateTime.now());
            update.setState(1);
            int count = baseMapper.updateById(update);

            if (count > 0) {
                //增加权益
                Product product = productService.getById(order.getProductId());
                LocalDateTime now = LocalDateTime.now();

                UserRights rights = new UserRights();
                rights.setUserId(order.getUserId());
                rights.setProductId(order.getProductId());
                rights.setStartDate(now);
                if (product.getDayType() == 1) {
                    rights.setEndDate(now.plusDays(product.getDay()));
                } else if (product.getDayType() == 2) {
                    rights.setEndDate(now.plusYears(product.getDay()));
                }
                userRightsService.save(rights);
                return true;
            }
            return false;
        }
    }
}
