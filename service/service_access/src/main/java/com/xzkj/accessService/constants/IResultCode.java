package com.xzkj.accessService.constants;

public interface IResultCode {
    /**
     * 获取错误时状态码
     * @return
     */
    String getCode();

    /**
     * 获取错误时信息
     * @return
     */
    String getMessage();
}
