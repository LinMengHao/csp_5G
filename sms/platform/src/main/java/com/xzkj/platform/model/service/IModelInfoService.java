package com.xzkj.platform.model.service;


import com.xzkj.platform.model.domain.ModelInfo;
import com.xzkj.platform.model.domain.ModelMaterial;

import java.util.List;

/**
 * 模板信息e_model_infoService接口
 * 
 * @author ruoyi
 * @date 2022-11-24
 */
public interface IModelInfoService 
{
    /**
     * 查询模板信息e_model_info
     * 
     * @param id 模板信息e_model_info主键
     * @return 模板信息e_model_info
     */
    public ModelInfo selectModelInfoById(Long id);

    /**
     * 查询模板信息e_model_info列表
     * 
     * @param modelInfo 模板信息e_model_info
     * @return 模板信息e_model_info集合
     */
    public List<ModelInfo> selectModelInfoList(ModelInfo modelInfo);

    /**
     * 新增模板信息e_model_info
     * 
     * @param modelInfo 模板信息e_model_info
     * @return 结果
     */
    public int insertModelInfo(ModelInfo modelInfo);

    /**
     * 修改模板信息e_model_info
     * 
     * @param modelInfo 模板信息e_model_info
     * @return 结果
     */
    public int updateModelInfo(ModelInfo modelInfo);

    /**
     * 批量删除模板信息e_model_info
     * 
     * @param ids 需要删除的模板信息e_model_info主键集合
     * @return 结果
     */
    public int deleteModelInfoByIds(String ids);

    /**
     * 删除模板信息e_model_info信息
     * 
     * @param id 模板信息e_model_info主键
     * @return 结果
     */
    public int deleteModelInfoById(Long id);

    /**
     * 获取数据表的最大主键值
     * @param tableName 数据表
     * @param field 字段名
     * @return 结果
     */
    public long getMaxId(String tableName, String field);

    int updateModelInfoByModelId(ModelInfo info);

    int copyModelInfo(ModelInfo modelInfo,List<ModelMaterial> materials);

    ModelInfo selectModelInfoByModelId(String modelId);

}
