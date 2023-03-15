package com.xzkj.platform.mo.task;

import com.alibaba.fastjson.JSONObject;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.khd.domain.MmsSender;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.IMmsSenderService;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.khd.utils.DateUtil;
import com.xzkj.platform.mo.domain.MoInfo;
import com.xzkj.platform.mo.service.IMoInfoService;
import com.xzkj.platform.model.domain.EModelInfo;
import com.xzkj.platform.model.service.IEModelInfoService;
import com.xzkj.platform.redis.RedisUtils;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component("MoTask")
public class MoTask {


    @Autowired
    private IMoInfoService moInfoService;

    @Autowired
    private ITApplicationService tApplicationService;

    @Autowired
    private IMmsSenderService mmsSenderService;

    @Autowired
    private IEModelInfoService eModelInfoService;

    //上行回调
    public void callback(){
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        ThreadContext.bind(manager);
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MoInfo info=new MoInfo();
        Date date1 = new Date();
        String logDate = DateUtil.convertDate3(date1);
        info.setLogDate(logDate);
        info.setStatus(2L);
        //查询出待回调的上行记录
        List<MoInfo> moInfos = moInfoService.selectMoInfoList(info);
        //查找每天上行最近匹配的下行提交时间（最近3天的）
        for (int i = 0; i < moInfos.size(); i++) {
            MoInfo moInfo = moInfos.get(i);
            String serviceCode = moInfo.getServiceCode();
            if (StringUtils.isBlank(serviceCode)){
                continue;
            }
            String mobile = moInfo.getMobile();
            String appExt = serviceCode.substring(8, 12);
            Long appId = moInfo.getAppId();
            //根据拓展码4位判断用户
//            String appName="";
//            Long companyId=0L;
//            Long appId=0L;
//            List<TApplication> app =new ArrayList<>();
//            if ("106816114400013".equals(serviceCode)||"106908324400021".equals(serviceCode)){
//                appName="gzyyV";
//                companyId=107L;
//                appId=16L;
//
//            }else {
//                app=tApplicationService.selectTApplicationByAppExt(appExt);
//                if (app== null|| app.size()<=0){
//                    continue;
//                }
//                appName=app.get(0).getAppName();
//                companyId=app.get(0).getCompanyId();
//                appId=app.get(0).getId();
//            }
            TApplication application = tApplicationService.selectTApplicationByIdN(appId);
            //根据号码，查询近三天的下行记录
            Calendar calendar = Calendar.getInstance();
            for (int j = 1; j <= 3; j++) {
                String date=new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                MmsSender mmsSender=new MmsSender();
                mmsSender.setLogDate(date);
                mmsSender.setDestNumber(mobile);

                //获取可匹配的下行记录
                List<MmsSender> senderList = mmsSenderService.selectMmsSenderList(mmsSender);
                if(senderList!=null && senderList.size()>0){
                    for (int k = 0; k < senderList.size(); k++) {
                        //根据用户过滤
                        MmsSender sender = senderList.get(k);
                        Long appId1 = sender.getAppId();
                        if(appId1 == appId){
                            //用户匹配成功，需要匹配模板
                       /* String modelId = sender.getBatchId();
                        //获取模板下拓展码
                        EModelInfo eModelInfo = eModelInfoService.selectEModelInfoByModelId(modelId);
                        //获取子模版
                        List<EModelInfo> modelInfos=eModelInfoService.selectByPModelId(modelId);
                        for (int k = 0; k < modelInfos.size(); k++) {
                            String extNum = modelInfos.get(k).getExtNum();
                            if(StringUtils.isBlank(extNum)){
                                //下行不走模板自定义拓展码，例如，移动
                                //

                            }else if(serviceCode.length()>=12){
                                //下行走模板自定义拓展码，例如，电信
                                String modelExt = serviceCode.substring(12);
                                if(modelExt.equals(extNum)){
                                    //
                                }
                            }else {

                            }
                        }*/
                            moInfo.setSendTime(sender.getSubmitTime());
                            moInfo.setLogDate(DateUtil.convertDate1(date1));
                            //更新数据库
                            int i1 = moInfoService.updateMoInfoByDay(moInfo);
                            break;
                        }

                    }

                }
                if (moInfo.getSendTime()!=null){
                    break;
                }
                //前一天
                calendar.add(Calendar.DATE, -1);
            }
            Long companyId = application.getCompanyId();
            //组包放入上行回调队列
            JSONObject submitJson=new JSONObject();
            submitJson.put("acc",application.getAppName());
            submitJson.put("serviceNo",serviceCode);
            submitJson.put("mob",mobile);
            submitJson.put("msg",moInfo.getContent());
            Date receiveDate = moInfo.getReceiveTime();
            Date sendDate = moInfo.getSendTime();
            String receiveTime="";
            String sendTime="";
            if(receiveDate!=null){
                receiveTime = sdf3.format(receiveDate);
            }
            if(sendDate!=null){
                sendTime=sdf3.format(sendDate);
            }
            submitJson.put("moTime",receiveTime);

            submitJson.put("sendTime",sendTime);

            String tableName="mms_mo_"+DateUtil.convertDate1(date1);
            submitJson.put("tableName",tableName);
            submitJson.put("moId",moInfo.getMoId());

            System.out.println("上行回调信息："+submitJson.toString());
            //保存到上行队列
            RedisUtils.fifo_push(RedisUtils.FIFO_APP_MO_LIST+companyId,submitJson.toJSONString());
            RedisUtils.hash_incrBy(RedisUtils.HASH_APP_MO_TOTAL, companyId+"", 1);
        }
    }
}
