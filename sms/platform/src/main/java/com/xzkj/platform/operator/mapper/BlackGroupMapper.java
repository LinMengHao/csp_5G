package com.xzkj.platform.operator.mapper;

import com.xzkj.platform.operator.domain.BlackGroup;

import java.util.List;

/**
 * 黑名单组Mapper接口
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
public interface BlackGroupMapper 
{
    /**
     * 查询黑名单组
     * 
     * @param id 黑名单组主键
     * @return 黑名单组
     */
    public BlackGroup selectBlackGroupById(Long id);

    /**
     * 查询黑名单组列表
     * 
     * @param blackGroup 黑名单组
     * @return 黑名单组集合
     */
    public List<BlackGroup> selectBlackGroupList(BlackGroup blackGroup);

    /**
     * 新增黑名单组
     * 
     * @param blackGroup 黑名单组
     * @return 结果
     */
    public int insertBlackGroup(BlackGroup blackGroup);

    /**
     * 修改黑名单组
     * 
     * @param blackGroup 黑名单组
     * @return 结果
     */
    public int updateBlackGroup(BlackGroup blackGroup);

    /**
     * 删除黑名单组
     * 
     * @param id 黑名单组主键
     * @return 结果
     */
    public int deleteBlackGroupById(Long id);

    /**
     * 批量删除黑名单组
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBlackGroupByIds(String[] ids);
}
