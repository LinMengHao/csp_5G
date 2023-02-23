package com.xzkj.platform.khd.mapper;




import com.xzkj.platform.khd.domain.TOutbox;

import java.util.List;

/**
 * 发件箱Mapper接口
 * 
 * @author ruoyi
 * @date 2022-02-07
 */
public interface TOutboxMapper 
{
    /**
     * 查询发件箱
     * 
     * @param id 发件箱主键
     * @return 发件箱
     */
    public TOutbox selectTOutboxById(Long id);

    /**
     * 查询发件箱列表
     * 
     * @param tOutbox 发件箱
     * @return 发件箱集合
     */
    public List<TOutbox> selectTOutboxList(TOutbox tOutbox);

    /**
     * 新增发件箱
     * 
     * @param tOutbox 发件箱
     * @return 结果
     */
    public int insertTOutbox(TOutbox tOutbox);

    /**
     * 修改发件箱
     * 
     * @param tOutbox 发件箱
     * @return 结果
     */
    public int updateTOutbox(TOutbox tOutbox);

    /**
     * 删除发件箱
     * 
     * @param id 发件箱主键
     * @return 结果
     */
    public int deleteTOutboxById(Long id);

    /**
     * 批量删除发件箱
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTOutboxByIds(String[] ids);
}
