package com.xzkj.platform.operator.service;

import com.xzkj.platform.operator.domain.ModelRelated;

import java.util.List;

/**
 * 模板关系e_model_relatedService接口
 * 
 * @author ruoyi
 * @date 2022-10-12
 */
public interface IModelRelatedService 
{
    /**
     * 查询模板关系e_model_related
     * 
     * @param id 模板关系e_model_related主键
     * @return 模板关系e_model_related
     */
    public ModelRelated selectModelRelatedById(Long id);

    /**
     * 查询模板关系e_model_related列表
     * 
     * @param modelRelated 模板关系e_model_related
     * @return 模板关系e_model_related集合
     */
    public List<ModelRelated> selectModelRelatedList(ModelRelated modelRelated);

    /**
     * 新增模板关系e_model_related
     * 
     * @param modelRelated 模板关系e_model_related
     * @return 结果
     */
    public int insertModelRelated(ModelRelated modelRelated);

    /**
     * 修改模板关系e_model_related
     * 
     * @param modelRelated 模板关系e_model_related
     * @return 结果
     */
    public int updateModelRelated(ModelRelated modelRelated);

    /**
     * 批量删除模板关系e_model_related
     * 
     * @param ids 需要删除的模板关系e_model_related主键集合
     * @return 结果
     */
    public int deleteModelRelatedByIds(String ids);

    /**
     * 删除模板关系e_model_related信息
     * 
     * @param id 模板关系e_model_related主键
     * @return 结果
     */
    public int deleteModelRelatedById(Long id);

    /**
     * 批量导入模版映射关系
     * @param list
     * @param
     * @return
     */
    String importModelRelated(List<ModelRelated> list);

    int updateModelRelatedByQuery(ModelRelated modelRelated);
}
