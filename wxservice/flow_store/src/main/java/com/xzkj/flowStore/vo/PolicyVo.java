package com.xzkj.flowStore.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PolicyVo {

    @ApiModelProperty("accessIds")
    private String accessId;

    @ApiModelProperty("图片域名")
    private String host;

    @ApiModelProperty("凭证")
    private String policy;

    @ApiModelProperty("签名")
    private String signature;

    @ApiModelProperty("文件目录")
    private String dir;


    @ApiModelProperty("失效时间")
    private Long expire;

    @ApiModelProperty("回调")
    private String callback;
}
