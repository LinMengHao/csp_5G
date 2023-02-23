package com.xzkj.taskPorxy.service.impl;


import com.xzkj.taskPorxy.mapper.ICommonMapper;
import com.xzkj.taskPorxy.service.ICommonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CommonServiceImpl implements ICommonService {
	
    @Resource
    private ICommonMapper voiceMapper ;
    
	@Override
	public void batchUpdate(List<String> sqlList){
		/*Map<String,Object> param = new HashMap<String,Object>();
		param.put("list", sqlList);*/
		voiceMapper.batchUpdate(sqlList);
	}
	
	@Override
	public List<Map<String, Object>> appInfoList(){
		return voiceMapper.appInfoList();
	}
	@Override
	public List<Map<String, Object>> blactRelateList(String groupId){
		return voiceMapper.blactRelateList(groupId);
	}
	@Override
	public List<Map<String, Object>> blactGroupInfo(String groupId){
		return voiceMapper.blactGroupInfo(groupId);
	}
	@Override
	public List<Map<String, Object>> modelInfoList(){
		return voiceMapper.modelInfoList();
	}
	@Override
	public List<Map<String, Object>> channelInfoList(){
		return voiceMapper.channelInfoList();
	}
	@Override
	public List<Map<String, Object>> routeBaseList(){
		return voiceMapper.routeBaseList();
	}
	@Override
	public List<Map<String, Object>> tableInfoList(String tableName){
		return voiceMapper.tableInfoList(tableName);
	}
	@Override
	public void createNewTable(String createSql){
		voiceMapper.createNewTable(createSql);
	}
	@Override
	public List<Map<String, Object>> commonInfoList(String selectSql){
		return voiceMapper.commonInfoList(selectSql);
	}
	@Override
	public List<Map<String, Object>> segmentList(){
		return voiceMapper.segmentList();
	}

	@Override
	public int statisticDayOne(Map<String, String> map) {
		List<Map<String, Object>> statisticsList = voiceMapper.statisticDay(map);
		int i = statisticsList.size();
		if (statisticsList.size() > 0) {
			int result = voiceMapper.insertOrUpdateStatistic(statisticsList);
			if (result <= 0) {
				i = 0;
			}
		}
		return i;
	}

	@Override
	public List<Map<String, Object>> modelInfoNewList() {
		return voiceMapper.modelInfoNewList();
	}

	@Override
	public Map<String, Object> signInfo(String sign_id) {
		return voiceMapper.signInfo(sign_id);
	}
}
