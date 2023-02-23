package com.xzkj.flowStore.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.xzkj.flowStore.entity.SysUsers;
import com.xzkj.flowStore.service.SysUsersService;
import com.xzkj.flowStore.utils.MsgBean;
import com.xzkj.flowStore.utils.TokenUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUsersService sysUsersService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With,Content-Type,XFILENAME,XFILECATEGORY,XFILESIZE,token");

        response.setHeader("Access-Control-Allow-Credentials", "true");


        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpStatus.SC_OK);
            response.getWriter().write("OPTIONS returns OK");
            return true;
        }

//        if (1 == 1) {
//            return true;
//        }

        String uri = request.getRequestURI();

        String token = request.getHeader("token");

        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }

        MsgBean msg = null;

        if (StringUtils.isBlank(token) && uri.contains("/admin/sysUsers/login")) {
            return true;
        } else {
            Long uid = TokenUtil.getUIDFromToken(token);
            if (uid == null || uid <= 0) {
                msg = MsgBean.error(505, "Token验证失败！");
            } else {
                SysUsers users = sysUsersService.getById(uid);

                if (users == null || users.getState() == -1) {
                    msg = MsgBean.error(505, "Token验证失败！");
                } else if (!TokenUtil.checkToken(users, token)) {
                    msg = MsgBean.error(505, "Token验证失败！");
                }
            }
        }

        if (msg != null) {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(JSON.toJSONString(msg));
            return false;
        }

        return true;
    }
}
