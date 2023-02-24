package com.xzkj.flowPassthrough.utils;

import java.util.HashMap;
import java.util.Map;
public class R {
    //是否成功
    private Boolean success;
    //状态吗
    private String code;
    //返回消息
    private String message;
    //返回token
    private String token;
    //返回数据
    private Map<String,Object> data=new HashMap<>();
    /*
        构造方法私有，防止返回值被串改，只能通过静态获取，保护属性
     */
    private R(){};

    public static R ok(){
        R r=new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMessage(ResultCode.SUCCESS.getMessage());
        return r;
    }

    public static R error(){
        R r=new R();
        r.setSuccess(false);
        r.setCode(ResultCode.COMMON_FAIL.getCode());
        r.setMessage(ResultCode.COMMON_FAIL.getMessage());
        return r;
    }

    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public R code(String code){
        this.setCode(code);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }
    public R token(String token){
        this.setToken(token);
        return this;
    }

    public R data(Map<String, Object>map){
        this.setData(map);
        return this;
    }

    public R data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
