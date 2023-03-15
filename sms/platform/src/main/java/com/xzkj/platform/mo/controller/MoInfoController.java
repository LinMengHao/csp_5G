package com.xzkj.platform.mo.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.khd.utils.DateUtil;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.redis.RedisUtils;
import com.xzkj.platform.system.service.ISysDeptService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.mo.domain.MoInfo;
import com.xzkj.platform.mo.service.IMoInfoService;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.common.core.page.TableDataInfo;

/**
 * 上行Controller
 * 
 * @author lmh
 * @date 2023-03-09
 */
@Controller
@RequestMapping("/mo/mo")
public class MoInfoController extends BaseController
{
    private String prefix = "mo/mo";

    @Autowired
    private IMoInfoService moInfoService;

    @Autowired
    private IChannelService channelService;

    @Autowired
    private ITApplicationService tApplication;

    @Autowired
    private ISysDeptService deptService;

    @RequiresPermissions("mo:mo:view")
    @GetMapping()
    public String mo(ModelMap mmap)
    {
        List<Channel> channels = channelService.selectChannelList(0L);
        mmap.put("channellist", channels);
        return prefix + "/mo";
    }

    /**
     * 查询上行列表
     */
    @RequiresPermissions("mo:mo:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(MoInfo moInfo)
    {
        startPage();
        List<MoInfo> list = moInfoService.selectMoInfoList(moInfo);

        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Map<Long,String> mapDept = new HashMap<Long,String>();
        for (SysDept dept:deptList){
            mapDept.put(dept.getCompanyId(),dept.getCompanyName());
        }
        List<TApplication> applist = tApplication.selectTApplicationListN(0L);
        Map<Long,String> mapApp = new HashMap<Long,String>();
        for (TApplication app:applist){
            mapApp.put(app.getId(),app.getId()+":"+app.getAppName());
        }
        for(MoInfo en:list){
            en.setAppName(mapApp.containsKey(en.getAppId())?mapApp.get(en.getAppId()):en.getAppId()+":");
            en.setCompanyName(mapDept.getOrDefault(en.getCompanyId(),""));
        }
        return getDataTable(list);
    }

    /**
     * 导出上行列表
     */
    @RequiresPermissions("mo:mo:export")
    @Log(title = "上行", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(MoInfo moInfo)
    {
        List<MoInfo> list = moInfoService.selectMoInfoList(moInfo);
        ExcelUtil<MoInfo> util = new ExcelUtil<MoInfo>(MoInfo.class);
        return util.exportExcel(list, "上行数据");
    }

    /**
     * 新增上行
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存上行
     */
    @RequiresPermissions("mo:mo:add")
    @Log(title = "上行", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(MoInfo moInfo)
    {
        return toAjax(moInfoService.insertMoInfo(moInfo));
    }

    /**
     * 修改上行
     */
    @RequiresPermissions("mo:mo:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        MoInfo info=new MoInfo();
        info.setId(id);
        String logDate=DateUtil.convertDate1(new Date());
        info.setLogDate(logDate);

        MoInfo moInfo = moInfoService.selectMoInfoById(info);
        mmap.put("moInfo", moInfo);
        return prefix + "/edit";
    }

    /**
     * 修改保存上行
     */
    @RequiresPermissions("mo:mo:edit")
    @Log(title = "上行", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(MoInfo moInfo)
    {
        moInfo.setLogDate(DateUtil.convertDate1(new Date()));
        return toAjax(moInfoService.updateMoInfo(moInfo));
    }

    @RequiresPermissions("mo:mo:edit")
    @PostMapping("/push")
    @ResponseBody
    public AjaxResult push(MoInfo mo)
    {
        Long id = mo.getId();
        System.out.println(id);
        String logDate1 = mo.getLogDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(StringUtils.isBlank(logDate1)){
            logDate1=DateUtil.convertDate1(new Date());
        }else {

            try {
                Date parse = sdf.parse(logDate1);
                logDate1=sdf2.format(parse);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        mo.setLogDate(logDate1);
        MoInfo moInfo = moInfoService.selectMoInfoById(mo);
        Long companyId = moInfo.getCompanyId();
        Long appId = moInfo.getAppId();
        TApplication application = tApplication.selectTApplicationById(appId);
//        if(application ==null || StringUtils.isBlank(application.getMoSyncAddress())){
//            return AjaxResult.error("推送目标不明确");
//        }
        String appName = application.getAppName();
        JSONObject submitJson=new JSONObject();
        submitJson.put("acc",appName);
        submitJson.put("serviceNo",moInfo.getServiceCode());
        submitJson.put("mob",moInfo.getMobile());
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
        String logDate = DateUtil.convertDate1(new Date());
        String tableName="mms_mo_"+logDate;
        submitJson.put("tableName",tableName);
        submitJson.put("moId",moInfo.getMoId());
        System.out.println("推送参数： "+submitJson.toString());
        //保存到上行队列
//        RedisUtils.fifo_push(RedisUtils.FIFO_APP_MO_LIST+companyId,submitJson.toJSONString());
//        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_MO_TOTAL, companyId+"", 1);
        return AjaxResult.success("推送成功");
    }

    /**
     * 删除上行
     */
    @RequiresPermissions("mo:mo:remove")
    @Log(title = "上行", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(moInfoService.deleteMoInfoByIds(ids));
    }
}
