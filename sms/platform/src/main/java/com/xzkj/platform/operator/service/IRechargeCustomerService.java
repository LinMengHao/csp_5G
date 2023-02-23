package com.xzkj.platform.operator.service;

import com.xzkj.platform.operator.domain.RechargeCustomer;

import java.util.List;

/**
 * 客户充值记录Service接口
 * 
 * @author ruoyi
 * @date 2022-10-24
 */
public interface IRechargeCustomerService 
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
     * 批量删除客户充值记录
     * 
     * @param ids 需要删除的客户充值记录主键集合
     * @return 结果
     */
    public int deleteRechargeCustomerByIds(String ids);

    /**
     * 删除客户充值记录信息
     * 
     * @param id 客户充值记录主键
     * @return 结果
     */
    public int deleteRechargeCustomerById(Long id);
}
