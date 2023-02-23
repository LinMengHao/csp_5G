package com.xzkj.platform.khd.service.impl;


import com.xzkj.platform.common.annotation.DataSource;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.enums.DataSourceType;
import com.xzkj.platform.khd.domain.DataStatisticAppChannelVo;
import com.xzkj.platform.khd.mapper.DataStatisticAppChannelServiceMapper;
import com.xzkj.platform.khd.service.DataStatisticAppChannelService;
import com.xzkj.platform.khd.utils.ByBetweenTime;
import com.xzkj.platform.khd.utils.DateUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author WHY
 * @date 2022年03月06日 19:45
 */
@Service
public class DataStatisticAppChannelServiceImpl implements DataStatisticAppChannelService {

    @Resource
    private DataStatisticAppChannelServiceMapper dataStatisticAppChannelServiceMapper;

    @Override
    public Map getMonthStatistics() {

        List<String> monthList = Arrays.asList("1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"); //12个月
        List<Integer> sendTotalList = new ArrayList<>();//提交总数
        List<Integer> reportDelivrdList = new ArrayList<>();//状态成功
        List<Integer> reportUndelivList = new ArrayList<>();//状态失败
        List<Integer> reportUnknownList = new ArrayList<>();//状态未知
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        Long companyId=user.getCompanyId();

        List<DataStatisticAppChannelVo> dataStatisticAppChannelVoList = dataStatisticAppChannelServiceMapper.getMonthStatistics(companyId);//原数据
        String year = DateUtil.getNow("yyyy");//获取当前年份
        String beginTime = year+"-01";//开始日期
        String endTime = year+"-12";//结束日期
        List<DataStatisticAppChannelVo> newDataStatisticAppChannelVoList = yearMont(beginTime,endTime,dataStatisticAppChannelVoList);//新数据

        newDataStatisticAppChannelVoList.forEach(item->{
            sendTotalList.add(item.getSendTotal());
            reportDelivrdList.add(item.getReportDelivrd());
            reportUndelivList.add(item.getReportUndeliv());
            reportUnknownList.add(item.getReportUnknown());
        });

        Map<String,Object> rMap= new HashMap<>();
        rMap.put("monthList",monthList);//月份
        rMap.put("sendTotalList",sendTotalList);//提交总数
        rMap.put("reportDelivrdList",reportDelivrdList);//状态成功
        rMap.put("reportUndelivList",reportUndelivList);//状态失败
        rMap.put("reportUnknownList",reportUnknownList);//状态未知
        rMap.put("sendTotalMax",Collections.max(sendTotalList));//提交总数-最大
        rMap.put("sendTotalMin",Collections.min(sendTotalList));//提交总数-最小
        rMap.put("reportDelivrdMax",Collections.max(reportDelivrdList));//状态成功-最大
        rMap.put("reportDelivrdMin",Collections.min(reportDelivrdList));//状态成功-最小
        rMap.put("reportUndelivMax",Collections.max(reportUndelivList));//状态失败-最大
        rMap.put("reportUndelivMin",Collections.min(reportUndelivList));///状态失败-最大
        rMap.put("reportUnknownMax",Collections.max(reportUnknownList));//状态未知-最大
        rMap.put("reportUnknownMin",Collections.min(reportUnknownList));//状态未知-最大
        return rMap;
    }

    /**
     *  遍历数据
     *  @param beginTime  2022-01
     *  @param endTime    2022-12
     * @param list 数据
     * @return
     */
    @DataSource(value = DataSourceType.SLAVE)
    public List<DataStatisticAppChannelVo> yearMont(String beginTime, String endTime,List<DataStatisticAppChannelVo> list){
        List<DataStatisticAppChannelVo> resultList =new ArrayList<>();

        List<String> timeList = ByBetweenTime.getMonthByBetweenTime(beginTime, endTime);

        if(list == null || list.size() == 0) {
            for (String timeStr : timeList) {
                DataStatisticAppChannelVo dataStatisticAppChannelVo = new DataStatisticAppChannelVo();
                dataStatisticAppChannelVo.setLogDate(timeStr);
                dataStatisticAppChannelVo.setReportDelivrd(0);
                dataStatisticAppChannelVo.setReportUndeliv(0);
                dataStatisticAppChannelVo.setReportUnknown(0);
                dataStatisticAppChannelVo.setSendTotal(0);
                resultList.add(dataStatisticAppChannelVo);
            }
        }else {
            for (String timeStr : timeList) {
                DataStatisticAppChannelVo dataStatisticAppChannelVo = new DataStatisticAppChannelVo();
                dataStatisticAppChannelVo.setLogDate(timeStr);
                for (DataStatisticAppChannelVo dVo : list) {
                    if(timeStr.equals(dVo.getLogDate())){
                        dataStatisticAppChannelVo.setSendTotal(dVo.getSendTotal());
                        dataStatisticAppChannelVo.setReportUnknown(dVo.getReportUnknown());
                        dataStatisticAppChannelVo.setReportUndeliv(dVo.getReportUndeliv());
                        dataStatisticAppChannelVo.setReportDelivrd(dVo.getReportDelivrd());
                        continue;
                    }
                }
                if (dataStatisticAppChannelVo.getSendTotal() == null ||
                    dataStatisticAppChannelVo.getReportDelivrd() == null ||
                    dataStatisticAppChannelVo.getReportUndeliv() == null ||
                    dataStatisticAppChannelVo.getReportUnknown() == null){
                    dataStatisticAppChannelVo.setReportDelivrd(0);
                    dataStatisticAppChannelVo.setReportUndeliv(0);
                    dataStatisticAppChannelVo.setReportUnknown(0);
                    dataStatisticAppChannelVo.setSendTotal(0);
                }
              resultList.add(dataStatisticAppChannelVo);
            }
        }
        return resultList;
    }
}
