package com.xzkj.platform.operator.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.operator.domain.BlackCompany;
import com.xzkj.platform.operator.service.IBlackCompanyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 私有黑名单Controller
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Controller
@RequestMapping("/operator/blackCompany")
public class BlackCompanyController extends BaseController
{
    private String prefix = "operator/blackCompany";

    @Autowired
    private IBlackCompanyService blackCompanyService;

    @RequiresPermissions("operator:blackCompany:view")
    @GetMapping()
    public String blackCompany()
    {
        return prefix + "/blackCompany";
    }

    /**
     * 查询私有黑名单列表
     */
    @RequiresPermissions("operator:blackCompany:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BlackCompany blackCompany)
    {
        startPage();
        List<BlackCompany> list = blackCompanyService.selectBlackCompanyList(blackCompany);
        return getDataTable(list);
    }

    /**
     * 导出私有黑名单列表
     */
    @RequiresPermissions("operator:blackCompany:export")
    @Log(title = "私有黑名单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BlackCompany blackCompany)
    {
        List<BlackCompany> list = blackCompanyService.selectBlackCompanyList(blackCompany);
        ExcelUtil<BlackCompany> util = new ExcelUtil<BlackCompany>(BlackCompany.class);
        return util.exportExcel(list, "私有黑名单数据");
    }

    /**
     * 新增私有黑名单
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存私有黑名单
     */
    @RequiresPermissions("operator:blackCompany:add")
    @Log(title = "私有黑名单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BlackCompany blackCompany)
    {
        return toAjax(blackCompanyService.insertBlackCompany(blackCompany));
    }

    /**
     * 修改私有黑名单
     */
    @RequiresPermissions("operator:blackCompany:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BlackCompany blackCompany = blackCompanyService.selectBlackCompanyById(id);
        mmap.put("blackCompany", blackCompany);
        return prefix + "/edit";
    }

    /**
     * 修改保存私有黑名单
     */
    @RequiresPermissions("operator:blackCompany:edit")
    @Log(title = "私有黑名单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BlackCompany blackCompany)
    {
        return toAjax(blackCompanyService.updateBlackCompany(blackCompany));
    }

    /**
     * 删除私有黑名单
     */
    @RequiresPermissions("operator:blackCompany:remove")
    @Log(title = "私有黑名单", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(blackCompanyService.deleteBlackCompanyByIds(ids));
    }
}
