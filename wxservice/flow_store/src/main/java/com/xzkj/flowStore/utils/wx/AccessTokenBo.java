package com.xzkj.flowStore.utils.wx;

import lombok.Data;

@Data
public class AccessTokenBo {
    private Integer errcode;
    private String errmsg;
    private String accessToken;
    private Long expiresIn;
}
