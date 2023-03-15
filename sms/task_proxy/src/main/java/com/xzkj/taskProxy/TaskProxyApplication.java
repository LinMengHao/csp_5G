package com.xzkj.taskProxy;


import com.xzkj.taskProxy.sms.constants.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskProxyApplication {

    public static void main(String[] args) {

        SpringApplication.run(TaskProxyApplication.class, args);

        //数据库
        new ThreadDataBase().start();
        //加载用户信息进redis缓存
        new ThreadAppBase().start();

        //视频彩信模板处理
        new ThreadModelMaterial().start();
        new ThreadSignMaterial().start();

        //视频彩信发送路由预处理
        new ThreadSendPretreat().start();
        //视频短信补呼路由预处理
        //new ThreadNoticeRecall().start();

        //视频彩信通道侧模板处理
        new ThreadModelToChannel().start();

        new ThreadSignToChannel().start();
        //视频彩信模版客户回调通知
        new ThreadModelCallBack().start();
        //视频彩信模版客户回调补呼
        new ThreadModelCallbackMore().start();

        new ThreadSignCallBack().start();

        new ThreadSignCallbackMore().start();

        new ThreadModelQuery().start();

        //客户回调通知
        new ThreadCallBack().start();
        //回调补呼
        new ThreadCallbackMore().start();

        //上行回调
        new ThreadMoCallBack().start();
        new ThreadMoCallbackMore();

        //api重推客户状态报告
        new ThreadPushReport().start();
    }

}
