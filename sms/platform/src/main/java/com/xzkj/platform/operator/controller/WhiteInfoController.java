package com.xzkj.platform.operator.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.operator.domain.WhiteInfo;
import com.xzkj.platform.operator.service.IWhiteInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 白名单Controller
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
@Controller
@RequestMapping("/operator/whiteInfo")
public class WhiteInfoController extends BaseController
{
    private String prefix = "operator/whiteInfo";

    @Autowired
    private IWhiteInfoService whiteInfoService;

    @RequiresPermissions("operator:whiteInfo:view")
    @GetMapping()
    public String whiteInfo()
    {
        return prefix + "/whiteInfo";
    }

    /**
     * 查询白名单列表
     */
    @RequiresPermissions("operator:whiteInfo:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(WhiteInfo whiteInfo)
    {
        startPage();
        List<WhiteInfo> list = whiteInfoService.selectWhiteInfoList(whiteInfo);
        return getDataTable(list);
    }

    /**
     * 导出白名单列表
     */
    @RequiresPermissions("operator:whiteInfo:export")
    @Log(title = "白名单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(WhiteInfo whiteInfo)
    {
        List<WhiteInfo> list = whiteInfoService.selectWhiteInfoList(whiteInfo);
        ExcelUtil<WhiteInfo> util = new ExcelUtil<WhiteInfo>(WhiteInfo.class);
        return util.exportExcel(list, "白名单数据");
    }

    /**
     * 新增白名单
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存白名单
     */
    @RequiresPermissions("operator:whiteInfo:add")
    @Log(title = "白名单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(WhiteInfo whiteInfo)
    {
        return toAjax(whiteInfoService.insertWhiteInfo(whiteInfo));
    }

    /**
     * 修改白名单
     */
    @RequiresPermissions("operator:whiteInfo:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        WhiteInfo whiteInfo = whiteInfoService.selectWhiteInfoById(id);
        mmap.put("whiteInfo", whiteInfo);
        return prefix + "/edit";
    }

    /**
     * 修改保存白名单
     */
    @RequiresPermissions("operator:whiteInfo:edit")
    @Log(title = "白名单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(WhiteInfo whiteInfo)
    {
        return toAjax(whiteInfoService.updateWhiteInfo(whiteInfo));
    }

    /**
     * 删除白名单
     */
    @RequiresPermissions("operator:whiteInfo:remove")
    @Log(title = "白名单", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(whiteInfoService.deleteWhiteInfoByIds(ids));
    }
}
