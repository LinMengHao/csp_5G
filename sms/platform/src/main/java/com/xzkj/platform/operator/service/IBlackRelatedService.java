package com.xzkj.platform.operator.service;


import com.xzkj.platform.operator.domain.BlackRelated;

import java.util.List;

/**
 * 黑名单组关系Service接口
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
public interface IBlackRelatedService 
{
    /**
     * 查询黑名单组关系
     * 
     * @param id 黑名单组关系主键
     * @return 黑名单组关系
     */
    public BlackRelated selectBlackRelatedById(Long id);

    /**
     * 查询黑名单组关系列表
     * 
     * @param blackRelated 黑名单组关系
     * @return 黑名单组关系集合
     */
    public List<BlackRelated> selectBlackRelatedList(BlackRelated blackRelated);

    /**
     * 新增黑名单组关系
     * 
     * @param blackRelated 黑名单组关系
     * @return 结果
     */
    public int insertBlackRelated(BlackRelated blackRelated);

    /**
     * 新增黑名单组关系
     *
     * @param groupId 黑名单组id
     * @param channelList 黑名单组关系列表
     * @return 结果
     */
    public int insertBlackRelated(Long groupId,List<BlackRelated> channelList);

    /**
     * 修改黑名单组关系
     * 
     * @param blackRelated 黑名单组关系
     * @return 结果
     */
    public int updateBlackRelated(BlackRelated blackRelated);

    /**
     * 批量删除黑名单组关系
     * 
     * @param ids 需要删除的黑名单组关系主键集合
     * @return 结果
     */
    public int deleteBlackRelatedByIds(String ids);

    /**
     * 删除黑名单组关系信息
     * 
     * @param id 黑名单组关系主键
     * @return 结果
     */
    public int deleteBlackRelatedById(Long id);
    public int deleteBlackRelatedByGroupId(Long groupId);
    public int deleteBlackRelatedByGroupIds(String groupIds);
}
