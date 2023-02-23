package com.xzkj.flowStore.controller.web.vo;

import com.xzkj.flowStore.entity.Order;
import lombok.Data;

@Data
public class OrderVo extends Order {


    private String productService;

    private String productPic;
}
