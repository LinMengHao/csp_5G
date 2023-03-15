package com.xzkj.platform.model.mapper;

import com.xzkj.platform.model.domain.EModelInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板信息e_model_infoMapper接口
 * 
 * @author linmenghao
 * @date 2023-02-06
 */
public interface EModelInfoMapper 
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
     * 删除模板信息e_model_info
     * 
     * @param id 模板信息e_model_info主键
     * @return 结果
     */
    public int deleteEModelInfoById(Long id);

    /**
     * 批量删除模板信息e_model_info
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEModelInfoByIds(String[] ids);

    int selectMaxId(String tableName, String field);

    int updateModelInfoByModelId(EModelInfo modelInfo);

    EModelInfo selectEModelInfoByModelId(@Param("modelId") String s);

    List<EModelInfo> selectPEModelInfoList(EModelInfo eModelInfo);

    List<EModelInfo> selectSonModelInfoList(EModelInfo info);

    List<EModelInfo> selectByPModelId(@Param("pModelId")String modelId);
}
