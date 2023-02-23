package com.xzkj.flowStore.controller.web.vo;

import com.google.common.collect.Lists;
import com.xzkj.flowStore.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserVo extends User {

    @ApiModelProperty("会员权益")
    private List<UserRightsVo> userRightsList = Lists.newArrayList();
}
