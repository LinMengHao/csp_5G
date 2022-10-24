package com.xzkj.base.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor//自动生成无参数构造
@AllArgsConstructor//自动生成有参数构造
public class LmhException extends RuntimeException{
    private String code;//状态码
    private String msg;//异常信息
}
