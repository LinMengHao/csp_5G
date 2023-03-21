package com.xzkj.platform.khd.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.khd.domain.Statistics;
import com.xzkj.platform.khd.service.StatisticsService;
import com.xzkj.platform.khd.utils.DateUtil;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/mmskhd/statistics")
public class StatisticController extends BaseController {

    private String prefix = "mmskhd/statistics";

    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private IChannelService channelService;

    @RequiresPermissions("mmskhd:statistics:list")
    @GetMapping()
    public String statistics(ModelMap mmap)
    {
        List<Channel> channellist = channelService.selectChannelListAll(0L);
        mmap.put("channellist", channellist);
        return prefix + "/statistics";
    }

    @RequiresPermissions("mmskhd:statistics:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo findStatisticList(Statistics statistics) {
        logger.debug("[BINS][STATISTIC] statistics="+ JSONObject.toJSONString(statistics));
        long time = System.currentTimeMillis();
        startPage();

        if (StringUtil.isEmpty(statistics.getStartTime()) || StringUtil.isEmpty(statistics.getEndTime())) {
            statistics.setStartTime(DateUtil.convertDate3(new Date()));
            statistics.setEndTime(DateUtil.convertDate3(new Date()));
        }

        List<Statistics> statisticsList = new ArrayList<Statistics>();
        try {
            statisticsList = statisticsService.findStatisticsList(statistics);
        } catch (Exception e) {
            logger.error("[BINS][APP_STATISTIC] ", e);
        }
        //总数
        Integer sendTotalSum=0;
        //成功
        Integer delivrdSum=0;
        //失败
        Integer unDelivrdSum=0;
        //黑名单
        Integer blackSum=0;
        //未知
        Integer unknownSum=0;
        //限频
        Integer loaddelivrdSum=0;

        NumberFormat numberFormat=NumberFormat.getInstance();
        //百分比保留两位
        numberFormat.setMaximumFractionDigits(2);
        for(Statistics stat:statisticsList){
            sendTotalSum+=stat.getSendTotal();
            delivrdSum+=stat.getReportDelivrd();
            unDelivrdSum+=stat.getReportUndeliv();
            blackSum+=stat.getReportBlack();
            unknownSum+=stat.getReportUnknown();
            loaddelivrdSum+=stat.getLoadDelivrd();


            //成功率
            String successRate = numberFormat.format((float) stat.getReportDelivrd() / (float) stat.getSendTotal()*100);
            //失败率
            String failureRate = numberFormat.format((float) stat.getReportUndeliv() / (float) stat.getSendTotal()*100);
            //未知率
            String unknownRate = numberFormat.format((float) stat.getReportUnknown() / (float) stat.getSendTotal()*100);
            //触黑率
            String backRate = numberFormat.format((float) stat.getReportBlack() / (float) stat.getSendTotal()*100);
            //限频限次率
            String limitRate = numberFormat.format((float) stat.getLoadDelivrd() / (float) stat.getSendTotal()*100);

            stat.setSuccessRate(successRate+"%");
            stat.setFailureRate(failureRate+"%");
            stat.setUnknownRate(unknownRate+"%");
            stat.setBlackRate(backRate+"%");
            stat.setLimitRate(limitRate+"%");
        }
        Statistics stat=new Statistics();
        stat.setAppName("总计");
        stat.setSendTotal(sendTotalSum);
        stat.setReportDelivrd(delivrdSum);
        stat.setReportUndeliv(unDelivrdSum);
        stat.setReportBlack(blackSum);
        stat.setReportUnknown(unknownSum);
        stat.setLoadDelivrd(loaddelivrdSum);

        //成功率
        String successRate = numberFormat.format((float) delivrdSum / (float) sendTotalSum*100);
        //失败率
        String failureRate = numberFormat.format((float) unDelivrdSum / (float) sendTotalSum*100);
        //未知率
        String unknownRate = numberFormat.format((float) unknownSum / (float) sendTotalSum*100);
        //触黑率
        String backRate = numberFormat.format((float) blackSum / (float) stat.getSendTotal()*100);
        //限频限次率
        String limitRate = numberFormat.format((float) loaddelivrdSum / (float) stat.getSendTotal()*100);
        stat.setSuccessRate(successRate+"%");
        stat.setFailureRate(failureRate+"%");
        stat.setUnknownRate(unknownRate+"%");
        stat.setBlackRate(backRate+"%");
        stat.setLimitRate(limitRate+"%");
        if("no".equals(statistics.getStatisticType())){
            statisticsList.add(stat);
        }

        logger.debug("[BINS][STATISTIC] find list size="+statisticsList.size()+",time="+(System.currentTimeMillis()-time));
        return getDataTable(statisticsList);
    }

    /**
     * 导出下行日志的显示查询列表
     */
    @RequiresPermissions("mmskhd:statistics:export")
    @Log(title = "统计导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Statistics statistics)
    {
        logger.debug("[BINS][STATISTIC] statistics="+ JSONObject.toJSONString(statistics));
        if (StringUtil.isEmpty(statistics.getStartTime()) || StringUtil.isEmpty(statistics.getEndTime())) {
            statistics.setStartTime(DateUtil.convertDate3(new Date()));
            statistics.setEndTime(DateUtil.convertDate3(new Date()));
        }
        List<Statistics> statisticsList = new ArrayList<Statistics>();
        try {
            statisticsList = statisticsService.findStatisticsList(statistics);
        } catch (Exception e) {
            logger.error("[BINS][APP_STATISTIC] ", e);
        }
        Integer sendTotalSum=0;
        Integer delivrdSum=0;
        Integer unDelivrdSum=0;
        Integer blackSum=0;
        Integer unknownSum=0;
        Integer loaddelivrdSum=0;

        for(Statistics stat:statisticsList){
            sendTotalSum+=stat.getSendTotal();
            delivrdSum+=stat.getReportDelivrd();
            unDelivrdSum+=stat.getReportUndeliv();
            blackSum+=stat.getReportBlack();
            unknownSum+=stat.getReportUnknown();
            loaddelivrdSum+=stat.getLoadDelivrd();
        }
        Statistics stat=new Statistics();
        stat.setAppName("总计");
        stat.setSendTotal(sendTotalSum);
        stat.setReportDelivrd(delivrdSum);
        stat.setReportUndeliv(unDelivrdSum);
        stat.setReportBlack(blackSum);
        stat.setReportUnknown(unknownSum);
        stat.setLoadDelivrd(loaddelivrdSum);
        statisticsList.add(stat);
        ExcelUtil<Statistics> util = new ExcelUtil<Statistics>(Statistics.class);
        return util.exportExcel(statisticsList, "统计导出");
    }

}
