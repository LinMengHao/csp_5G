package com.xzkj.accessService.constants;

//接受http响应返回值
public class ResponseUtil {
    //返回信息
    private String message;
    //状态值
    private String code;
    //返回审核身份后的token
    private String token;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
