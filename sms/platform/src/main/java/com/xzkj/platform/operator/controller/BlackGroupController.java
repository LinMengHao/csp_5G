package com.xzkj.platform.operator.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.operator.domain.BlackGroup;
import com.xzkj.platform.operator.service.IBlackGroupService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 黑名单组Controller
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Controller
@RequestMapping("/operator/blackGroup")
public class BlackGroupController extends BaseController
{
    private String prefix = "operator/blackGroup";

    @Autowired
    private IBlackGroupService blackGroupService;

    @RequiresPermissions("operator:blackGroup:view")
    @GetMapping()
    public String blackGroup()
    {
        return prefix + "/blackGroup";
    }

    /**
     * 查询黑名单组列表
     */
    @RequiresPermissions("operator:blackGroup:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BlackGroup blackGroup)
    {
        startPage();
        List<BlackGroup> list = blackGroupService.selectBlackGroupList(blackGroup);
        return getDataTable(list);
    }

    /**
     * 导出黑名单组列表
     */
    @RequiresPermissions("operator:blackGroup:export")
    @Log(title = "黑名单组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BlackGroup blackGroup)
    {
        List<BlackGroup> list = blackGroupService.selectBlackGroupList(blackGroup);
        ExcelUtil<BlackGroup> util = new ExcelUtil<BlackGroup>(BlackGroup.class);
        return util.exportExcel(list, "黑名单组数据");
    }

    /**
     * 新增黑名单组
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存黑名单组
     */
    @RequiresPermissions("operator:blackGroup:add")
    @Log(title = "黑名单组", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BlackGroup blackGroup)
    {
        return toAjax(blackGroupService.insertBlackGroup(blackGroup));
    }

    /**
     * 修改黑名单组
     */
    @RequiresPermissions("operator:blackGroup:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BlackGroup blackGroup = blackGroupService.selectBlackGroupById(id);
        mmap.put("blackGroup", blackGroup);
        return prefix + "/edit";
    }

    /**
     * 修改保存黑名单组
     */
    @RequiresPermissions("operator:blackGroup:edit")
    @Log(title = "黑名单组", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BlackGroup blackGroup)
    {
        return toAjax(blackGroupService.updateBlackGroup(blackGroup));
    }

    /**
     * 删除黑名单组
     */
    @RequiresPermissions("operator:blackGroup:remove")
    @Log(title = "黑名单组", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(blackGroupService.deleteBlackGroupByIds(ids));
    }
}
