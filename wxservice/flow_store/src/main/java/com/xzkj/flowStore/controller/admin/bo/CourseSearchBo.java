package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CourseSearchBo extends BaseBo {

    @ApiModelProperty("user_id")
    private Long userId;

    @ApiModelProperty("课程分类ID")
    private Long courseClassifyId;

    @ApiModelProperty("课程名称")
    private String title;

    @ApiModelProperty("状态：-1-删除，0-审核中，1-审核通过，2-审核失败，3-已下线")
    private Integer state;
}
