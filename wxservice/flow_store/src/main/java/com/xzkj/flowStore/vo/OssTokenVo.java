package com.xzkj.flowStore.vo;

import com.xzkj.flowStore.common.Constant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OssTokenVo {
    @ApiModelProperty("Android/iOS移动应用初始化OSSClient获取的 AccessKey ID。")
    private String accessKeyId;
    @ApiModelProperty("Android/iOS移动应用初始化OSSClient获取AccessKey Secret")
    private String accessKeySecret;
    @ApiModelProperty("Android/iOS移动应用初始化的Token。")
    private String securityToken;
    @ApiModelProperty("该Token失效的时间。Android SDK会自动判断Token是否失效，如果失效，则自动获取Token")
    private String expiration;
    @ApiModelProperty("host")
    private String host = Constant.FILE_HOST;
}
