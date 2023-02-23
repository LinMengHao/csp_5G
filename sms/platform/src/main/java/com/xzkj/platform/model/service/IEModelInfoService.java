package com.xzkj.platform.model.service;

import com.xzkj.platform.common.core.domain.Ztree;
import com.xzkj.platform.model.domain.EModelInfo;
import com.xzkj.platform.model.domain.ModelMaterial;

import java.util.List;


/**
 * 模板信息e_model_infoService接口
 * 
 * @author linmenghao
 * @date 2023-02-06
 */
public interface IEModelInfoService 
{
    /**
     * 查询模板信息e_model_info
     * 
     * @param id 模板信息e_model_info主键
     * @return 模板信息e_model_info
     */
    public EModelInfo selectEModelInfoById(Long id);

    /**
     * 查询模板信息e_model_info列表
     * 
     * @param eModelInfo 模板信息e_model_info
     * @return 模板信息e_model_info集合
     */
    public List<EModelInfo> selectEModelInfoList(EModelInfo eModelInfo);

    /**
     * 新增模板信息e_model_info
     * 
     * @param eModelInfo 模板信息e_model_info
     * @return 结果
     */
    public int insertEModelInfo(EModelInfo eModelInfo);

    /**
     * 修改模板信息e_model_info
     * 
     * @param eModelInfo 模板信息e_model_info
     * @return 结果
     */
    public int updateEModelInfo(EModelInfo eModelInfo);

    /**
     * 批量删除模板信息e_model_info
     * 
     * @param ids 需要删除的模板信息e_model_info主键集合
     * @return 结果
     */
    public int deleteEModelInfoByIds(String ids);

    /**
     * 删除模板信息e_model_info信息
     * 
     * @param id 模板信息e_model_info主键
     * @return 结果
     */
    public int deleteEModelInfoById(Long id);

    /**
     * 查询模板信息e_model_info树列表
     * 
     * @return 所有模板信息e_model_info信息
     */
    public List<Ztree> selectEModelInfoTree();
    /**
     * 获取数据表的最大主键值
     * @param tableName 数据表
     * @param field 字段名
     * @return 结果
     */
    public long getMaxId(String tableName, String field);

    int copyEModelInfo(EModelInfo eModelInfo, List<ModelMaterial> modelMaterials);

    int updateModelInfoByModelId(EModelInfo modelInfo);

    EModelInfo selectEModelInfoByModelId(String s);

    List<EModelInfo> selectPEModelInfoList(EModelInfo eModelInfo);

    List<EModelInfo> selectSonModelInfoList(EModelInfo info);
}
