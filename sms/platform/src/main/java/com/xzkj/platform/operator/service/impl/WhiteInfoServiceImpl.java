package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.PageUtils;
import com.xzkj.platform.operator.domain.WhiteInfo;
import com.xzkj.platform.operator.mapper.WhiteInfoMapper;
import com.xzkj.platform.operator.service.IWhiteInfoService;
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
 * 白名单Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Service
public class WhiteInfoServiceImpl implements IWhiteInfoService 
{
    @Autowired
    private WhiteInfoMapper whiteInfoMapper;
    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询白名单
     * 
     * @param id 白名单主键
     * @return 白名单
     */
    @Override
    public WhiteInfo selectWhiteInfoById(Long id)
    {
        WhiteInfo whiteInfo = whiteInfoMapper.selectWhiteInfoById(id);
        if(whiteInfo.getCompanyId()==0){
            whiteInfo.setCompanyName("全局白名单");
        }else{
            SysDept dept = deptService.selectDeptById(whiteInfo.getCompanyId());
            whiteInfo.setCompanyName(dept!=null?dept.getCompanyName():"未知");
        }

        return whiteInfo;
    }

    /**
     * 查询白名单列表
     * 
     * @param whiteInfo 白名单
     * @return 白名单
     */
    @Override
    public List<WhiteInfo> selectWhiteInfoList(WhiteInfo whiteInfo)
    {
        if(whiteInfo.getCompanyId()==0){//全局白名单
            whiteInfo.setCompanyId(null);
        }
        List<WhiteInfo> list = whiteInfoMapper.selectWhiteInfoList(whiteInfo);
        PageUtils.clearPage();
        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Map<Long,String> mapDept = new HashMap<Long,String>();
        for (SysDept dept:deptList){
            mapDept.put(dept.getCompanyId(),dept.getCompanyId()+":"+dept.getCompanyName());
        }
        for (WhiteInfo en:list){
            if(en.getCompanyId()==0){
                en.setCompanyName("全局白名单");
            }else{
                en.setCompanyName(mapDept.containsKey(en.getCompanyId())?mapDept.get(en.getCompanyId()):en.getCompanyId()+":");
            }
        }
        return list;
    }

    /**
     * 新增白名单
     * 
     * @param whiteInfo 白名单
     * @return 结果
     */
    @Override
    public int insertWhiteInfo(WhiteInfo whiteInfo)
    {
        if(whiteInfo.getCompanyId()==null){
            whiteInfo.setCompanyId(0L);
        }
        String mobiles = whiteInfo.getMobile();
        String[] mobArr = mobiles.replaceAll("，",",").replaceAll("\r\n",",").split(",");
        List<WhiteInfo> whiteInfoList = new ArrayList<WhiteInfo>();
        for (String mobile:mobArr){
            if(StringUtils.isBlank(mobile)){
                continue;
            }
            WhiteInfo one = new WhiteInfo();
            one.setCompanyId(whiteInfo.getCompanyId());
            one.setMobile(mobile.trim());
            one.setRemark(whiteInfo.getRemark());
            one.setUpdateTime(DateUtils.getNowDate());
            one.setCreateTime(DateUtils.getNowDate());
            whiteInfoList.add(one);
            //白名单
            String key = RedisUtils.HASH_MOBILE_WHITE+one.getCompanyId()+":0";
            RedisUtils.hash_set(key,one.getMobile(),"1");
        }
        int count=0;
        if(whiteInfoList.size()==1){
            count = whiteInfoMapper.insertWhiteInfo(whiteInfoList.get(0));
        }else{
            count = whiteInfoMapper.insertWhiteInfos(whiteInfoList);
        }

        return count;
    }

    /**
     * 修改白名单
     * 
     * @param whiteInfo 白名单
     * @return 结果
     */
    @Override
    public int updateWhiteInfo(WhiteInfo whiteInfo)
    {
        WhiteInfo whiteOri = whiteInfoMapper.selectWhiteInfoById(whiteInfo.getId());
        //白名单
        String key = RedisUtils.HASH_MOBILE_WHITE+whiteOri.getCompanyId()+":0";
        RedisUtils.hash_remove(key,whiteOri.getMobile());

        if(whiteInfo.getCompanyId()==null){
            whiteInfo.setCompanyId(0L);
        }
        key = RedisUtils.HASH_MOBILE_WHITE+whiteInfo.getCompanyId()+":0";
        RedisUtils.hash_set(key,whiteInfo.getMobile(),"1");

        whiteInfo.setUpdateTime(DateUtils.getNowDate());
        return whiteInfoMapper.updateWhiteInfo(whiteInfo);
    }

    /**
     * 批量删除白名单
     * 
     * @param ids 需要删除的白名单主键
     * @return 结果
     */
    @Override
    public int deleteWhiteInfoByIds(String ids)
    {
        String[] idsArr = Convert.toStrArray(ids);
        List<WhiteInfo> list = whiteInfoMapper.selectWhiteInfoListByIds(idsArr);
        for (WhiteInfo whiteOri :list){
            //白名单
            String key = RedisUtils.HASH_MOBILE_WHITE+whiteOri.getCompanyId()+":0";
            RedisUtils.hash_remove(key,whiteOri.getMobile());
        }
        return whiteInfoMapper.deleteWhiteInfoByIds(idsArr);
    }

    /**
     * 删除白名单信息
     * 
     * @param id 白名单主键
     * @return 结果
     */
    @Override
    public int deleteWhiteInfoById(Long id)
    {
        WhiteInfo whiteOri = whiteInfoMapper.selectWhiteInfoById(id);
        //白名单
        String key = RedisUtils.HASH_MOBILE_WHITE+whiteOri.getCompanyId()+":0";
        RedisUtils.hash_remove(key,whiteOri.getMobile());

        return whiteInfoMapper.deleteWhiteInfoById(id);
    }
}
