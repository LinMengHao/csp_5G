package com.xzkj.platform.khd.mapper;


import com.xzkj.platform.khd.domain.Statistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StatisticsMapper {

    public List<Statistics> findStatisticsList(Statistics statistics);
//    public List<Statistics> findStatisticsListM(String date,Long companyId);

    public List<Statistics> findStatisticsListM(@Param("companyId")Long companyId, @Param("date")String date);

}
