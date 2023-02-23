package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FeedbackSearchBo extends BaseBo {

    @ApiModelProperty("类型：0-建议，1-投诉")
    private Integer type;
    

}
