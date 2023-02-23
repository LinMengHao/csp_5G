package com.xzkj.platform.khd.service.impl;


import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.khd.domain.TModel;
import com.xzkj.platform.khd.mapper.TModelMapper;
import com.xzkj.platform.khd.service.ITModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模板管理Service业务层处理
 * 
 * @author Lixl
 * @date 2022-02-09
 */
@Service
public class TModelServiceImpl implements ITModelService
{
    @Autowired
    private TModelMapper tModelMapper;

    /**
     * 查询模板管理
     * 
     * @param id 模板管理主键
     * @return 模板管理
     */
    @Override
    public TModel selectTModelById(Long id)
    {
        return tModelMapper.selectTModelById(id);
    }

    /**
     * 查询模板管理列表
     * 
     * @param tModel 模板管理
     * @return 模板管理
     */
    @Override
    public List<TModel> selectTModelList(TModel tModel)
    {
        return tModelMapper.selectTModelList(tModel);
    }

    @Override
    public List<TModel> selectTModelListN(Long companyid) {
        return tModelMapper.selectTModelListN(companyid);
    }


    /**
     * 新增模板管理
     * 
     * @param tModel 模板管理
     * @return 结果
     */
    @Override
    public int insertTModel(TModel tModel)
    {
        return tModelMapper.insertTModel(tModel);
    }

    /**
     * 修改模板管理
     * 
     * @param tModel 模板管理
     * @return 结果
     */
    @Override
    public int updateTModel(TModel tModel)
    {
        return tModelMapper.updateTModel(tModel);
    }

    /**
     * 批量删除模板管理
     * 
     * @param ids 需要删除的模板管理主键
     * @return 结果
     */
    @Override
    public int deleteTModelByIds(String ids)
    {
        return tModelMapper.deleteTModelByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除模板管理信息
     * 
     * @param id 模板管理主键
     * @return 结果
     */
    @Override
    public int deleteTModelById(Long id)
    {
        return tModelMapper.deleteTModelById(id);
    }

    @Override
    public int statisticsModel() {
        return tModelMapper.statisticsModel();
    }

    @Override
    public List<TModel> selectModelByAppId(Long appId) {
        return tModelMapper.selectModelByAppId(appId);
    }
}
