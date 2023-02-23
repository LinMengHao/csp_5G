package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.operator.domain.BlackGroup;
import com.xzkj.platform.operator.domain.BlackRelated;
import com.xzkj.platform.operator.mapper.BlackGroupMapper;
import com.xzkj.platform.operator.service.IBlackGroupService;
import com.xzkj.platform.operator.service.IBlackRelatedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 黑名单组Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Service
public class BlackGroupServiceImpl implements IBlackGroupService 
{
    @Autowired
    private BlackGroupMapper blackGroupMapper;
    @Autowired
    private IBlackRelatedService blackRelatedService;

    /**
     * 查询黑名单组
     * 
     * @param id 黑名单组主键
     * @return 黑名单组
     */
    @Override
    public BlackGroup selectBlackGroupById(Long id)
    {
        BlackGroup blackGroup = blackGroupMapper.selectBlackGroupById(id);
        BlackRelated blackRelated = new BlackRelated();
        blackRelated.setGroupId(id);
        List<BlackRelated> channelList = blackRelatedService.selectBlackRelatedList(blackRelated);
        blackGroup.setChannelList(channelList);
        return blackGroup;
    }

    /**
     * 查询黑名单组列表
     * 
     * @param blackGroup 黑名单组
     * @return 黑名单组
     */
    @Override
    public List<BlackGroup> selectBlackGroupList(BlackGroup blackGroup)
    {
        return blackGroupMapper.selectBlackGroupList(blackGroup);
    }

    /**
     * 新增黑名单组
     * @param blackGroup 黑名单组
     * @return 结果
     */
    @Override
    public int insertBlackGroup(BlackGroup blackGroup)
    {
        List<BlackRelated> channelList = blackGroup.getChannelList();
        blackGroup.setChannelTotal(channelList==null?0L:channelList.size());
        blackGroup.setUpdateTime(DateUtils.getNowDate());
        blackGroup.setCreateTime(DateUtils.getNowDate());
        int count = blackGroupMapper.insertBlackGroup(blackGroup);

        Long groupId = blackGroup.getId();
        blackRelatedService.insertBlackRelated(groupId,channelList);

        return count;
    }

    /**
     * 修改黑名单组
     * 
     * @param blackGroup 黑名单组
     * @return 结果
     */
    @Override
    public int updateBlackGroup(BlackGroup blackGroup)
    {
        List<BlackRelated> channelList = blackGroup.getChannelList();
        blackGroup.setChannelTotal(channelList==null?0L:channelList.size());
        blackGroup.setUpdateTime(DateUtils.getNowDate());
        int count = blackGroupMapper.updateBlackGroup(blackGroup);

        Long groupId = blackGroup.getId();
        blackRelatedService.deleteBlackRelatedByGroupId(groupId);
        blackRelatedService.insertBlackRelated(groupId,channelList);
        return count;
    }

    /**
     * 批量删除黑名单组
     * 
     * @param ids 需要删除的黑名单组主键
     * @return 结果
     */
    @Override
    public int deleteBlackGroupByIds(String ids)
    {
        int count = blackGroupMapper.deleteBlackGroupByIds(Convert.toStrArray(ids));
        blackRelatedService.deleteBlackRelatedByGroupIds(ids);
        return count;
    }

    /**
     * 删除黑名单组信息
     * 
     * @param id 黑名单组主键
     * @return 结果
     */
    @Override
    public int deleteBlackGroupById(Long id)
    {
        int count = blackGroupMapper.deleteBlackGroupById(id);
        blackRelatedService.deleteBlackRelatedByGroupId(id);
        return count;
    }
}
