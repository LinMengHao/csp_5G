package com.xzkj.platform.operator.mapper;

import com.xzkj.platform.operator.domain.BlackRelated;

import java.util.List;

/**
 * 黑名单组关系Mapper接口
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
public interface BlackRelatedMapper 
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
    public int insertBlackRelateds(List<BlackRelated> channelList);

    /**
     * 修改黑名单组关系
     * 
     * @param blackRelated 黑名单组关系
     * @return 结果
     */
    public int updateBlackRelated(BlackRelated blackRelated);

    /**
     * 删除黑名单组关系
     * 
     * @param id 黑名单组关系主键
     * @return 结果
     */
    public int deleteBlackRelatedById(Long id);
    public int deleteBlackRelatedByGroupId(Long groupId);
    public int deleteBlackRelatedByGroupIds(String[] groupIds);

    /**
     * 批量删除黑名单组关系
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBlackRelatedByIds(String[] ids);
}
