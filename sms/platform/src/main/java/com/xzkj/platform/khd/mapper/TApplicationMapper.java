package com.xzkj.platform.khd.mapper;




import com.xzkj.platform.khd.domain.TApplication;

import java.util.List;

/**
 * 账户管理Mapper接口
 * 
 * @author ruoyi
 * @date 2022-02-08
 */
public interface TApplicationMapper 
{
    /**
     * 查询账户管理
     * 
     * @param id 账户管理主键
     * @return 账户管理
     */
    public TApplication selectTApplicationById(Long id);

    /**
     * 查询账户管理列表
     * 
     * @param tApplication 账户管理
     * @return 账户管理集合
     */
    public List<TApplication> selectTApplicationList(TApplication tApplication);
    public List<TApplication> selectTApplicationListN(Long CompanyId);

    /**
     * 新增账户管理
     * 
     * @param tApplication 账户管理
     * @return 结果
     */
    public int insertTApplication(TApplication tApplication);

    /**
     * 修改账户管理
     * 
     * @param tApplication 账户管理
     * @return 结果
     */
    public int updateTApplication(TApplication tApplication);

    /**
     * 修改账户充值余额信息
     *
     * @param tApplication 账户管理
     * @return 结果
     */
    public int updateRecharge(TApplication tApplication);

    /**
     * 删除账户管理
     * 
     * @param id 账户管理主键
     * @return 结果
     */
    public int deleteTApplicationById(Long id);

    /**
     * 批量删除账户管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTApplicationByIds(String[] ids);
}
