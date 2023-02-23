package com.xzkj.platform.khd.mapper;



import com.xzkj.platform.khd.domain.TModel;

import java.util.List;

/**
 * 模板管理Mapper接口
 * 
 * @author Lixl
 * @date 2022-02-09
 */
public interface TModelMapper 
{
    /**
     * 查询模板管理
     * 
     * @param id 模板管理主键
     * @return 模板管理
     */
    public TModel selectTModelById(Long id);

    /**
     * 查询模板管理列表
     * 
     * @param tModel 模板管理
     * @return 模板管理集合
     */
    public List<TModel> selectTModelList(TModel tModel);

    /**
     * 查询模板管理列表
     *
     * @param companyid 公司id
     * @return 模板管理集合
     */
    public List<TModel> selectTModelListN(Long companyid);

    /**
     * 新增模板管理
     * 
     * @param tModel 模板管理
     * @return 结果
     */
    public int insertTModel(TModel tModel);

    /**
     * 修改模板管理
     * 
     * @param tModel 模板管理
     * @return 结果
     */
    public int updateTModel(TModel tModel);

    /**
     * 删除模板管理
     * 
     * @param id 模板管理主键
     * @return 结果
     */
    public int deleteTModelById(Long id);

    /**
     * 批量删除模板管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTModelByIds(String[] ids);

    /**
     * 统计模板数量
     * @return 结果
     */
    public int statisticsModel();


    /**
     * 查找模板
     *
     * @param appId appid
     * @return 工艺产品
     */
    List<TModel> selectModelByAppId(Long appId);
}
