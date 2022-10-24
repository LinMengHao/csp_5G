package com.xzkj.smsService.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xzkj.smsService.entity.TUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-13
 */
@Mapper
public interface TUserMapper extends BaseMapper<TUser> {

    List<TUser> findAll();

}
