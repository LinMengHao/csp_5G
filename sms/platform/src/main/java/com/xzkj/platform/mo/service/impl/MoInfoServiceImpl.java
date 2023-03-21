package com.xzkj.platform.mo.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.github.pagehelper.util.StringUtil;
import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.khd.service.impl.MmsSenderServiceImpl;
import com.xzkj.platform.khd.utils.DateUtil;
import com.xzkj.platform.model.domain.EModelInfo;
import com.xzkj.platform.system.service.ISysDeptService;
import com.xzkj.platform.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xzkj.platform.mo.mapper.MoInfoMapper;
import com.xzkj.platform.mo.domain.MoInfo;
import com.xzkj.platform.mo.service.IMoInfoService;
import com.xzkj.platform.common.core.text.Convert;

/**
 * 上行Service业务层处理
 * 
 * @author lmh
 * @date 2023-03-09
 */
@Service
public class MoInfoServiceImpl implements IMoInfoService 
{
    private static final Logger logger = LoggerFactory.getLogger(MoInfoServiceImpl.class);
    @Autowired
    private MoInfoMapper moInfoMapper;

    @Autowired
    private ITApplicationService tApplicationService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询上行
     *
     * @return 上行
     */
    @Override
    public MoInfo selectMoInfoById(MoInfo moInfo)
    {
        return moInfoMapper.selectMoInfoById(moInfo);
    }

    /**
     * 查询上行列表
     * 
     * @param moInfo 上行
     * @return 上行
     */
    @Override
    public List<MoInfo> selectMoInfoList(MoInfo moInfo)
    {
        String logDate = moInfo.getLogDate();
        String startTime = moInfo.getStartTime();
        String endTime = moInfo.getEndTime();
        List<String> timeList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        if(StringUtil.isEmpty(logDate)) {
            if(StringUtils.isNoneBlank(startTime, endTime)){
                try {
                    Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startTime);
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endTime);
                    int gap = (int) ((endDate.getTime() - startDate.getTime()) / (30*24*60*60*1000));
                    Calendar cld = Calendar.getInstance();
                    Calendar cld2 = Calendar.getInstance();
                    cld2.setTime(endDate);
                    cld.setTime(startDate);
                    int year1 = cld.get(Calendar.YEAR);
                    int year2 = cld2.get(Calendar.YEAR);
                    int i1 = cld.get(Calendar.MONTH);
                    int i2 = cld2.get(Calendar.MONTH);
                    int year=year2-year1;
                    int end= year*12+i2;
                    if(year1==year2){
                        for (int i = i1; i <= end; i++) {
                            timeList.add("mms_mo_"+sdf.format(cld.getTime()));
                            cld.add(Calendar.MONTH,1);
                        }
                    }

                    moInfo.setTimeList(timeList);
                }catch (Exception e){
                    logger.error("[MT]时间转换异常.EX=", e);
                }

            }else {
                logDate = DateUtil.convertDate1(new Date());
                moInfo.setLogDate(logDate);
            }
        }else {
            try {
                logDate = DateUtil.convertDate1(DateUtil.converDateFromStr3(logDate));
                moInfo.setLogDate(logDate);
            } catch (ParseException e) {
                logger.error("[MT]时间转换异常.EX=", e);
            }

        }
        List<MoInfo> list = moInfoMapper.selectMoInfoList(moInfo);


        return list;
    }

    /**
     * 新增上行
     * 
     * @param moInfo 上行
     * @return 结果
     */
    @Override
    public int insertMoInfo(MoInfo moInfo)
    {
        String logDate = DateUtil.convertDate1(moInfo.getReceiveTime());
        moInfo.setLogDate(logDate);
        moInfo.setCreateTime(DateUtils.getNowDate());
        moInfo.setMoId(String.valueOf(new Date().getTime()));
        return moInfoMapper.insertMoInfo(moInfo);
    }

    /**
     * 修改上行
     * 
     * @param moInfo 上行
     * @return 结果
     */
    @Override
    public int updateMoInfo(MoInfo moInfo)
    {
        moInfo.setUpdateTime(DateUtils.getNowDate());
        return moInfoMapper.updateMoInfo(moInfo);
    }

    @Override
    public int updateMoInfoByDay(MoInfo moInfo)
    {
        moInfo.setUpdateTime(DateUtils.getNowDate());
        return moInfoMapper.updateMoInfoByDay(moInfo);
    }

    /**
     * 批量删除上行
     * 
     * @param ids 需要删除的上行主键
     * @return 结果
     */
    @Override
    public int deleteMoInfoByIds(String ids)
    {
        return moInfoMapper.deleteMoInfoByIds(Convert.toStrArray(ids),DateUtil.convertDate1(new Date()));
    }

    /**
     * 删除上行信息
     * 
     * @param id 上行主键
     * @return 结果
     */
    @Override
    public int deleteMoInfoById(String id)
    {
        return moInfoMapper.deleteMoInfoById(id);
    }
}
