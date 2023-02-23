package com.xzkj.flowStore.utils.wx;

import lombok.Data;

@Data
public class WXShareVo {
    private String appId;
    private String nonceStr;
    private String signature;
    private Long timestamp;

}
