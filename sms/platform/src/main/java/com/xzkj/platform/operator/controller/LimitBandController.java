package com.xzkj.platform.operator.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.domain.LimitBand;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.operator.service.ILimitBandService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 频次限制Controller
 * 
 * @author ruoyi
 * @date 2022-12-04
 */
@Controller
@RequestMapping("/operator/limitBand")
public class LimitBandController extends BaseController
{
    private String prefix = "operator/limitBand";

    @Autowired
    private ILimitBandService limitBandService;
    @Autowired
    private IChannelService channelService;

    @RequiresPermissions("operator:limitBand:view")
    @GetMapping()
    public String limitBand(ModelMap mmap)
    {
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        return prefix + "/limitBand";
    }

    /**
     * 查询频次限制列表
     */
    @RequiresPermissions("operator:limitBand:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(LimitBand limitBand)
    {
        startPage();
        List<LimitBand> list = limitBandService.selectLimitBandList(limitBand);
        return getDataTable(list);
    }

    /**
     * 导出频次限制列表
     */
    @RequiresPermissions("operator:limitBand:export")
    @Log(title = "频次限制", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(LimitBand limitBand)
    {
        List<LimitBand> list = limitBandService.selectLimitBandList(limitBand);
        ExcelUtil<LimitBand> util = new ExcelUtil<LimitBand>(LimitBand.class);
        return util.exportExcel(list, "频次限制数据");
    }

    /**
     * 新增频次限制
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        return prefix + "/add";
    }

    /**
     * 新增保存频次限制
     */
    @RequiresPermissions("operator:limitBand:add")
    @Log(title = "频次限制", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(LimitBand limitBand)
    {
        return toAjax(limitBandService.insertLimitBand(limitBand));
    }

    /**
     * 修改频次限制
     */
    @RequiresPermissions("operator:limitBand:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        LimitBand limitBand = limitBandService.selectLimitBandById(id);
        mmap.put("limitBand", limitBand);
        return prefix + "/edit";
    }

    /**
     * 修改保存频次限制
     */
    @RequiresPermissions("operator:limitBand:edit")
    @Log(title = "频次限制", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(LimitBand limitBand)
    {
        return toAjax(limitBandService.updateLimitBand(limitBand));
    }

    /**
     * 删除频次限制
     */
    @RequiresPermissions("operator:limitBand:remove")
    @Log(title = "频次限制", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(limitBandService.deleteLimitBandByIds(ids));
    }
}
