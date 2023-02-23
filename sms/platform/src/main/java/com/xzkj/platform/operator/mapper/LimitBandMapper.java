package com.xzkj.platform.operator.mapper;

import com.xzkj.platform.operator.domain.LimitBand;

import java.util.List;

/**
 * 频次限制Mapper接口
 * 
 * @author ruoyi
 * @date 2022-12-04
 */
public interface LimitBandMapper 
{
    /**
     * 查询频次限制
     * 
     * @param id 频次限制主键
     * @return 频次限制
     */
    public LimitBand selectLimitBandById(Long id);

    /**
     * 查询频次限制列表
     * 
     * @param limitBand 频次限制
     * @return 频次限制集合
     */
    public List<LimitBand> selectLimitBandList(LimitBand limitBand);

    /**
     * 新增频次限制
     * 
     * @param limitBand 频次限制
     * @return 结果
     */
    public int insertLimitBand(LimitBand limitBand);

    /**
     * 修改频次限制
     * 
     * @param limitBand 频次限制
     * @return 结果
     */
    public int updateLimitBand(LimitBand limitBand);

    /**
     * 删除频次限制
     * 
     * @param id 频次限制主键
     * @return 结果
     */
    public int deleteLimitBandById(Long id);

    /**
     * 批量删除频次限制
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLimitBandByIds(String[] ids);
}
