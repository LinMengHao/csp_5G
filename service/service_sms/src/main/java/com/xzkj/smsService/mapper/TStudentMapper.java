package com.xzkj.smsService.mapper;

import com.xzkj.smsService.entity.TStudent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-18
 */
public interface TStudentMapper extends BaseMapper<TStudent> {

    List<TStudent> findAll();
}
