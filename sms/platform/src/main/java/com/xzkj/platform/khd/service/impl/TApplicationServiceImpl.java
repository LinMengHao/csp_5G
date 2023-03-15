package com.xzkj.platform.khd.service.impl;


import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.PageUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.mapper.TApplicationMapper;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.operator.domain.BlackGroup;
import com.xzkj.platform.operator.service.IBlackGroupService;
import com.xzkj.platform.system.service.ISysDeptService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账户管理Service业务层处理
 * 
 * @author Lixl
 * @date 2022-02-08
 */
@Service
public class TApplicationServiceImpl implements ITApplicationService
{
    @Autowired
    private TApplicationMapper tApplicationMapper;
    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private IBlackGroupService blackGroupService;

    /**
     * 查询账户管理
     * 
     * @param id 账户管理主键
     * @return 账户管理
     */
    @Override
    public TApplication selectTApplicationById(Long id)
    {
        TApplication app = tApplicationMapper.selectTApplicationById(id);
        if(app!=null){
            SysDept dept = deptService.selectDeptById(app.getCompanyId());
            app.setCompanyName(dept!=null?dept.getCompanyName():"未知");
        }
        return app;
    }

    @Override
    public TApplication selectTApplicationByIdN(Long id)
    {
        TApplication app = tApplicationMapper.selectTApplicationByIdN(id);
        return app;
    }

    /**
     * 查询账户管理列表
     * 
     * @param tApplication 账户管理
     * @return 账户管理
     */
    @Override
    public List<TApplication> selectTApplicationList(TApplication tApplication)
    {
        List<TApplication> appList = tApplicationMapper.selectTApplicationList(tApplication);
        PageUtils.clearPage();

        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Map<Long,String> mapDept = new HashMap<Long,String>();
        for (SysDept dept:deptList){
            mapDept.put(dept.getCompanyId(),dept.getCompanyName());
        }
        List<BlackGroup> blacklist = blackGroupService.selectBlackGroupList(new BlackGroup());
        Map<Long,String> mapBlack = new HashMap<Long,String>();
        for (BlackGroup black:blacklist){
            mapBlack.put(black.getId(),black.getTitle());
        }

        for (TApplication en:appList){
            en.setCompanyName(mapDept.containsKey(en.getCompanyId())?mapDept.get(en.getCompanyId()):en.getCompanyId()+":");
            if(StringUtils.isBlank(en.getBlackLevels())){
                en.setBlackLevels("无");
            }else{
                en.setBlackLevels(mapBlack.containsKey(Long.valueOf(en.getBlackLevels()))?(en.getBlackLevels()+":"+mapBlack.get(Long.valueOf(en.getBlackLevels()))):en.getBlackLevels()+":");
            }
        }
        return appList;
    }

    @Override
    public List<TApplication> selectTApplicationListN(Long CompanyId) {
        return tApplicationMapper.selectTApplicationListN(CompanyId);
    }

    @Override
    public List<TApplication> selectTApplicationByAppExt(String appExt) {
        return tApplicationMapper.selectTApplicationByAppExt(appExt);
    }

    /**
     * 新增账户管理
     * 
     * @param tApplication 账户管理
     * @return 结果
     */
    @Override
    public int insertTApplication(TApplication tApplication)
    {
        tApplication.setAddTime(DateUtils.getNowDate());
        return tApplicationMapper.insertTApplication(tApplication);
    }

    /**
     * 修改账户管理
     * 
     * @param tApplication 账户管理
     * @return 结果
     */
    @Override
    public int updateTApplication(TApplication tApplication)
    {
        return tApplicationMapper.updateTApplication(tApplication);
    }

    /**
     * 修改账户充值余额
     *
     * @param tApplication 账户管理
     * @return 结果
     */
    @Override
    public int updateRecharge(TApplication tApplication)
    {
        return tApplicationMapper.updateRecharge(tApplication);
    }

    /**
     * 批量删除账户管理
     * 
     * @param ids 需要删除的账户管理主键
     * @return 结果
     */
    @Override
    public int deleteTApplicationByIds(String ids)
    {
        return tApplicationMapper.deleteTApplicationByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除账户管理信息
     * 
     * @param id 账户管理主键
     * @return 结果
     */
    @Override
    public int deleteTApplicationById(Long id)
    {
        return tApplicationMapper.deleteTApplicationById(id);
    }
}
