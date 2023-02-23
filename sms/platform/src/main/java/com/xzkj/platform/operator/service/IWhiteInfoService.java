package com.xzkj.platform.operator.service;

import com.xzkj.platform.operator.domain.WhiteInfo;

import java.util.List;

/**
 * 白名单Service接口
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
public interface IWhiteInfoService 
{
    /**
     * 查询白名单
     * 
     * @param id 白名单主键
     * @return 白名单
     */
    public WhiteInfo selectWhiteInfoById(Long id);

    /**
     * 查询白名单列表
     * 
     * @param whiteInfo 白名单
     * @return 白名单集合
     */
    public List<WhiteInfo> selectWhiteInfoList(WhiteInfo whiteInfo);

    /**
     * 新增白名单
     * 
     * @param whiteInfo 白名单
     * @return 结果
     */
    public int insertWhiteInfo(WhiteInfo whiteInfo);

    /**
     * 修改白名单
     * 
     * @param whiteInfo 白名单
     * @return 结果
     */
    public int updateWhiteInfo(WhiteInfo whiteInfo);

    /**
     * 批量删除白名单
     * 
     * @param ids 需要删除的白名单主键集合
     * @return 结果
     */
    public int deleteWhiteInfoByIds(String ids);

    /**
     * 删除白名单信息
     * 
     * @param id 白名单主键
     * @return 结果
     */
    public int deleteWhiteInfoById(Long id);
}
