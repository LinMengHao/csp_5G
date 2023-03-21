package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.PageUtils;
import com.xzkj.platform.common.utils.ShiroUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.domain.LimitBand;
import com.xzkj.platform.operator.mapper.LimitBandMapper;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.operator.service.ILimitBandService;
import com.xzkj.platform.redis.RedisUtils;
import com.xzkj.platform.system.service.ISysDeptService;
import com.xzkj.platform.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 频次限制Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-12-04
 */
@Service
public class LimitBandServiceImpl implements ILimitBandService 
{
    @Autowired
    private LimitBandMapper limitBandMapper;
    @Autowired
    private IChannelService channelService;
    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private ITApplicationService tApplicationService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询频次限制
     * 
     * @param id 频次限制主键
     * @return 频次限制
     */
    @Override
    public LimitBand selectLimitBandById(Long id)
    {
        LimitBand limitBand = limitBandMapper.selectLimitBandById(id);
        SysDept dept = deptService.selectDeptById(limitBand.getCompanyId());
        TApplication app = tApplicationService.selectTApplicationById(limitBand.getAppId());
        SysUser user = sysUserService.selectUserById(limitBand.getUserId());
        limitBand.setCompanyName(dept!=null?dept.getCompanyName():"未知");
        limitBand.setAppName(app!=null?app.getAppName():limitBand.getAppId()+"");
        limitBand.setUserName(user!=null?user.getUserName():limitBand.getUserId()+"");

        return limitBand;
    }

    /**
     * 查询频次限制列表
     * 
     * @param limitBand 频次限制
     * @return 频次限制
     */
    @Override
    public List<LimitBand> selectLimitBandList(LimitBand limitBand)
    {
        List<LimitBand> list = limitBandMapper.selectLimitBandList(limitBand);
        PageUtils.clearPage();
        List<Channel> channellist = channelService.selectChannelListAll(0L);
        Map<Long,String> map = new HashMap<Long,String>();
        for (Channel channel:channellist){
            map.put(channel.getId(),channel.getId()+":"+channel.getChannelName());
        }

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

        for (LimitBand en:list){
            String companyName="全局";
            String accountName="全局";
            //限制类型 1-客户全局 2-通道全局 3-客户账号 4-通道账号
            if(en.getLimitType()==3){
                companyName = mapDept.containsKey(en.getCompanyId())?mapDept.get(en.getCompanyId()):en.getCompanyId()+":";
                accountName = mapApp.containsKey(en.getAppId())?mapApp.get(en.getAppId()):en.getAppId()+":";
            }else if(en.getLimitType()==4){
                accountName = map.containsKey(en.getChannelId())?map.get(en.getChannelId()):en.getChannelId()+":";
            }
            en.setCompanyName(companyName);
            en.setAccountName(accountName);
            en.setUserName(mapUser.containsKey(en.getUserId())?mapUser.get(en.getUserId()):en.getUserId()+"");
        }
        return list;
    }

    /**
     * 新增频次限制
     * 
     * @param limitBand 频次限制
     * @return 结果
     */
    @Override
    public int insertLimitBand(LimitBand limitBand)
    {
        // 获取当前的用户ID
        Long userId = ShiroUtils.getUserId();
        limitBand.setUserId(userId);
        limitBand.setUpdateTime(DateUtils.getNowDate());
        limitBand.setCreateTime(DateUtils.getNowDate());
        int result = limitBandMapper.insertLimitBand(limitBand);
        if(result>0){
            String key = getKeyLimit(limitBand);
            RedisUtils.hash_set(key,limitBand.getDays()+"",limitBand.getTimes()+"");
        }
        return result;
    }

    /**
     * 修改频次限制
     * 
     * @param limitBand 频次限制
     * @return 结果
     */
    @Override
    public int updateLimitBand(LimitBand limitBand)
    {
        LimitBand limitOne = limitBandMapper.selectLimitBandById(limitBand.getId());

        // 获取当前的用户ID
        Long userId = ShiroUtils.getUserId();
        limitBand.setUserId(userId);
        limitBand.setUpdateTime(DateUtils.getNowDate());
        int result = limitBandMapper.updateLimitBand(limitBand);
        if(result>0){
            String keyOne = getKeyLimit(limitOne);
            RedisUtils.hash_remove(keyOne,limitOne.getDays()+"");

            String key = getKeyLimit(limitBand);
            RedisUtils.hash_set(key,limitBand.getDays()+"",limitBand.getTimes()+"");
        }
        return result;
    }
    private String getKeyLimit(LimitBand limitBand){
        String key = "";
        switch (limitBand.getLimitType()){
            case 1:
                key = RedisUtils.HASH_BAND_LIMIT_APP+"0:0";
                break;
            case 2:
                key = RedisUtils.HASH_BAND_LIMIT_CHANNEL+"0:0";
                break;
            case 3:
                key = RedisUtils.HASH_BAND_LIMIT_APP+limitBand.getCompanyId()+":"+limitBand.getAppId();
                break;
            case 4:
                key = RedisUtils.HASH_BAND_LIMIT_CHANNEL+"0:"+limitBand.getChannelId();
                break;
        }
        return key;
    }

    /**
     * 批量删除频次限制
     * 
     * @param ids 需要删除的频次限制主键
     * @return 结果
     */
    @Override
    public int deleteLimitBandByIds(String ids)
    {
        return limitBandMapper.deleteLimitBandByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除频次限制信息
     * 
     * @param id 频次限制主键
     * @return 结果
     */
    @Override
    public int deleteLimitBandById(Long id)
    {
        return limitBandMapper.deleteLimitBandById(id);
    }
}
