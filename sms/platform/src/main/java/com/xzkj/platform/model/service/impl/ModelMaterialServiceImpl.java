package com.xzkj.platform.model.service.impl;

import com.github.pagehelper.PageHelper;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.ShiroUtils;
import com.xzkj.platform.model.domain.ModelMaterial;
import com.xzkj.platform.model.mapper.ModelMaterialMapper;
import com.xzkj.platform.model.service.IModelMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模板素材Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-11-30
 */
@Service
public class ModelMaterialServiceImpl implements IModelMaterialService 
{
    @Autowired
    private ModelMaterialMapper modelMaterialMapper;

    /**
     * 查询模板素材
     * 
     * @param id 模板素材主键
     * @return 模板素材
     */
    @Override
    public ModelMaterial selectModelMaterialById(Long id)
    {
        return modelMaterialMapper.selectModelMaterialById(id);
    }

    /**
     * 查询模板素材列表
     * 
     * @param modelMaterial 模板素材
     * @return 模板素材
     */
    @Override
    public List<ModelMaterial> selectModelMaterialList(ModelMaterial modelMaterial)
    {
        return modelMaterialMapper.selectModelMaterialList(modelMaterial);
    }
    @Override
    public List<ModelMaterial> selectModelMaterialLists(ModelMaterial modelMaterial)
    {
        PageHelper.orderBy("frame_index,frame_sort");
        return modelMaterialMapper.selectModelMaterialLists(modelMaterial);
    }

    /**
     * 新增模板素材
     * 
     * @param modelMaterial 模板素材
     * @return 结果
     */
    @Override
    public int insertModelMaterial(ModelMaterial modelMaterial)
    {
        modelMaterial.setCreateTime(DateUtils.getNowDate());
        return modelMaterialMapper.insertModelMaterial(modelMaterial);
    }
    @Override
    public int insertModelMaterials(List<ModelMaterial> materialList)
    {
        Long user_id = ShiroUtils.getUserId();
        for (ModelMaterial modelMaterial:materialList) {
            modelMaterial.setUserId(user_id);
            modelMaterial.setUpdateTime(DateUtils.getNowDate());
            modelMaterial.setCreateTime(DateUtils.getNowDate());
        }
        return modelMaterialMapper.insertModelMaterials(materialList);
    }

    /**
     * 修改模板素材
     * 
     * @param modelMaterial 模板素材
     * @return 结果
     */
    @Override
    public int updateModelMaterial(ModelMaterial modelMaterial)
    {
        modelMaterial.setUpdateTime(DateUtils.getNowDate());
        return modelMaterialMapper.updateModelMaterial(modelMaterial);
    }

    /**
     * 批量删除模板素材
     * 
     * @param ids 需要删除的模板素材主键
     * @return 结果
     */
    @Override
    public int deleteModelMaterialByIds(String ids)
    {
        return modelMaterialMapper.deleteModelMaterialByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除模板素材信息
     * 
     * @param id 模板素材主键
     * @return 结果
     */
    @Override
    public int deleteModelMaterialById(Long id)
    {
        return modelMaterialMapper.deleteModelMaterialById(id);
    }
    @Override
    public int deleteModelMaterialByModelId(String modelId)
    {
        return modelMaterialMapper.deleteModelMaterialByModelId(modelId);
    }
}
