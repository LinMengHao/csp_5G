package com.xzkj.flowPassthrough.mapper;

import com.xzkj.flowPassthrough.entity.OrderPass;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author LinMengHao
 * @since 2023-02-24
 */
public interface OrderPassMapper extends BaseMapper<OrderPass> {

    int insertByTableName(OrderPass orderPass);

    boolean updateByTableName(OrderPass orderPass);
}
