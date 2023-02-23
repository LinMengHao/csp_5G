package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.operator.domain.BlackRelated;
import com.xzkj.platform.operator.mapper.BlackRelatedMapper;
import com.xzkj.platform.operator.service.IBlackRelatedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 黑名单组关系Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Service
public class BlackRelatedServiceImpl implements IBlackRelatedService 
{
    @Autowired
    private BlackRelatedMapper blackRelatedMapper;

    /**
     * 查询黑名单组关系
     * 
     * @param id 黑名单组关系主键
     * @return 黑名单组关系
     */
    @Override
    public BlackRelated selectBlackRelatedById(Long id)
    {
        return blackRelatedMapper.selectBlackRelatedById(id);
    }

    /**
     * 查询黑名单组关系列表
     * 
     * @param blackRelated 黑名单组关系
     * @return 黑名单组关系
     */
    @Override
    public List<BlackRelated> selectBlackRelatedList(BlackRelated blackRelated)
    {
        return blackRelatedMapper.selectBlackRelatedList(blackRelated);
    }

    /**
     * 新增黑名单组关系
     * 
     * @param blackRelated 黑名单组关系
     * @return 结果
     */
    @Override
    public int insertBlackRelated(BlackRelated blackRelated)
    {
        blackRelated.setUpdateTime(DateUtils.getNowDate());
        blackRelated.setCreateTime(DateUtils.getNowDate());
        return blackRelatedMapper.insertBlackRelated(blackRelated);
    }
    @Override
    public int insertBlackRelated(Long groupId,List<BlackRelated> channelList)
    {

        for (BlackRelated blackRelated:channelList) {
            blackRelated.setRiskHigh(blackRelated.getRiskHigh()==null?2:blackRelated.getRiskHigh());
            blackRelated.setRiskMedium(blackRelated.getRiskMedium()==null?2:blackRelated.getRiskMedium());
            blackRelated.setRiskLow(blackRelated.getRiskLow()==null?2:blackRelated.getRiskLow());
            blackRelated.setRiskPrivate(blackRelated.getRiskPrivate()==null?2:blackRelated.getRiskPrivate());
            blackRelated.setGroupId(groupId);
            blackRelated.setUpdateTime(DateUtils.getNowDate());
            blackRelated.setCreateTime(DateUtils.getNowDate());
        }
        return blackRelatedMapper.insertBlackRelateds(channelList);
    }
    /**
     * 修改黑名单组关系
     * 
     * @param blackRelated 黑名单组关系
     * @return 结果
     */
    @Override
    public int updateBlackRelated(BlackRelated blackRelated)
    {
        blackRelated.setUpdateTime(DateUtils.getNowDate());
        return blackRelatedMapper.updateBlackRelated(blackRelated);
    }

    /**
     * 批量删除黑名单组关系
     * 
     * @param ids 需要删除的黑名单组关系主键
     * @return 结果
     */
    @Override
    public int deleteBlackRelatedByIds(String ids)
    {
        return blackRelatedMapper.deleteBlackRelatedByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除黑名单组关系信息
     * 
     * @param id 黑名单组关系主键
     * @return 结果
     */
    @Override
    public int deleteBlackRelatedById(Long id)
    {
        return blackRelatedMapper.deleteBlackRelatedById(id);
    }
    @Override
    public int deleteBlackRelatedByGroupId(Long groupId)
    {
        return blackRelatedMapper.deleteBlackRelatedByGroupId(groupId);
    }
    @Override
    public int deleteBlackRelatedByGroupIds(String groupIds)
    {
        return blackRelatedMapper.deleteBlackRelatedByGroupIds(Convert.toStrArray(groupIds));
    }
}
