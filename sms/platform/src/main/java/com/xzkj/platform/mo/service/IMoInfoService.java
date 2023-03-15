package com.xzkj.platform.mo.service;

import java.util.List;
import com.xzkj.platform.mo.domain.MoInfo;

/**
 * 上行Service接口
 * 
 * @author lmh
 * @date 2023-03-09
 */
public interface IMoInfoService 
{
    /**
     * 查询上行
     *
     * @return 上行
     */
    public MoInfo selectMoInfoById(MoInfo moInfo);

    /**
     * 查询上行列表
     * 
     * @param moInfo 上行
     * @return 上行集合
     */
    public List<MoInfo> selectMoInfoList(MoInfo moInfo);

    /**
     * 新增上行
     * 
     * @param moInfo 上行
     * @return 结果
     */
    public int insertMoInfo(MoInfo moInfo);

    /**
     * 修改上行
     * 
     * @param moInfo 上行
     * @return 结果
     */
    public int updateMoInfo(MoInfo moInfo);

    /**
     * 批量删除上行
     * 
     * @param ids 需要删除的上行主键集合
     * @return 结果
     */
    public int deleteMoInfoByIds(String ids);

    /**
     * 删除上行信息
     * 
     * @param id 上行主键
     * @return 结果
     */
    public int deleteMoInfoById(String id);

    int updateMoInfoByDay(MoInfo moInfo);
}
