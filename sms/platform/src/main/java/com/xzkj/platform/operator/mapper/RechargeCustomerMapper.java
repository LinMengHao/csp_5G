package com.xzkj.platform.operator.mapper;

import com.xzkj.platform.operator.domain.RechargeCustomer;

import java.util.List;

/**
 * 客户充值记录Mapper接口
 * 
 * @author ruoyi
 * @date 2022-10-24
 */
public interface RechargeCustomerMapper 
{
    /**
     * 查询客户充值记录
     * 
     * @param id 客户充值记录主键
     * @return 客户充值记录
     */
    public RechargeCustomer selectRechargeCustomerById(Long id);

    /**
     * 查询客户充值记录列表
     * 
     * @param rechargeCustomer 客户充值记录
     * @return 客户充值记录集合
     */
    public List<RechargeCustomer> selectRechargeCustomerList(RechargeCustomer rechargeCustomer);

    /**
     * 新增客户充值记录
     * 
     * @param rechargeCustomer 客户充值记录
     * @return 结果
     */
    public int insertRechargeCustomer(RechargeCustomer rechargeCustomer);

    /**
     * 修改客户充值记录
     * 
     * @param rechargeCustomer 客户充值记录
     * @return 结果
     */
    public int updateRechargeCustomer(RechargeCustomer rechargeCustomer);

    /**
     * 删除客户充值记录
     * 
     * @param id 客户充值记录主键
     * @return 结果
     */
    public int deleteRechargeCustomerById(Long id);

    /**
     * 批量删除客户充值记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRechargeCustomerByIds(String[] ids);
}
