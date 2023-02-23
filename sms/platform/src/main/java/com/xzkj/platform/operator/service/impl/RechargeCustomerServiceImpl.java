package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.PageUtils;
import com.xzkj.platform.common.utils.ShiroUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.operator.domain.RechargeCustomer;
import com.xzkj.platform.operator.mapper.RechargeCustomerMapper;
import com.xzkj.platform.operator.service.IRechargeCustomerService;
import com.xzkj.platform.system.service.ISysDeptService;
import com.xzkj.platform.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户充值记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-10-24
 */
@Service
public class RechargeCustomerServiceImpl implements IRechargeCustomerService 
{
    @Autowired
    private RechargeCustomerMapper rechargeCustomerMapper;
    @Autowired
    private ITApplicationService tApplicationService;
    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询客户充值记录
     * 
     * @param id 客户充值记录主键
     * @return 客户充值记录
     */
    @Override
    public RechargeCustomer selectRechargeCustomerById(Long id)
    {
        RechargeCustomer rechargeCustomer = rechargeCustomerMapper.selectRechargeCustomerById(id);
        SysDept dept = deptService.selectDeptById(rechargeCustomer.getCompanyId());
        TApplication app = tApplicationService.selectTApplicationById(rechargeCustomer.getAppId());
        SysUser user = sysUserService.selectUserById(rechargeCustomer.getUserId());

        rechargeCustomer.setCompanyName(dept!=null?dept.getCompanyName():rechargeCustomer.getCompanyId()+"");
        rechargeCustomer.setAppName(app!=null?app.getAppName():rechargeCustomer.getAppId()+"");
        rechargeCustomer.setUserName(user!=null?user.getUserName():rechargeCustomer.getUserId()+"");
        return rechargeCustomer;
    }

    /**
     * 查询客户充值记录列表
     * 
     * @param rechargeCustomer 客户充值记录
     * @return 客户充值记录
     */
    @Override
    public List<RechargeCustomer> selectRechargeCustomerList(RechargeCustomer rechargeCustomer)
    {

        List<RechargeCustomer> rechargeList = rechargeCustomerMapper.selectRechargeCustomerList(rechargeCustomer);
        PageUtils.clearPage();

        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Map<Long,String> mapDept = new HashMap<Long,String>();
        for (SysDept dept:deptList){
            mapDept.put(dept.getCompanyId(),dept.getCompanyId()+":"+dept.getCompanyName());
        }
        List<TApplication> applist = tApplicationService.selectTApplicationListN(0L);
        Map<Long,String> mapApp = new HashMap<Long,String>();
        for (TApplication app:applist){
            mapApp.put(app.getId(),app.getId()+":"+app.getAppName());
        }
        List<SysUser> userList = sysUserService.selectUserList(new SysUser());
        Map<Long,String> mapUser = new HashMap<Long,String>();
        for (SysUser user:userList){
            mapUser.put(user.getUserId(),user.getUserName());
        }

        for (RechargeCustomer en:rechargeList) {
            en.setAppName(mapApp.containsKey(en.getAppId())?mapApp.get(en.getAppId()):en.getAppId()+":");
            en.setCompanyName(mapDept.containsKey(en.getCompanyId())?mapDept.get(en.getCompanyId()):en.getCompanyId()+":");
            en.setUserName(mapUser.containsKey(en.getUserId())?mapUser.get(en.getUserId()):en.getUserId()+"");
        }

        return rechargeList;
    }

    /**
     * 新增客户充值记录
     * 
     * @param rechargeCustomer 客户充值记录
     * @return 结果
     */
    @Override
    public int insertRechargeCustomer(RechargeCustomer rechargeCustomer)
    {
        TApplication tApplication = new TApplication();
        tApplication.setChangeType(rechargeCustomer.getChangeType());
        tApplication.setId(rechargeCustomer.getAppId());
        tApplication.setLimitCount(rechargeCustomer.getChangeNum());
        tApplication.setPayCount(rechargeCustomer.getChangeNum());
        int result = tApplicationService.updateRecharge(tApplication);
        /*if(result<=0){
            return result;
        }*/
        // 获取当前的用户ID
        Long userId = ShiroUtils.getUserId();
        rechargeCustomer.setUserId(userId);
        rechargeCustomer.setCreateTime(DateUtils.getNowDate());
        return rechargeCustomerMapper.insertRechargeCustomer(rechargeCustomer);
    }

    /**
     * 修改客户充值记录
     * 
     * @param rechargeCustomer 客户充值记录
     * @return 结果
     */
    @Override
    public int updateRechargeCustomer(RechargeCustomer rechargeCustomer)
    {
        return rechargeCustomerMapper.updateRechargeCustomer(rechargeCustomer);
    }

    /**
     * 批量删除客户充值记录
     * 
     * @param ids 需要删除的客户充值记录主键
     * @return 结果
     */
    @Override
    public int deleteRechargeCustomerByIds(String ids)
    {
        return rechargeCustomerMapper.deleteRechargeCustomerByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除客户充值记录信息
     * 
     * @param id 客户充值记录主键
     * @return 结果
     */
    @Override
    public int deleteRechargeCustomerById(Long id)
    {
        return rechargeCustomerMapper.deleteRechargeCustomerById(id);
    }
}
