package com.xzkj.smsService.controller;


import com.xzkj.smsService.annotation.DS;
import com.xzkj.smsService.constants.DataSourceConstants;
import com.xzkj.smsService.entity.TStudent;
import com.xzkj.smsService.service.TStudentService;
import com.xzkj.smsService.utils.RedisUtils;
import com.xzkj.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-18
 */
@Slf4j
@RestController
@RequestMapping("/smsService/t-student")
public class TStudentController {
    @Autowired
    TStudentService service;
    @Autowired
    RedisUtils redisUtils;

    @DS(value = DataSourceConstants.DS_KEY_SLAVE)
    @PostMapping(value = "findAll",consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public R test(){
        redisUtils.setCacheObject("lmh","yy", Duration.ofHours(1));
        List<TStudent> list= service.findAll();
        log.info(list.toString());
        log.info("缓存值："+redisUtils.getCacheObject("lmh"));
        return R.ok().data("list",list);
    }
}

