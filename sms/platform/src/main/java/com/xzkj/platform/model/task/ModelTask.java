package com.xzkj.platform.model.task;


import com.alibaba.fastjson.JSONObject;
import com.xzkj.platform.common.config.ServerConfig;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.model.domain.EModelInfo;
import com.xzkj.platform.model.service.IEModelInfoService;
import com.xzkj.platform.operator.domain.ModelRelated;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.operator.service.IModelRelatedService;
import com.xzkj.platform.redis.RedisUtils;
import com.xzkj.platform.sign.service.IEModelSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 定时任务调度测试
 * 
 * @author ruoyi
 */
@Component("modelTask")
public class ModelTask
{
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private IChannelService channelService;
    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private IEModelSignService eModelSignService;

    @Autowired
    private IEModelInfoService eModelInfoService;
    @Autowired
    private ITApplicationService tApplicationService;

    @Autowired
    private IModelRelatedService modelRelatedService;

    //策略回调签名状态
    public void callback(){
        EModelInfo eModelInfo=new EModelInfo();
        eModelInfo.setStatus(3l);
        List<EModelInfo> eModelInfos = eModelInfoService.selectPEModelInfoList(eModelInfo);
        for (int i = 0; i < eModelInfos.size(); i++) {
            System.out.println(eModelInfos.get(i).toString());
            EModelInfo info=eModelInfos.get(i);
            String idea = info.getIdea();
            if(StringUtils.isBlank(idea)) continue;
            //策略
            String[] modelIdList = idea.split(",");
            int k=0;
            for (int j = 0; j < modelIdList.length; j++) {
                EModelInfo modelInfo=eModelInfoService.selectEModelInfoByModelId(modelIdList[j]);
                if(modelInfo==null){
                    break;
                }
                Long status = modelInfo.getStatus();
                if(status != 1){
                    break;
                }
                Long toCmcc = modelInfo.getToCmcc();
                Long toTelecom = modelInfo.getToTelecom();
                Long toInternational = modelInfo.getToInternational();
                Long toUnicom = modelInfo.getToUnicom();
                if(toCmcc==1){
                    info.setToCmcc(1l);
                }
                if(toUnicom==1){
                    info.setToUnicom(1l);
                }
                if(toTelecom==1){
                    info.setToTelecom(1l);
                }
                if(toInternational==1){
                    info.setToInternational(1l);
                }
                ++k;
            }
            if(k==modelIdList.length){

                Long toCmcc = info.getToCmcc();
                Long toUnicom = info.getToUnicom();
                Long toTelecom = info.getToTelecom();
                Long toInternational = info.getToInternational();
                if(null==toCmcc ||toCmcc!=1){
                    toCmcc=2l;
                }
                if(null==toUnicom ||toUnicom!=1){
                    toUnicom=2l;
                }
                if(null==toTelecom ||toTelecom!=1){
                    toTelecom=2l;
                }
                if(null==toInternational ||toInternational!=1){
                    toInternational=2l;
                }

                EModelInfo eModelInfo1 = new EModelInfo();
                eModelInfo1.setId(info.getId());
                eModelInfo1.setStatus(1L);
                eModelInfo1.setToCmcc(toCmcc);
                eModelInfo1.setToTelecom(toTelecom);
                eModelInfo1.setToUnicom(toUnicom);
                eModelInfo1.setToInternational(toInternational);
                int i1 = eModelInfoService.updateEModelInfo(eModelInfo1);
                TApplication app = tApplicationService.selectTApplicationById(info.getAppId());
                info.setAppName(app.getAppName());
                info.setToCmcc(toCmcc);
                info.setToUnicom(toUnicom);
                info.setToTelecom(toTelecom);
                info.setToInternational(toInternational);
                info.setStatus(1L);
                info.setInfo("审核成功");
                Long companyId = info.getCompanyId();
                String s = JSONObject.toJSONString(info);
                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,s);
                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);

            }
        }
    }

    //主动向通道侧查询状态，判断模版是否有效，参数1为有效期，电信模版使用该参数，默认90天,参数2为通道查询id，用英文逗号隔开,默认1，2，6（移动的主动查询通道id）
    public void modelStatusQuery(Long days,String channelIds){
        //查询所有状态为成功的子模版
        EModelInfo info = new EModelInfo();
        info.setStatus(1L);
        List<EModelInfo>list=eModelInfoService.selectSonModelInfoList(info);
        for (int i = 0; i < list.size(); i++) {
            EModelInfo info1 = list.get(i);
            Long channelId = info1.getChannelId()!=null?info1.getChannelId():0L;
            Long companyId = info1.getCompanyId()!=null?info1.getCompanyId():0L;
            Long appId = info1.getAppId();
            if(StringUtils.isBlank(channelIds)){
                channelIds="1,2,6";
            }
            boolean contains = channelIds.contains(String.valueOf(channelId));
            if(contains){
                String body = JSONObject.toJSONString(info1);
                RedisUtils.fifo_push(RedisUtils.FIFO_QUERY_MODEL_LIST+companyId+":"+appId+":"+channelId,body);
                RedisUtils.hash_incrBy(RedisUtils.HASH_QUERY_MODEL_TOTAL, companyId+"_"+appId+"_"+channelId, 1);
            }else {
                //判断有效期 90天
                Long day=days!=null?days:90L;
                Date createTime = info1.getCreateTime();
                Date now = new Date();
                int i1 = differentDays(createTime, now);
                if(i1>day){
                    //过期
                    EModelInfo eModelInfo = new EModelInfo();
                    eModelInfo.setId(info1.getId());
                    eModelInfo.setStatus(2L);
                    eModelInfo.setInfo("模版失效，通道侧模版过期");
                    int i2 = eModelInfoService.updateEModelInfo(eModelInfo);
                    //改变模版映射表的状态
                    String pModelId = !StringUtils.isBlank(info1.getpModelId())?info1.getpModelId():"";
                    String channelModelId = !StringUtils.isBlank(info1.getChannelModelId())?info1.getChannelModelId():"";
                    ModelRelated modelRelated = new ModelRelated();
                    modelRelated.setModelId(pModelId);
                    modelRelated.setChannelModelId(channelModelId);
                    modelRelated.setChannelId(channelId);
                    modelRelated.setStatus(2L);
                    int i3=modelRelatedService.updateModelRelatedByQuery(modelRelated);
                    if(i3>0&&i2>0){
                        System.out.println(info1.getModelId()+"模版过期");
                    }
                }
            }
        }
    }

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i)
    {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void ryParams(String params)
    {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams()
    {
        System.out.println("执行模版自动审核回调方法");
    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //不同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else    //同一年
        {
            System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }
}
