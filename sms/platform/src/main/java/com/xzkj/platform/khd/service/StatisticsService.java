package com.xzkj.platform.khd.service;

import com.xzkj.platform.khd.domain.Statistics;

import java.util.List;

public interface StatisticsService {

    public List<Statistics> findStatisticsList(Statistics statistics);
    public List<Statistics> findStatisticsListM(String date);

}
