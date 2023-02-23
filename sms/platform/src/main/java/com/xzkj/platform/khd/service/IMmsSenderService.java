package com.xzkj.platform.khd.service;


import com.xzkj.platform.khd.domain.MmsSender;

import java.util.List;

/**
 * 下行日志的显示查询Service接口
 * 
 * @author lixl
 * @date 2022-01-24
 */
public interface IMmsSenderService 
{
    /**
     * 查询下行日志的显示查询
     * 
     * @param linkId 下行日志的显示查询主键
     * @return 下行日志的显示查询
     */
    public MmsSender selectMmsSenderByLinkId(String linkId);

    /**
     * 查询下行日志的显示查询列表
     * 
     * @param mmsSender 下行日志的显示查询
     * @return 下行日志的显示查询集合
     */
    public List<MmsSender> selectMmsSenderList(MmsSender mmsSender);

    /**
     * 新增下行日志的显示查询
     * 
     * @param mmsSender 下行日志的显示查询
     * @return 结果
     */
    public int insertMmsSender(MmsSender mmsSender);

    /**
     * 修改下行日志的显示查询
     * 
     * @param mmsSender 下行日志的显示查询
     * @return 结果
     */
    public int updateMmsSender(MmsSender mmsSender);

    /**
     * 批量删除下行日志的显示查询
     * 
     * @param linkIds 需要删除的下行日志的显示查询主键集合
     * @return 结果
     */
    public int deleteMmsSenderByLinkIds(String linkIds);

    /**
     * 删除下行日志的显示查询信息
     * 
     * @param linkId 下行日志的显示查询主键
     * @return 结果
     */
    public int deleteMmsSenderByLinkId(String linkId);
}
