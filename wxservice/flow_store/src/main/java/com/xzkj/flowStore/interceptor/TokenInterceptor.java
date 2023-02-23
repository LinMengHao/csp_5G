package com.xzkj.flowStore.interceptor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xzkj.flowStore.entity.User;
import com.xzkj.flowStore.service.LoginDayLogService;
import com.xzkj.flowStore.service.UserService;
import com.xzkj.flowStore.utils.MsgBean;
import com.xzkj.flowStore.utils.RedisUtil;
import com.xzkj.flowStore.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * 拦截token
 *
 * @author dashan
 * @date 2019/4/23 9:52 AM
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {


    @Autowired
    private UserService userService;


    @Autowired
    private LoginDayLogService loginDayLogService;

    @Autowired
    private RedisUtil redisUtil;

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


        String uri = request.getRequestURI();

        String token = request.getHeader("token");

        MsgBean msg = null;

        List<String> urlList = Lists.newArrayList();
        urlList.add("/web/phone/save");


        for (String url : urlList) {
            if (uri.equals(url) && StringUtils.isBlank(token)) {
                msg = MsgBean.error(505, "Token验证失败！");
            }
        }

        //判断哪些路径必须有token
//        if (uri.contains("save")  || uri.contains("newOrder") || uri.contains("getCenterInfo")) {
//            if (StringUtils.isBlank(token)) {
//                msg = MsgBean.error(505, "Token验证失败！");
//            }
//        }

        //如果token不为空，则校验token
        if (StringUtils.isNotBlank(token)) {
            Long uid = TokenUtil.getUIDFromToken(token);
//            log.info("token拦截器 token = {}, userId={}", token, uid);
            if (uid == null || uid <= 0) {
                msg = MsgBean.error(505, "Token验证失败！");
            } else {
                loginDayLogService.addLog(uid);
                User user = userService.getById(uid);
                if (user == null || user.getState() == -1) {
                    msg = MsgBean.error(505, "Token验证失败！");
                } else if (!TokenUtil.checkToken(user, token)) {
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

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
