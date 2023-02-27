package com.xzkj.flowPassthrough.utils;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xzkj.flowPassthrough.entity.OrderPass;
import com.xzkj.flowPassthrough.entity.ProductFlow;
import com.xzkj.flowPassthrough.service.OrderPassService;
import com.xzkj.flowPassthrough.service.ProductFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

//异步请求管理工具
@Slf4j
@Component
public class AsyncUtils {
    @Autowired
    OrderPassService orderPassService;
    @Autowired
    ProductFlowService productFlowService;

    @Autowired
    RedisTemplate redisTemplate;

    @Async("sendPoolTaskExecutor")
    public void insert(JSONObject json) {
        OrderPass orderPass=JSONObject.parseObject(json.toJSONString(), OrderPass.class);
        String day = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime());
        String tableName="order_pass_"+day;
        //三天回调期限
        redisTemplate.opsForValue().set(orderPass.getSaleorderno(),tableName, Duration.ofHours(72L));
        orderPass.setLogDate(day);
        orderPass.setCreateTime(new Date());
        orderPass.setUpdateTime(new Date());
        int i= orderPassService.insert(orderPass);
        if(i>0){
            log.info("订购单号：{}, 入库表：{} 操做成功",orderPass.getSaleorderno(),tableName);
        }else {
            log.info("订购单号：{}, 入库表：{} 操做失败",orderPass.getSaleorderno(),tableName);
        }
    }


    @Async("sendPoolTaskExecutor")
    public void update(JSONObject json) {
        String orderno = json.containsKey("orderno") ? json.getString("orderno") : "";
        String sale_orderno = json.containsKey("sale_orderno") ? json.getString("sale_orderno") : "";
        String charge_state = json.containsKey("charge_state") ? json.getString("charge_state") : "0";
        String state_msg = json.containsKey("state_msg") ? json.getString("state_msg") : "充值失败";
        String usr_name = json.containsKey("usr_name") ? json.getString("usr_name") : "";
        String sales_returl = json.containsKey("sales_returl") ? json.getString("sales_returl") : "0";
        String uid = json.containsKey("uid") ? json.getString("uid") : "";
        String receive_time = json.containsKey("receive_time") ? json.getString("receive_time") : "";


        OrderPass orderPass=new OrderPass();
        orderPass.setOrderNo(orderno);
        orderPass.setSaleorderno(sale_orderno);
        orderPass.setChargeState(Integer.parseInt(charge_state));
        orderPass.setStateMsg(state_msg);
        orderPass.setUsrName(usr_name);
        orderPass.setUid(Long.parseLong(uid));
        String tableName = (String)redisTemplate.opsForValue().get(sale_orderno);
        if(!StringUtils.hasText(tableName)) {
            if (StringUtils.hasText(receive_time)) {
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(receive_time);
                    String day = new SimpleDateFormat("yyyyMMdd").format(date);
                    tableName = "order_pass_" + day;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            //TODO 极端情况下 需要遍历所以表，修改这个订单状态
        }
        orderPass.setTableName(tableName);
        orderPass.setUpdateTime(new Date());
        boolean update = orderPassService.updateByTableName(orderPass);
        if(update){
            log.info("订购单号：{}, 更新库表：{} 操做成功",orderPass.getSaleorderno(),tableName);
        }else {
            log.info("订购单号：{}, 更新库表：{} 操做失败",orderPass.getSaleorderno(),tableName);
        }


    }

    @Async("sendPoolTaskExecutor")
    public void product(JSONObject json) {
        String operator_name = json.containsKey("operator_name") ? json.getString("operator_name") : "";
        String pkgcode = json.containsKey("pkgcode") ? json.getString("pkgcode") : "";
        String pkgsize = json.containsKey("pkgsize") ? json.getString("pkgsize") : "0";
        String pkgstprice = json.containsKey("pkgstprice") ? json.getString("pkgstprice") : "0";
        String province_name = json.containsKey("province_name") ? json.getString("province_name") : "";
        String areatypename = json.containsKey("areatypename") ? json.getString("areatypename") : "";
        String uid = json.containsKey("uid") ? json.getString("uid") : "";

        QueryWrapper<ProductFlow> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid);
        wrapper.eq("pkgcode",pkgcode);
        wrapper.eq("pkgsize",pkgsize);
        wrapper.eq("province_name",province_name);
        wrapper.eq("areatypename",areatypename);
        wrapper.eq("pkgstprice",pkgstprice);
        wrapper.eq("operator_name",operator_name);
        List<ProductFlow> list = productFlowService.list(wrapper);
        if(list==null || list.size()<=0){
            //插入
            ProductFlow productFlow=new ProductFlow();
            productFlow.setUid(Long.parseLong(uid));
            productFlow.setPkgcode(pkgcode);
            productFlow.setPkgsize(Double.parseDouble(pkgsize));
            productFlow.setProvinceName(province_name);
            productFlow.setPkgstprice(BigDecimal.valueOf(Double.parseDouble(pkgstprice)));
            productFlow.setAreatypename(areatypename);
            productFlow.setOperatorName(operator_name);
            boolean save = productFlowService.save(productFlow);
        }

    }
}
