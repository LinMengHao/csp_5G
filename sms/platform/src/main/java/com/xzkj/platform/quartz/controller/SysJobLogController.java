package com.xzkj.platform.quartz.controller;


import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.quartz.domain.SysJob;
import com.xzkj.platform.quartz.domain.SysJobLog;
import com.xzkj.platform.quartz.service.ISysJobLogService;
import com.xzkj.platform.quartz.service.ISysJobService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度日志操作处理
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends BaseController
{
    private String prefix = "monitor/job";

    @Autowired
    private ISysJobService jobService;

    @Autowired
    private ISysJobLogService jobLogService;

    @RequiresPermissions("monitor:job:view")
    @GetMapping()
    public String jobLog(@RequestParam(value = "jobId", required = false) Long jobId, ModelMap mmap)
    {
        if (StringUtils.isNotNull(jobId))
        {
            SysJob job = jobService.selectJobById(jobId);
            mmap.put("job", job);
        }
        return prefix + "/jobLog";
    }

    @RequiresPermissions("monitor:job:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysJobLog jobLog)
    {
        startPage();
        List<SysJobLog> list = jobLogService.selectJobLogList(jobLog);
        return getDataTable(list);
    }

    @Log(title = "调度日志", businessType = BusinessType.EXPORT)
    @RequiresPermissions("monitor:job:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysJobLog jobLog)
    {
        List<SysJobLog> list = jobLogService.selectJobLogList(jobLog);
        ExcelUtil<SysJobLog> util = new ExcelUtil<SysJobLog>(SysJobLog.class);
        return util.exportExcel(list, "调度日志");
    }

    @Log(title = "调度日志", businessType = BusinessType.DELETE)
    @RequiresPermissions("monitor:job:remove")
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(jobLogService.deleteJobLogByIds(ids));
    }

    @RequiresPermissions("monitor:job:detail")
    @GetMapping("/detail/{jobLogId}")
    public String detail(@PathVariable("jobLogId") Long jobLogId, ModelMap mmap)
    {
        mmap.put("name", "jobLog");
        mmap.put("jobLog", jobLogService.selectJobLogById(jobLogId));
        return prefix + "/detail";
    }

    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @RequiresPermissions("monitor:job:remove")
    @PostMapping("/clean")
    @ResponseBody
    public AjaxResult clean()
    {
        jobLogService.cleanJobLog();
        return success();
    }
}
