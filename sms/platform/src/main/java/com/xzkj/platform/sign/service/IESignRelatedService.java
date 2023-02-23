package com.xzkj.platform.sign.service;

import com.xzkj.platform.sign.domain.ESignRelated;

import java.util.List;

/**
 * 签名映射关系Service接口
 * 
 * @author linmenghao
 * @date 2023-02-06
 */
public interface IESignRelatedService 
{
    /**
     * 查询签名映射关系
     * 
     * @param id 签名映射关系主键
     * @return 签名映射关系
     */
    public ESignRelated selectESignRelatedById(Long id);

    /**
     * 查询签名映射关系列表
     * 
     * @param eSignRelated 签名映射关系
     * @return 签名映射关系集合
     */
    public List<ESignRelated> selectESignRelatedList(ESignRelated eSignRelated);

    /**
     * 新增签名映射关系
     * 
     * @param eSignRelated 签名映射关系
     * @return 结果
     */
    public int insertESignRelated(ESignRelated eSignRelated);

    /**
     * 修改签名映射关系
     * 
     * @param eSignRelated 签名映射关系
     * @return 结果
     */
    public int updateESignRelated(ESignRelated eSignRelated);

    /**
     * 批量删除签名映射关系
     * 
     * @param ids 需要删除的签名映射关系主键集合
     * @return 结果
     */
    public int deleteESignRelatedByIds(String ids);

    /**
     * 删除签名映射关系信息
     * 
     * @param id 签名映射关系主键
     * @return 结果
     */
    public int deleteESignRelatedById(Long id);
}
