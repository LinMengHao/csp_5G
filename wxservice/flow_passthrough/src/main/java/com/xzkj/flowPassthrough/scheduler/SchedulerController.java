package com.xzkj.flowPassthrough.scheduler;


import com.xzkj.flowPassthrough.service.ICommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@EnableScheduling
public class SchedulerController {
    public static Logger logger = LoggerFactory.getLogger("SchedulerController");
    @Resource
    private ICommonService commonService;

    //每日凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ? ")
//    @Scheduled(initialDelay = 10000, fixedDelay = 5 * 60 * 1000)
    public void createTables() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);//增加一天
        //cal.add(Calendar.MONTH, n);//增加一个月
        String destTableName="order_pass_"+new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        String tableName = "order_pass";
        String createSql = null;
        try {
            List<Map<String, Object>> result = commonService.tableInfoList(tableName);
            if (result != null && result.size() != 0) {
                Map map = (Map) result.get(0);
                createSql = map.containsKey("Create Table") ? (String) map.get("Create Table")
                        : (map.containsKey("create table") ? (String) map.get("create table") :
                        (map.containsKey("CREATE TABLE") ? (String) map.get("CREATE TABLE") : ""));
            }
            if (createSql != null) {
                createSql = createSql.replaceFirst(tableName, destTableName);
                commonService.createNewTable(createSql);
            }
            logger.info("创建数据表成功【{}】建表语句【{}】",destTableName,createSql);
        } catch (Exception ex) {
            logger.info("创建数据表失败【{}】建表语句【{}】异常信息:{}",destTableName,createSql,ex.getMessage());
            ex.printStackTrace();
        }
    }

}
