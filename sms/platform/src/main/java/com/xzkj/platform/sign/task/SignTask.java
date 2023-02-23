package com.xzkj.platform.sign.task;


import com.alibaba.fastjson.JSONObject;
import com.xzkj.platform.common.config.ServerConfig;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.redis.RedisUtils;
import com.xzkj.platform.sign.domain.EModelSign;
import com.xzkj.platform.sign.service.IEModelSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 定时任务调度测试
 * 
 * @author ruoyi
 */
@Component("signTask")
public class SignTask
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
    private ITApplicationService tApplicationService;

    //策略回调签名状态
    public void callback(){
        EModelSign eModelSign=new EModelSign();
        eModelSign.setStatus(3l);
        List<EModelSign> list= eModelSignService.selectPEModelSignList(eModelSign);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
            EModelSign sign = list.get(i);
            String idea = sign.getIdea();
            if(StringUtils.isBlank(idea)) continue;
            //策略
            String[] signIdList = idea.split(",");
            int k=0;
            for (int j = 0; j < signIdList.length; j++) {
                EModelSign modelSign=eModelSignService.selectEModelSignBySignId(signIdList[j]);
                if (modelSign==null){
                    break;
                }
                Long status = modelSign.getStatus();
                if(!(status==1||status==5)){
                    break;
                }
                Long toCmcc = modelSign.getToCmcc();
                Long toTelecom = modelSign.getToTelecom();
                Long toInternational = modelSign.getToInternational();
                Long toUnicom = modelSign.getToUnicom();
                if(toCmcc==1){
                    sign.setToCmcc(1l);
                }
                if(toUnicom==1){
                    sign.setToUnicom(1l);
                }
                if(toTelecom==1){
                    sign.setToTelecom(1l);
                }
                if(toInternational==1){
                    sign.setToInternational(1l);
                }

                ++k;
            }
            if (k==signIdList.length){
                //TODO 回调
                String backUrl = sign.getBackUrl();
                if (StringUtils.isEmpty(backUrl)){
                    continue;
                }
                Long toCmcc = sign.getToCmcc();
                Long toUnicom = sign.getToUnicom();
                Long toTelecom = sign.getToTelecom();
                Long toInternational = sign.getToInternational();
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

                EModelSign eModelSign1 = new EModelSign();
                eModelSign1.setId(sign.getId());
                eModelSign1.setStatus(1l);
                eModelSign1.setToCmcc(toCmcc);
                eModelSign1.setToTelecom(toTelecom);
                eModelSign1.setToUnicom(toUnicom);
                eModelSign1.setToInternational(toInternational);
                int i1 = eModelSignService.updateEModelSign(eModelSign1);


                sign.setToCmcc(toCmcc);
                sign.setToUnicom(toUnicom);
                sign.setToTelecom(toTelecom);
                sign.setToInternational(toInternational);
                sign.setStatus(1L);
                sign.setInfo("审核成功");
                Long companyId = sign.getCompanyId();
                String s = JSONObject.toJSONString(sign);
                RedisUtils.fifo_push(RedisUtils.FIFO_SIGN_MT_CLIENT+companyId,s);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SIGN_MT_COUNT, companyId+"", 1);
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
        System.out.println("执行签名自动审核回调方法");
    }
}
