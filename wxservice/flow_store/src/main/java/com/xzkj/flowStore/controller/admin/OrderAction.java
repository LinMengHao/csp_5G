package com.xzkj.flowStore.controller.admin;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import com.xzkj.flowStore.controller.admin.bo.OrderSearchBo;
import com.xzkj.flowStore.controller.admin.vo.OrderVo;
import com.xzkj.flowStore.entity.Order;
import com.xzkj.flowStore.service.OrderService;
import com.xzkj.flowStore.service.UserService;
import com.xzkj.flowStore.utils.MsgBean;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "006.订单")
@RestController
@RequestMapping("/admin/order")
public class OrderAction {


    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/list")
    @ApiOperation("订单列表")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "订单列表", response = Order.class)})
    public MsgBean save(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody OrderSearchBo bo) {
        if (bo == null) {
            bo = new OrderSearchBo();
        }

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(bo.getOrderNo()), "order_no", bo.getOrderNo());
        wrapper.eq(StrUtil.isNotBlank(bo.getOutOrderNo()), "out_order_no", bo.getOutOrderNo());
        wrapper.eq(bo.getPayType() != null, "pay_type", bo.getPayType());
        wrapper.eq(bo.getState() != null, "state", bo.getState());
        wrapper.orderByDesc("id");

        IPage<Order> page = new Page<>(bo.getPageNo(), bo.getPageSize());
        orderService.page(page, wrapper);

        List<OrderVo> voList = Lists.newArrayList();

        for (Order order : page.getRecords()) {
            OrderVo vo = new OrderVo();
            BeanUtil.copyProperties(order, vo);
            vo.setUser(userService.getById(order.getUserId()));
            voList.add(vo);
        }

        IPage<OrderVo> voIPage = new Page<>();
        voIPage.setSize(page.getSize());
        voIPage.setPages(page.getPages());
        voIPage.setTotal(page.getTotal());
        voIPage.setRecords(voList);
        return MsgBean.ok().putData(voIPage);
    }

}
