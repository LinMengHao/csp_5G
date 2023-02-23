package com.xzkj.platform.operator.mapper;

import com.xzkj.platform.operator.domain.BlackCompany;

import java.util.List;

/**
 * 私有黑名单Mapper接口
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
public interface BlackCompanyMapper 
{
    /**
     * 查询私有黑名单
     * 
     * @param id 私有黑名单主键
     * @return 私有黑名单
     */
    public BlackCompany selectBlackCompanyById(Long id);

    /**
     * 查询私有黑名单列表
     * 
     * @param blackCompany 私有黑名单
     * @return 私有黑名单集合
     */
    public List<BlackCompany> selectBlackCompanyList(BlackCompany blackCompany);

    /**
     * 新增私有黑名单
     * 
     * @param blackCompany 私有黑名单
     * @return 结果
     */
    public int insertBlackCompany(BlackCompany blackCompany);
    public int insertBlackCompanys(List<BlackCompany> blackCompanyList);

    /**
     * 修改私有黑名单
     * 
     * @param blackCompany 私有黑名单
     * @return 结果
     */
    public int updateBlackCompany(BlackCompany blackCompany);

    /**
     * 删除私有黑名单
     * 
     * @param id 私有黑名单主键
     * @return 结果
     */
    public int deleteBlackCompanyById(Long id);

    /**
     * 批量删除私有黑名单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBlackCompanyByIds(String[] ids);

    /**
     * 查询私有黑名单列表
     *
     * @param ids 私有黑名单
     * @return 私有黑名单集合
     */
    public List<BlackCompany> selectBlackCompanyListByIds(String[] ids);
}
