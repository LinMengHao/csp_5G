package com.xzkj.flowStore.utils.wx;

import lombok.Data;

@Data
public class Ticket {
    private String ticket;//获取到的凭证
    private Long expiresIn;//凭证有效时间,单位：秒

}
