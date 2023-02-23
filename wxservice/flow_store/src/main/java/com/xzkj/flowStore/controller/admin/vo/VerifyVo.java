package com.xzkj.flowStore.controller.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VerifyVo {

    @ApiModelProperty("ID")
    private Long id;
    /**
     * 真实姓名
     */
    @ApiModelProperty("真实姓名")
    private String name;

    /**
     * 身份证号
     */
    @ApiModelProperty("身份证号")
    private String idcard;

    /**
     * 身份证正面
     */
    @ApiModelProperty("身份证正面")
    private String idcardPic1;

    /**
     * 身份证反面
     */
    @ApiModelProperty("身份证反面")
    private String idcardPic2;

    /**
     * 身份证审核状态：0-审核中，1-认证成功，2-认证失败
     */
    @ApiModelProperty("身份证审核状态：0-审核中，1-认证成功，2-认证失败")
    private Integer idcardAuditState;

    @ApiModelProperty("身份证审核备注")
    private Integer idcardAuditRemark;

}
