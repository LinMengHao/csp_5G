package com.xzkj.smsService.service;

import com.xzkj.smsService.entity.TStudent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-18
 */
public interface TStudentService extends IService<TStudent> {

    List<TStudent> findAll();
}
