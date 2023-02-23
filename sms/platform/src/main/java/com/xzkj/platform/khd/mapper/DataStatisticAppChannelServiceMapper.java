package com.xzkj.platform.khd.mapper;


import com.xzkj.platform.khd.domain.DataStatisticAppChannelVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author WHY
 * @date 2022年03月06日 19:48
 */
public interface DataStatisticAppChannelServiceMapper {

    //首页-月统计
    List<DataStatisticAppChannelVo> getMonthStatistics(@Param("companyId")Long companyId);
}
