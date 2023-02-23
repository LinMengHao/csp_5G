package com.xzkj.platform.operator.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通道信息Controller
 * 
 * @author admin
 * @date 2022-09-25
 */
@Controller
@RequestMapping("/operator/channel")
public class ChannelController extends BaseController
{
    private String prefix = "operator/channel";

    @Autowired
    private IChannelService channelService;

    @RequiresPermissions("operator:channel:view")
    @GetMapping()
    public String channel()
    {
        return prefix + "/channel";
    }

    /**
     * 查询通道信息列表
     */
    @RequiresPermissions("operator:channel:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Channel channel)
    {
        startPage();
        List<Channel> list = channelService.selectChannelListPage(channel);
        return getDataTable(list);
    }

    /**
     * 导出通道信息列表
     */
    @RequiresPermissions("operator:channel:export")
    @Log(title = "通道信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Channel channel)
    {
        List<Channel> list = channelService.selectChannelListPage(channel);
        ExcelUtil<Channel> util = new ExcelUtil<Channel>(Channel.class);
        return util.exportExcel(list, "通道信息数据");
    }

    /**
     * 新增通道信息
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存通道信息
     */
    @RequiresPermissions("operator:channel:add")
    @Log(title = "通道信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Channel channel)
    {
        return toAjax(channelService.insertChannel(channel));
    }

    /**
     * 修改通道信息
     */
    @RequiresPermissions("operator:channel:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        Channel channel = channelService.selectChannelById(id);
        mmap.put("channel", channel);
        return prefix + "/edit";
    }

    /**
     * 修改保存通道信息
     */
    @RequiresPermissions("operator:channel:edit")
    @Log(title = "通道信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Channel channel)
    {
        return toAjax(channelService.updateChannel(channel));
    }

    /**
     * 删除通道信息
     */
    @RequiresPermissions("operator:channel:remove")
    @Log(title = "通道信息", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(channelService.deleteChannelByIds(ids));
    }
}
