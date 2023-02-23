package com.xzkj.platform.khd.service.impl;


import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.khd.domain.TOutbox;
import com.xzkj.platform.khd.mapper.TOutboxMapper;
import com.xzkj.platform.khd.service.ITOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发件箱Service业务层处理
 * 
 * @author Lixl
 * @date 2022-02-07
 */
@Service
public class TOutboxServiceImpl implements ITOutboxService
{
    @Autowired
    private TOutboxMapper tOutboxMapper;

    /**
     * 查询发件箱
     * 
     * @param id 发件箱主键
     * @return 发件箱
     */
    @Override
    public TOutbox selectTOutboxById(Long id)
    {
        return tOutboxMapper.selectTOutboxById(id);
    }

    /**
     * 查询发件箱列表
     * 
     * @param tOutbox 发件箱
     * @return 发件箱
     */
    @Override
    public List<TOutbox> selectTOutboxList(TOutbox tOutbox)
    {
        return tOutboxMapper.selectTOutboxList(tOutbox);
    }

    /**
     * 新增发件箱
     * 
     * @param tOutbox 发件箱
     * @return 结果
     */
    @Override
    public int insertTOutbox(TOutbox tOutbox)
    {
        return tOutboxMapper.insertTOutbox(tOutbox);
    }

    /**
     * 修改发件箱
     * 
     * @param tOutbox 发件箱
     * @return 结果
     */
    @Override
    public int updateTOutbox(TOutbox tOutbox)
    {
        return tOutboxMapper.updateTOutbox(tOutbox);
    }

    /**
     * 批量删除发件箱
     * 
     * @param ids 需要删除的发件箱主键
     * @return 结果
     */
    @Override
    public int deleteTOutboxByIds(String ids)
    {
        return tOutboxMapper.deleteTOutboxByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除发件箱信息
     * 
     * @param id 发件箱主键
     * @return 结果
     */
    @Override
    public int deleteTOutboxById(Long id)
    {
        return tOutboxMapper.deleteTOutboxById(id);
    }
}
