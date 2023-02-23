package com.xzkj.flowStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xzkj.flowStore.entity.LoginDayLog;
import com.xzkj.flowStore.mapper.LoginDayLogMapper;
import com.xzkj.flowStore.service.LoginDayLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 用户日登录日志 服务实现类
 * </p>
 *
 * @author dashan
 * @since 2019-05-23
 */
@Service
public class LoginDayLogServiceImpl extends ServiceImpl<LoginDayLogMapper, LoginDayLog> implements LoginDayLogService {


    @Override
    public void addLog(Long uid) {

        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("yyyyMMdd");

        long date = Long.parseLong(LocalDate.now().format(f1));

        QueryWrapper<LoginDayLog>  wrapper = new QueryWrapper<>();
        wrapper.eq("date", date);
        wrapper.eq("user_id",uid);
        wrapper.last(" limit 1 ");

        LoginDayLog loginDayLog = baseMapper.selectOne(wrapper);

        if (loginDayLog == null ){
            loginDayLog = new LoginDayLog();
            loginDayLog.setUserId(uid);
            loginDayLog.setDate(date);
            baseMapper.insert(loginDayLog);
        }
    }
}
