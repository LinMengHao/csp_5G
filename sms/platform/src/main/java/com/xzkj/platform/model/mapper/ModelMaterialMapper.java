package com.xzkj.platform.model.mapper;

import com.xzkj.platform.model.domain.ModelMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板素材Mapper接口
 * 
 * @author ruoyi
 * @date 2022-11-30
 */
public interface ModelMaterialMapper 
{
    /**
     * 查询模板素材
     * 
     * @param id 模板素材主键
     * @return 模板素材
     */
    public ModelMaterial selectModelMaterialById(Long id);

    /**
     * 查询模板素材列表
     * 
     * @param modelMaterial 模板素材
     * @return 模板素材集合
     */
    public List<ModelMaterial> selectModelMaterialList(ModelMaterial modelMaterial);
    public List<ModelMaterial> selectModelMaterialLists(ModelMaterial modelMaterial);

    /**
     * 新增模板素材
     * 
     * @param modelMaterial 模板素材
     * @return 结果
     */
    public int insertModelMaterial(ModelMaterial modelMaterial);
    public int insertModelMaterials(List<ModelMaterial> materialList);

    /**
     * 修改模板素材
     * 
     * @param modelMaterial 模板素材
     * @return 结果
     */
    public int updateModelMaterial(ModelMaterial modelMaterial);

    /**
     * 删除模板素材
     * 
     * @param id 模板素材主键
     * @return 结果
     */
    public int deleteModelMaterialById(Long id);
    public int deleteModelMaterialByModelId(@Param("modelId") String modelId);

    /**
     * 批量删除模板素材
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteModelMaterialByIds(String[] ids);
}
