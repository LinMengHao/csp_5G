package com.xzkj.platform.operator.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.operator.domain.BlackRelated;
import com.xzkj.platform.operator.service.IBlackRelatedService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 黑名单组关系Controller
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Controller
@RequestMapping("/operator/blackRelated")
public class BlackRelatedController extends BaseController
{
    private String prefix = "operator/blackRelated";

    @Autowired
    private IBlackRelatedService blackRelatedService;

    @RequiresPermissions("operator:blackRelated:view")
    @GetMapping()
    public String blackRelated()
    {
        return prefix + "/blackRelated";
    }

    /**
     * 查询黑名单组关系列表
     */
    @RequiresPermissions("operator:blackRelated:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BlackRelated blackRelated)
    {
        startPage();
        List<BlackRelated> list = blackRelatedService.selectBlackRelatedList(blackRelated);
        return getDataTable(list);
    }

    /**
     * 导出黑名单组关系列表
     */
    @RequiresPermissions("operator:blackRelated:export")
    @Log(title = "黑名单组关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BlackRelated blackRelated)
    {
        List<BlackRelated> list = blackRelatedService.selectBlackRelatedList(blackRelated);
        ExcelUtil<BlackRelated> util = new ExcelUtil<BlackRelated>(BlackRelated.class);
        return util.exportExcel(list, "黑名单组关系数据");
    }

    /**
     * 新增黑名单组关系
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存黑名单组关系
     */
    @RequiresPermissions("operator:blackRelated:add")
    @Log(title = "黑名单组关系", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BlackRelated blackRelated)
    {
        return toAjax(blackRelatedService.insertBlackRelated(blackRelated));
    }

    /**
     * 修改黑名单组关系
     */
    @RequiresPermissions("operator:blackRelated:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BlackRelated blackRelated = blackRelatedService.selectBlackRelatedById(id);
        mmap.put("blackRelated", blackRelated);
        return prefix + "/edit";
    }

    /**
     * 修改保存黑名单组关系
     */
    @RequiresPermissions("operator:blackRelated:edit")
    @Log(title = "黑名单组关系", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BlackRelated blackRelated)
    {
        return toAjax(blackRelatedService.updateBlackRelated(blackRelated));
    }

    /**
     * 删除黑名单组关系
     */
    @RequiresPermissions("operator:blackRelated:remove")
    @Log(title = "黑名单组关系", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(blackRelatedService.deleteBlackRelatedByIds(ids));
    }
}
