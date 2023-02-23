package com.xzkj.flowStore.tasks;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xzkj.flowStore.entity.Order;
import com.xzkj.flowStore.entity.Product;
import com.xzkj.flowStore.entity.User;
import com.xzkj.flowStore.service.OrderService;
import com.xzkj.flowStore.service.ProductService;
import com.xzkj.flowStore.service.UserService;
import com.xzkj.flowStore.utils.DingdingUtil;
import com.xzkj.flowStore.utils.XingJiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BanUserTask {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Scheduled(cron = "*/5 * * * * ?")
    public void banUser() {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("state", 1);
        wrapper.eq("sync_state", 0);
        List<Order> list = orderService.list(wrapper);

        for (Order order : list) {
            User user = userService.getById(order.getUserId());
            if (user == null || user.getState() == -1 || StrUtil.isBlank(user.getXjUserId())) {
                continue;
            }
            Product product = productService.getById(order.getProductId());

            //0-未知，1-支付宝，2-微信
            //TODO 判断支付类型
            int terminalType = 3;

            //TODO 判断支付类型
            int payType = 2;  //支付方式：1：支付宝 2：微信

            boolean result = XingJiUtil.syncOrder(user.getXjUserId(), order.getAmount(), product.getDay(), order.getIp(), terminalType, payType,
                    product.getCode(), order.getId(), order.getOutOrderNo(), LocalDateTimeUtil.format(order.getPaidTime(), DatePattern.NORM_DATETIME_PATTERN));
            log.info("定时任务同步订单,order={},result={}", order, result);
            Order update = new Order();
            update.setId(order.getId());
            update.setSyncState(result ? 1 : 3);  //同步状态：0-未同步，1-同步中，2-同步成功，3-同步失败
            orderService.updateById(update);
            if (!result) {
                DingdingUtil.sendMsg("同步订单失败：" + order.getId());
            }
        }
    }
}
