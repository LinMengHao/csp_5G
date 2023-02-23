package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CourseChapterSearchBo extends BaseBo {

    @ApiModelProperty("课程Id")
    private Long courseId;

    @ApiModelProperty("章节名称")
    private String title;

    @ApiModelProperty("状态：-1-删除，0-未开始，1-已开始，2-已结束")
    private Integer state;
}
