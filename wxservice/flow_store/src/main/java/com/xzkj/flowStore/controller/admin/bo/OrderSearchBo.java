package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderSearchBo extends BaseBo {

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("支付宝、微信交易号")
    private String outOrderNo;

    @ApiModelProperty("支付方式：0-未知，1-支付宝，2-微信")
    private Integer payType;

    @ApiModelProperty("订单状态：- 1-已删除，0-未支付，1-已支付，2-已过期，3-已退款")
    private Integer state;


}
