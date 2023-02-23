package com.xzkj.platform.khd.controller;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.khd.domain.MmsSender;
import com.xzkj.platform.khd.service.IMmsSenderService;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 下行日志的显示查询Controller测试
 * 
 * @author lixl
 * @date 2022-01-24
 */
@Controller
@RequestMapping("/mmskhd/mt")
public class MtController extends BaseController
{
    private String prefix = "mmskhd/mt";

    @Autowired
    private IMmsSenderService mmsSenderService;

    @Autowired
    private IChannelService channelService;

    @RequiresPermissions("mmskhd:mt:list")
    @GetMapping()
    public String mt(ModelMap mmap)
    {
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        return prefix + "/mt";
    }

    /**
     * 查询下行日志的显示查询列表
     */
    @RequiresPermissions("mmskhd:mt:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(MmsSender mmsSender)
    {
        startPage();
        List<MmsSender> list = mmsSenderService.selectMmsSenderList(mmsSender);
        logger.info("[mtres][RECHARGE] data="+ JSONObject.toJSONString(list));
        return getDataTable(list);
    }
    /**
     * 导出下行日志的显示查询列表
     */
    @RequiresPermissions("mmskhd:mt:export")
    @Log(title = "下行日志的显示查询", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(MmsSender mmsSender)
    {
        List<MmsSender> list = mmsSenderService.selectMmsSenderList(mmsSender);
        ExcelUtil<MmsSender> util = new ExcelUtil<MmsSender>(MmsSender.class);
        return util.exportExcel(list, "下行日志的显示查询数据");
    }
}
