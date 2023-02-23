package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewOrderBo {

    @ApiModelProperty("商品id")
    private Long productId;

    @ApiModelProperty("支付金额")
    private Long amount;

    @ApiModelProperty("支付方式：1-支付H5，2-微信内支付，3-微信h5")
    private Integer payType;

}
