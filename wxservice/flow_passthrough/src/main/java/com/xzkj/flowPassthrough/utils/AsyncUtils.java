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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

//异步请求管理工具
@Slf4j
@Component
public class AsyncUtils {
    @Autowired
    OrderPassService orderPassService;
    @Autowired
    ProductFlowService productFlowService;

    @Async("sendPoolTaskExecutor")
    public void insert(JSONObject json) {
        OrderPass orderPass=JSONObject.parseObject(json.toJSONString(), OrderPass.class);
       int i= orderPassService.insert(orderPass);
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

        UpdateWrapper<OrderPass> objectUpdateWrapper = new UpdateWrapper<OrderPass>();
        objectUpdateWrapper.eq("orderNo",orderno);
        objectUpdateWrapper.eq("saleorderno",sale_orderno);
        objectUpdateWrapper.eq("uid",uid);
        objectUpdateWrapper.set("charge_state",charge_state);
        objectUpdateWrapper.set("state_msg",state_msg);
        objectUpdateWrapper.set("usr_name",usr_name);
        boolean update = orderPassService.update(null, objectUpdateWrapper);
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
