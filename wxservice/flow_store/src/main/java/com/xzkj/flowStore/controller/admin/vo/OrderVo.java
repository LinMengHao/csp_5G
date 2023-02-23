package com.xzkj.flowStore.controller.admin.vo;

import com.xzkj.flowStore.entity.Order;
import com.xzkj.flowStore.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderVo extends Order {

    @ApiModelProperty("用户")
    private User user;
}
