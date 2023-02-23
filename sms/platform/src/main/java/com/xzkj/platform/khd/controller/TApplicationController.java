package com.xzkj.platform.khd.controller;


import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.operator.domain.BlackGroup;
import com.xzkj.platform.operator.service.IBlackGroupService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账户管理Controller
 * 
 * @author ruoyi
 * @date 2022-02-08
 */
@Controller
@RequestMapping("/mmskhd/application")
public class TApplicationController extends BaseController
{
    private String prefix = "mmskhd/application";

    @Autowired
    private ITApplicationService tApplicationService;
    @Autowired
    private IBlackGroupService blackGroupService;

    @RequiresPermissions("mmskhd:application:view")
    @GetMapping()
    public String application()
    {
        return prefix + "/application";
    }

    /**
     * 查询账户管理列表
     */
    @RequiresPermissions("mmskhd:application:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(TApplication tApplication)
    {
        startPage();
        List<TApplication> list = tApplicationService.selectTApplicationList(tApplication);
        return getDataTable(list);
    }

    /**
     * 导出账户管理列表
     */
    @RequiresPermissions("mmskhd:application:export")
    @Log(title = "账户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(TApplication tApplication)
    {
        List<TApplication> list = tApplicationService.selectTApplicationList(tApplication);
        ExcelUtil<TApplication> util = new ExcelUtil<TApplication>(TApplication.class);
        return util.exportExcel(list, "账户管理数据");
    }

    /**
     * 新增账户管理
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        BlackGroup blackGroup = new BlackGroup();
        blackGroup.setStatus(1L);
        List<BlackGroup> blacklist = blackGroupService.selectBlackGroupList(blackGroup);
        mmap.put("blacklist", blacklist);
        return prefix + "/add";
    }

    /**
     * 新增保存账户管理
     */
    @RequiresPermissions("mmskhd:application:add")
    @Log(title = "账户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(TApplication tApplication)
    {
        tApplication.setAppType("account");
        return toAjax(tApplicationService.insertTApplication(tApplication));
    }

    /**
     * 修改账户管理
     */
    @RequiresPermissions("mmskhd:application:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        TApplication tApplication = tApplicationService.selectTApplicationById(id);
        mmap.put("tApplication", tApplication);
        BlackGroup blackGroup = new BlackGroup();
        blackGroup.setStatus(1L);
        List<BlackGroup> blacklist = blackGroupService.selectBlackGroupList(blackGroup);
        mmap.put("blacklist", blacklist);
        return prefix + "/edit";
    }

    /**
     * 修改保存账户管理
     */
    @RequiresPermissions("mmskhd:application:edit")
    @Log(title = "账户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(TApplication tApplication)
    {
        return toAjax(tApplicationService.updateTApplication(tApplication));
    }

    /**
     * 删除账户管理
     */
    @RequiresPermissions("mmskhd:application:remove")
    @Log(title = "账户管理", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(tApplicationService.deleteTApplicationByIds(ids));
    }
}
