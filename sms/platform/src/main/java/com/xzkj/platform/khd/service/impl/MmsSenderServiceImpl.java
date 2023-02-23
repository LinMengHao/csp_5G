package com.xzkj.platform.khd.service.impl;

import com.github.pagehelper.util.StringUtil;

import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.khd.domain.MmsSender;
import com.xzkj.platform.khd.mapper.MmsSenderMapper;
import com.xzkj.platform.khd.service.IMmsSenderService;
import com.xzkj.platform.khd.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 下行日志的显示查询Service业务层处理
 * 
 * @author lixl
 * @date 2022-01-24
 */
@Service
public class MmsSenderServiceImpl implements IMmsSenderService
{
    private static final Logger logger = LoggerFactory.getLogger(MmsSenderServiceImpl.class);
    @Autowired
    private MmsSenderMapper mmsSenderMapper;

    /**
     * 查询下行日志的显示查询
     * 
     * @param linkId 下行日志的显示查询主键
     * @return 下行日志的显示查询
     */
    @Override
    public MmsSender selectMmsSenderByLinkId(String linkId)
    {
        return mmsSenderMapper.selectMmsSenderByLinkId(linkId);
    }

    /**
     * 查询下行日志的显示查询列表
     * 
     * @param mmsSender 下行日志的显示查询
     * @return 下行日志的显示查询
     */
    @Override
    public List<MmsSender> selectMmsSenderList(MmsSender mmsSender)
    {
        /*if(mmsSender.getLogDate()==""){
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            mmsSender.setLogDate(dateFormat.format(date));
        }else{
            mmsSender.setLogDate( mmsSender.getLogDate().replaceAll("-",""));
        }*/
        String logDate = mmsSender.getLogDate();
        String startTime = mmsSender.getStartTime();
        String endTime = mmsSender.getEndTime();
        //跨天查询
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        List<String> timeList = new ArrayList<>();
        if(StringUtil.isEmpty(logDate)) {
            if(StringUtils.isNoneBlank(startTime, endTime)){
                try {
                    Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startTime);
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endTime);
                    int gap = (int) ((endDate.getTime() - startDate.getTime()) / (24*60*60*1000));
                    Calendar cld = Calendar.getInstance();
                    cld.setTime(startDate);
                    for (int i = 0; i < gap +1; i++) {
                        timeList.add("mms_sender_"+sdf.format(cld.getTime()));
                        cld.add(Calendar.DATE,1);
                    }
                    mmsSender.setTimeList(timeList);
                }catch (Exception e){
                    logger.error("[MT]时间转换异常.EX=", e);
                }

            }else {
                logDate = DateUtil.convertDate(new Date());
                mmsSender.setLogDate(logDate);
            }
        }else {
            try {
                logDate = DateUtil.convertDate(DateUtil.converDateFromStr3(logDate));
                mmsSender.setLogDate(logDate);
            } catch (ParseException e) {
                logger.error("[MT]时间转换异常.EX=", e);
            }

        }
        //兼容有回调客户状态的记录和老版本数据
        if(StringUtils.isBlank(mmsSender.getCallback())){
            return mmsSenderMapper.selectMmsSenderListOld(mmsSender);
        }

        return mmsSenderMapper.selectMmsSenderList(mmsSender);
    }

    /**
     * 新增下行日志的显示查询
     * 
     * @param mmsSender 下行日志的显示查询
     * @return 结果
     */
    @Override
    public int insertMmsSender(MmsSender mmsSender)
    {
        mmsSender.setCreateTime(DateUtils.getNowDate());
        return mmsSenderMapper.insertMmsSender(mmsSender);
    }

    /**
     * 修改下行日志的显示查询
     * 
     * @param mmsSender 下行日志的显示查询
     * @return 结果
     */
    @Override
    public int updateMmsSender(MmsSender mmsSender)
    {
        return mmsSenderMapper.updateMmsSender(mmsSender);
    }

    /**
     * 批量删除下行日志的显示查询
     * 
     * @param linkIds 需要删除的下行日志的显示查询主键
     * @return 结果
     */
    @Override
    public int deleteMmsSenderByLinkIds(String linkIds)
    {
        return mmsSenderMapper.deleteMmsSenderByLinkIds(Convert.toStrArray(linkIds));
    }

    /**
     * 删除下行日志的显示查询信息
     * 
     * @param linkId 下行日志的显示查询主键
     * @return 结果
     */
    @Override
    public int deleteMmsSenderByLinkId(String linkId)
    {
        return mmsSenderMapper.deleteMmsSenderByLinkId(linkId);
    }
}
