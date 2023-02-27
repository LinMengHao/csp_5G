package com.xzkj.flowPassthrough.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface ICommonMapper {

	/**
	 * 批量
	 * @param sqlList
	 */
	int batchUpdate(List<String> sqlList);

	List<Map<String, Object>> appInfoList();
	List<Map<String, Object>> blactGroupInfo(@Param("groupId")String groupId);
	List<Map<String, Object>> blactRelateList(@Param("groupId")String groupId);
	List<Map<String, Object>> modelInfoList();
	List<Map<String, Object>> channelInfoList();
	List<Map<String, Object>> routeBaseList();
	List<Map<String, Object>> tableInfoList(@Param("tableName")String tableName);
	int createNewTable(@Param("createSql")String createSql);
	List<Map<String, Object>> commonInfoList(@Param("selectSql")String selectSql);
	List<Map<String, Object>> segmentList();
	List<Map<String, Object>> statisticDay(Map<String, String> map);
	int insertOrUpdateStatistic(@Param("mapList")List<Map<String, Object>> mapList);

    List<Map<String, Object>> modelInfoNewList();

    Map<String, Object> signInfo(@Param("signId")String signId);
}
