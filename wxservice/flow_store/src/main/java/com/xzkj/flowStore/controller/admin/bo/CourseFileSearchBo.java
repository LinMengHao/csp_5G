package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CourseFileSearchBo extends BaseBo {

    @ApiModelProperty("课程id")
    private Long courseId;
    
}
