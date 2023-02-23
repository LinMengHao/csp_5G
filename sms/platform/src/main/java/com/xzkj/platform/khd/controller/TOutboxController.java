package com.xzkj.platform.khd.controller;


import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.khd.domain.TOutbox;
import com.xzkj.platform.khd.service.ITOutboxService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发件箱Controller
 * 
 * @author Lixl
 * @date 2022-02-07
 */
@Controller
@RequestMapping("/mmskhd/outbox")
public class TOutboxController extends BaseController
{
    private String prefix = "mmskhd/outbox";

    @Autowired
    private ITOutboxService tOutboxService;

    @RequiresPermissions("mmskhd:outbox:view")
    @GetMapping()
    public String outbox()
    {
        return prefix + "/outbox";
    }

    /**
     * 查询发件箱列表
     */
    @RequiresPermissions("mmskhd:outbox:view")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(TOutbox tOutbox)
    {
        startPage();
        List<TOutbox> list = tOutboxService.selectTOutboxList(tOutbox);
        return getDataTable(list);
    }

    /**
     * 导出发件箱列表
     */
    @RequiresPermissions("mmskhd:outbox:export")
    @Log(title = "发件箱", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(TOutbox tOutbox)
    {
        List<TOutbox> list = tOutboxService.selectTOutboxList(tOutbox);
        ExcelUtil<TOutbox> util = new ExcelUtil<TOutbox>(TOutbox.class);
        return util.exportExcel(list, "发件箱数据");
    }

    /**
     * 新增发件箱
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存发件箱
     */
    @RequiresPermissions("mmskhd:outbox:add")
    @Log(title = "发件箱", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(TOutbox tOutbox)
    {
        return toAjax(tOutboxService.insertTOutbox(tOutbox));
    }

    /**
     * 修改发件箱
     */
    @RequiresPermissions("mmskhd:outbox:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        TOutbox tOutbox = tOutboxService.selectTOutboxById(id);
        mmap.put("tOutbox", tOutbox);
        return prefix + "/edit";
    }

    /**
     * 修改保存发件箱
     */
    @RequiresPermissions("mmskhd:outbox:edit")
    @Log(title = "发件箱", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(TOutbox tOutbox)
    {
        return toAjax(tOutboxService.updateTOutbox(tOutbox));
    }

    /**
     * 删除发件箱
     */
    @RequiresPermissions("mmskhd:outbox:remove")
    @Log(title = "发件箱", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(tOutboxService.deleteTOutboxByIds(ids));
    }
}
