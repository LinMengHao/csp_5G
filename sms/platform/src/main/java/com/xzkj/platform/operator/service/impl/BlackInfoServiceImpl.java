package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.operator.domain.BlackInfo;
import com.xzkj.platform.operator.mapper.BlackInfoMapper;
import com.xzkj.platform.operator.service.IBlackInfoService;
import com.xzkj.platform.redis.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局黑名单Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Service
public class BlackInfoServiceImpl implements IBlackInfoService 
{
    @Autowired
    private BlackInfoMapper blackInfoMapper;

    /**
     * 查询全局黑名单
     * @param id 全局黑名单主键
     * @return 全局黑名单
     */
    @Override
    public BlackInfo selectBlackInfoById(Long id)
    {
        return blackInfoMapper.selectBlackInfoById(id);
    }

    /**
     * 查询全局黑名单列表
     * 
     * @param blackInfo 全局黑名单
     * @return 全局黑名单
     */
    @Override
    public List<BlackInfo> selectBlackInfoList(BlackInfo blackInfo)
    {
        return blackInfoMapper.selectBlackInfoList(blackInfo);
    }

    /**
     * 新增全局黑名单
     * 
     * @param blackInfo 全局黑名单
     * @return 结果
     */
    @Override
    public int insertBlackInfo(BlackInfo blackInfo)
    {
        String mobiles = blackInfo.getMobile();
        String[] mobArr = mobiles.replaceAll("，",",").replaceAll("\r\n",",").split(",");
        List<BlackInfo> blackInfoList = new ArrayList<BlackInfo>();
        for (String mobile:mobArr){
            if(StringUtils.isBlank(mobile)){
                continue;
            }
            BlackInfo one = new BlackInfo();
            one.setMobile(mobile.trim());
            one.setRuleLevel(blackInfo.getRuleLevel());
            one.setSource(blackInfo.getSource());
            one.setRemark(blackInfo.getRemark());
            one.setUpdateTime(DateUtils.getNowDate());
            one.setCreateTime(DateUtils.getNowDate());
            blackInfoList.add(one);
            //全局黑名单
            String key = RedisUtils.HASH_MOBILE_BLACK+one.getRuleLevel();
            RedisUtils.hash_set(key,one.getMobile(),"1");
        }
        int count=0;
        if(blackInfoList.size()==1){
            count = blackInfoMapper.insertBlackInfo(blackInfoList.get(0));
        }else{
            count = blackInfoMapper.insertBlackInfos(blackInfoList);
        }

        return count;
    }

    /**
     * 修改全局黑名单
     * 
     * @param blackInfo 全局黑名单
     * @return 结果
     */
    @Override
    public int updateBlackInfo(BlackInfo blackInfo)
    {
        BlackInfo blackOri = blackInfoMapper.selectBlackInfoById(blackInfo.getId());
        //全局黑名单
        String key = RedisUtils.HASH_MOBILE_BLACK+blackOri.getRuleLevel();
        RedisUtils.hash_remove(key,blackOri.getMobile());
        key = RedisUtils.HASH_MOBILE_BLACK+blackInfo.getRuleLevel();
        RedisUtils.hash_set(key,blackInfo.getMobile(),"1");

        blackInfo.setUpdateTime(DateUtils.getNowDate());
        return blackInfoMapper.updateBlackInfo(blackInfo);
    }

    /**
     * 批量删除全局黑名单
     * 
     * @param ids 需要删除的全局黑名单主键
     * @return 结果
     */
    @Override
    public int deleteBlackInfoByIds(String ids)
    {
        String[] idsArr = Convert.toStrArray(ids);

        List<BlackInfo> list = blackInfoMapper.selectBlackInfoListByIds(idsArr);
        for(BlackInfo blackOri:list){
            //全局黑名单
            String key = RedisUtils.HASH_MOBILE_BLACK+blackOri.getRuleLevel();
            RedisUtils.hash_remove(key,blackOri.getMobile());
        }
        return blackInfoMapper.deleteBlackInfoByIds(idsArr);
    }

    /**
     * 删除全局黑名单信息
     * 
     * @param id 全局黑名单主键
     * @return 结果
     */
    @Override
    public int deleteBlackInfoById(Long id)
    {
        BlackInfo blackOri = blackInfoMapper.selectBlackInfoById(id);
        //全局黑名单
        String key = RedisUtils.HASH_MOBILE_BLACK+blackOri.getRuleLevel();
        RedisUtils.hash_remove(key,blackOri.getMobile());

        return blackInfoMapper.deleteBlackInfoById(id);
    }
}
