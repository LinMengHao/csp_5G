package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.PageUtils;
import com.xzkj.platform.operator.domain.BlackCompany;
import com.xzkj.platform.operator.mapper.BlackCompanyMapper;
import com.xzkj.platform.operator.service.IBlackCompanyService;
import com.xzkj.platform.redis.RedisUtils;
import com.xzkj.platform.system.service.ISysDeptService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 私有黑名单Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Service
public class BlackCompanyServiceImpl implements IBlackCompanyService 
{
    @Autowired
    private BlackCompanyMapper blackCompanyMapper;
    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询私有黑名单
     * 
     * @param id 私有黑名单主键
     * @return 私有黑名单
     */
    @Override
    public BlackCompany selectBlackCompanyById(Long id)
    {
        BlackCompany blackCompany = blackCompanyMapper.selectBlackCompanyById(id);
        SysDept dept = deptService.selectDeptById(blackCompany.getCompanyId());
        blackCompany.setCompanyName(dept!=null?dept.getCompanyName():"未知");
        return blackCompany;
    }

    /**
     * 查询私有黑名单列表
     * 
     * @param blackCompany 私有黑名单
     * @return 私有黑名单
     */
    @Override
    public List<BlackCompany> selectBlackCompanyList(BlackCompany blackCompany)
    {
        List<BlackCompany> list = blackCompanyMapper.selectBlackCompanyList(blackCompany);
        PageUtils.clearPage();
        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Map<Long,String> mapDept = new HashMap<Long,String>();
        for (SysDept dept:deptList){
            mapDept.put(dept.getCompanyId(),dept.getCompanyId()+":"+dept.getCompanyName());
        }
        for (BlackCompany en:list){
            en.setCompanyName(mapDept.containsKey(en.getCompanyId())?mapDept.get(en.getCompanyId()):en.getCompanyId()+":");
        }
        return list;
    }

    /**
     * 新增私有黑名单
     * 
     * @param blackCompany 私有黑名单
     * @return 结果
     */
    @Override
    public int insertBlackCompany(BlackCompany blackCompany)
    {
        if(blackCompany.getCompanyId()==null){
            blackCompany.setCompanyId(0L);
        }
        String mobiles = blackCompany.getMobile();
        String[] mobArr = mobiles.replaceAll("，",",").replaceAll("\r\n",",").split(",");
        List<BlackCompany> blackInfoList = new ArrayList<BlackCompany>();
        for (String mobile:mobArr){
            if(StringUtils.isBlank(mobile)){
                continue;
            }
            BlackCompany one = new BlackCompany();
            one.setCompanyId(blackCompany.getCompanyId());
            one.setMobile(mobile.trim());
            one.setRemark(blackCompany.getRemark());
            one.setUpdateTime(DateUtils.getNowDate());
            one.setCreateTime(DateUtils.getNowDate());
            blackInfoList.add(one);
            //私有黑名单
            String key = RedisUtils.HASH_MOBILE_BLACK+"0"+one.getCompanyId();
            RedisUtils.hash_set(key,one.getMobile(),"1");
        }
        int count=0;
        if(blackInfoList.size()==1){
            count = blackCompanyMapper.insertBlackCompany(blackInfoList.get(0));
        }else{
            count = blackCompanyMapper.insertBlackCompanys(blackInfoList);
        }

        return count;
    }

    /**
     * 修改私有黑名单
     * 
     * @param blackCompany 私有黑名单
     * @return 结果
     */
    @Override
    public int updateBlackCompany(BlackCompany blackCompany)
    {
        BlackCompany blackOri = blackCompanyMapper.selectBlackCompanyById(blackCompany.getId());
        //私有黑名单
        String key = RedisUtils.HASH_MOBILE_BLACK+"0"+blackOri.getCompanyId();
        RedisUtils.hash_remove(key,blackOri.getMobile());

        key = RedisUtils.HASH_MOBILE_BLACK+"0"+blackCompany.getCompanyId();
        RedisUtils.hash_set(key,blackCompany.getMobile(),"1");

        blackCompany.setUpdateTime(DateUtils.getNowDate());
        return blackCompanyMapper.updateBlackCompany(blackCompany);
    }

    /**
     * 批量删除私有黑名单
     * 
     * @param ids 需要删除的私有黑名单主键
     * @return 结果
     */
    @Override
    public int deleteBlackCompanyByIds(String ids)
    {
        String[] idsArr = Convert.toStrArray(ids);
        List<BlackCompany> list = blackCompanyMapper.selectBlackCompanyListByIds(idsArr);
        for (BlackCompany blackOri:list){
            //私有黑名单
            String key = RedisUtils.HASH_MOBILE_BLACK+"0"+blackOri.getCompanyId();
            RedisUtils.hash_remove(key,blackOri.getMobile());
        }
        return blackCompanyMapper.deleteBlackCompanyByIds(idsArr);
    }

    /**
     * 删除私有黑名单信息
     * 
     * @param id 私有黑名单主键
     * @return 结果
     */
    @Override
    public int deleteBlackCompanyById(Long id)
    {
        BlackCompany blackOri = blackCompanyMapper.selectBlackCompanyById(id);
        //私有黑名单
        String key = RedisUtils.HASH_MOBILE_BLACK+"0"+blackOri.getCompanyId();
        RedisUtils.hash_remove(key,blackOri.getMobile());

        return blackCompanyMapper.deleteBlackCompanyById(id);
    }
}
