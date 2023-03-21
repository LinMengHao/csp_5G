package com.xzkj.platform.servicecode.service;

import java.util.List;
import com.xzkj.platform.servicecode.domain.ECodeRelated;

/**
 * 服务码号管理Service接口
 * 
 * @author linmenghao
 * @date 2023-03-16
 */
public interface IECodeRelatedService 
{
    /**
     * 查询服务码号管理
     * 
     * @param id 服务码号管理主键
     * @return 服务码号管理
     */
    public ECodeRelated selectECodeRelatedById(Long id);

    /**
     * 查询服务码号管理列表
     * 
     * @param eCodeRelated 服务码号管理
     * @return 服务码号管理集合
     */
    public List<ECodeRelated> selectECodeRelatedList(ECodeRelated eCodeRelated);

    /**
     * 新增服务码号管理
     * 
     * @param eCodeRelated 服务码号管理
     * @return 结果
     */
    public int insertECodeRelated(ECodeRelated eCodeRelated);

    /**
     * 修改服务码号管理
     * 
     * @param eCodeRelated 服务码号管理
     * @return 结果
     */
    public int updateECodeRelated(ECodeRelated eCodeRelated);

    /**
     * 批量删除服务码号管理
     * 
     * @param ids 需要删除的服务码号管理主键集合
     * @return 结果
     */
    public int deleteECodeRelatedByIds(String ids);

    /**
     * 删除服务码号管理信息
     * 
     * @param id 服务码号管理主键
     * @return 结果
     */
    public int deleteECodeRelatedById(Long id);

    ECodeRelated autoComplete(ECodeRelated codeRelated);
}
