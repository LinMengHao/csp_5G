package com.xzkj.accessService.filter;


import com.xzkj.accessService.constants.Keys;
import com.xzkj.accessService.utils.RSAUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤鉴权不通过请求
 */

public class VerifyFilter implements Filter {
    @Autowired
    Keys keys;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //判断用户是否有效
        System.out.println("进入过滤器");
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        boolean verify = verify(request);
        if (verify){
            filterChain.doFilter(request,response);
        }else {
            response.setContentType("Application/json;charset=UTF-8");
            response.getWriter().write("登录失败...");
            return;
        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    //鉴权工具
    //TODO 存在问题：对方给的可能不是pkcs8格式的公钥，可能是pkcs1格式的，需要通过openssl来做转化
    public boolean verify(HttpServletRequest request){
        boolean b=false;
        try{
            String authorization = request.getHeader("Authorization").split(" ")[1];
            String timestamp = request.getHeader("Timestamp");
            String requestId = request.getHeader("Request-ID");
            String appId = request.getHeader("App-ID");
            //通过appid找到token，先写死测试，后面可以使用名单这种方式
            String token="b4e10cf1b467e25247400a454c5099971448aeea798921dd94524af25224ba82";
            String sign=token+timestamp+requestId;
            b = RSAUtils.verifySign(keys.getYdyyPublicKey(), sign, authorization);
        }catch (Exception e){
            b=false;
        }finally {
            return b;
        }


    }
}
