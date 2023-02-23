package com.xzkj.flowStore.controller.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HomeStatisticsVo {

    @ApiModelProperty("总订单数据")
    private Integer totalOrder;

    @ApiModelProperty("总支付订单数")
    private Integer totalPaidOrder;

    @ApiModelProperty("总用户数据")
    private Integer totalUser;

    @ApiModelProperty("总会员数")
    private Integer totalVipUser;
}
