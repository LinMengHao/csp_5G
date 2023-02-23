package com.xzkj.platform.model.mapper;

import com.xzkj.platform.model.domain.ModelInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板信息e_model_infoMapper接口
 * 
 * @author ruoyi
 * @date 2022-11-24
 */
public interface ModelInfoMapper 
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
     * 删除模板信息e_model_info
     * 
     * @param id 模板信息e_model_info主键
     * @return 结果
     */
    public int deleteModelInfoById(Long id);

    /**
     * 批量删除模板信息e_model_info
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteModelInfoByIds(String[] ids);

    /**
     * 获取模板表的最大id值
     * @param tableName 表名
     * @param field 主键名称
     * @return 最大id值
     */
    public int selectMaxId(@Param("tableName") String tableName,@Param("field") String field);

    int updateModelInfoByModelId(ModelInfo info);

    ModelInfo selectModelInfoByModelId(String modelId);
}
