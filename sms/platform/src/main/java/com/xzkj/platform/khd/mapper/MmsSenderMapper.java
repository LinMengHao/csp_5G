package com.xzkj.platform.khd.mapper;




import com.xzkj.platform.khd.domain.MmsSender;

import java.util.List;

/**
 * 下行日志的显示查询Mapper接口
 * 
 * @author lixl
 * @date 2022-01-24
 */
public interface MmsSenderMapper 
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
    public List<MmsSender> selectMmsSenderListOld(MmsSender mmsSender);

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
     * 删除下行日志的显示查询
     * 
     * @param linkId 下行日志的显示查询主键
     * @return 结果
     */
    public int deleteMmsSenderByLinkId(String linkId);

    /**
     * 批量删除下行日志的显示查询
     * 
     * @param linkIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMmsSenderByLinkIds(String[] linkIds);
}
