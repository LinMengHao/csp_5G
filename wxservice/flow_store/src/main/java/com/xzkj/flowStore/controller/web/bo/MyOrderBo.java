package com.xzkj.flowStore.controller.web.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MyOrderBo {


    @ApiModelProperty("状态：，0-未支付，1-已支付，2-已过期，3-已退款")
    private Integer state;


    @ApiModelProperty("同步状态：0-未同步，1-同步中，2-同步成功，3-同步失败")
    private Integer syncState;
}
