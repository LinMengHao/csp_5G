package com.xzkj.flowStore.service;

import com.xzkj.flowStore.entity.LoginDayLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户日登录日志 服务类
 * </p>
 *
 * @author dashan
 * @since 2019-05-23
 */
public interface LoginDayLogService extends IService<LoginDayLog> {


    /**
     * 添加日志
     * @param uid
     */
    void addLog(Long uid);
}
