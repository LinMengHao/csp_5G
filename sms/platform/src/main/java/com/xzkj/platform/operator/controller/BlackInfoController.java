package com.xzkj.platform.operator.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.operator.domain.BlackInfo;
import com.xzkj.platform.operator.service.IBlackInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 全局黑名单Controller
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Controller
@RequestMapping("/operator/blackInfo")
public class BlackInfoController extends BaseController
{
    private String prefix = "operator/blackInfo";

    @Autowired
    private IBlackInfoService blackInfoService;

    @RequiresPermissions("operator:blackInfo:view")
    @GetMapping()
    public String blackInfo()
    {
        return prefix + "/blackInfo";
    }

    /**
     * 查询全局黑名单列表
     */
    @RequiresPermissions("operator:blackInfo:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BlackInfo blackInfo)
    {
        startPage();
        List<BlackInfo> list = blackInfoService.selectBlackInfoList(blackInfo);
        return getDataTable(list);
    }

    /**
     * 导出全局黑名单列表
     */
    @RequiresPermissions("operator:blackInfo:export")
    @Log(title = "全局黑名单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BlackInfo blackInfo)
    {
        List<BlackInfo> list = blackInfoService.selectBlackInfoList(blackInfo);
        ExcelUtil<BlackInfo> util = new ExcelUtil<BlackInfo>(BlackInfo.class);
        return util.exportExcel(list, "全局黑名单数据");
    }

    /**
     * 新增全局黑名单
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存全局黑名单
     */
    @RequiresPermissions("operator:blackInfo:add")
    @Log(title = "全局黑名单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BlackInfo blackInfo)
    {
        return toAjax(blackInfoService.insertBlackInfo(blackInfo));
    }

    /**
     * 修改全局黑名单
     */
    @RequiresPermissions("operator:blackInfo:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BlackInfo blackInfo = blackInfoService.selectBlackInfoById(id);
        mmap.put("blackInfo", blackInfo);
        return prefix + "/edit";
    }

    /**
     * 修改保存全局黑名单
     */
    @RequiresPermissions("operator:blackInfo:edit")
    @Log(title = "全局黑名单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BlackInfo blackInfo)
    {
        return toAjax(blackInfoService.updateBlackInfo(blackInfo));
    }

    /**
     * 删除全局黑名单
     */
    @RequiresPermissions("operator:blackInfo:remove")
    @Log(title = "全局黑名单", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(blackInfoService.deleteBlackInfoByIds(ids));
    }
}
