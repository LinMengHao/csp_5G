package com.xzkj.platform.operator.service;


import com.xzkj.platform.operator.domain.BlackInfo;

import java.util.List;

/**
 * 全局黑名单Service接口
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
public interface IBlackInfoService 
{
    /**
     * 查询全局黑名单
     * 
     * @param id 全局黑名单主键
     * @return 全局黑名单
     */
    public BlackInfo selectBlackInfoById(Long id);

    /**
     * 查询全局黑名单列表
     * 
     * @param blackInfo 全局黑名单
     * @return 全局黑名单集合
     */
    public List<BlackInfo> selectBlackInfoList(BlackInfo blackInfo);

    /**
     * 新增全局黑名单
     * 
     * @param blackInfo 全局黑名单
     * @return 结果
     */
    public int insertBlackInfo(BlackInfo blackInfo);

    /**
     * 修改全局黑名单
     * 
     * @param blackInfo 全局黑名单
     * @return 结果
     */
    public int updateBlackInfo(BlackInfo blackInfo);

    /**
     * 批量删除全局黑名单
     * 
     * @param ids 需要删除的全局黑名单主键集合
     * @return 结果
     */
    public int deleteBlackInfoByIds(String ids);

    /**
     * 删除全局黑名单信息
     * 
     * @param id 全局黑名单主键
     * @return 结果
     */
    public int deleteBlackInfoById(Long id);
}
