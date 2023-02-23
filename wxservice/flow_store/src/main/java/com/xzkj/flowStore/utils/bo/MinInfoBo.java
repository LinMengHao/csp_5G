package com.xzkj.flowStore.utils.bo;

import lombok.Data;

@Data
public class MinInfoBo {
    private Integer errcode;
    private String errmsg;
    private String openid;
    private String session_key;
    private String unionid;
}
