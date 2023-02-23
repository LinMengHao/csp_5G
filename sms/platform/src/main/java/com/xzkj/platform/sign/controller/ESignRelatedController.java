package com.xzkj.platform.sign.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.sign.domain.ESignRelated;
import com.xzkj.platform.sign.service.IESignRelatedService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 签名映射关系Controller
 * 
 * @author linmenghao
 * @date 2023-02-06
 */
@Controller
@RequestMapping("/sign/related")
public class ESignRelatedController extends BaseController
{
    private String prefix = "sign/related";

    @Autowired
    private IChannelService channelService;
    @Autowired
    private IESignRelatedService eSignRelatedService;

    @RequiresPermissions("sign:related:view")
    @GetMapping()
    public String related(ModelMap mmap)
    {
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        return prefix + "/related";
    }

    /**
     * 查询签名映射关系列表
     */
    @RequiresPermissions("sign:related:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ESignRelated eSignRelated)
    {
        startPage();
        List<ESignRelated> list = eSignRelatedService.selectESignRelatedList(eSignRelated);
        return getDataTable(list);
    }

    /**
     * 导出签名映射关系列表
     */
    @RequiresPermissions("sign:related:export")
    @Log(title = "签名映射关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ESignRelated eSignRelated)
    {
        List<ESignRelated> list = eSignRelatedService.selectESignRelatedList(eSignRelated);
        ExcelUtil<ESignRelated> util = new ExcelUtil<ESignRelated>(ESignRelated.class);
        return util.exportExcel(list, "签名映射关系数据");
    }

    /**
     * 新增签名映射关系
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        return prefix + "/add";
    }

    /**
     * 新增保存签名映射关系
     */
    @RequiresPermissions("sign:related:add")
    @Log(title = "签名映射关系", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ESignRelated eSignRelated)
    {
        return toAjax(eSignRelatedService.insertESignRelated(eSignRelated));
    }

    /**
     * 修改签名映射关系
     */
    @RequiresPermissions("sign:related:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        ESignRelated eSignRelated = eSignRelatedService.selectESignRelatedById(id);
        mmap.put("eSignRelated", eSignRelated);
        return prefix + "/edit";
    }

    /**
     * 修改保存签名映射关系
     */
    @RequiresPermissions("sign:related:edit")
    @Log(title = "签名映射关系", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ESignRelated eSignRelated)
    {
        return toAjax(eSignRelatedService.updateESignRelated(eSignRelated));
    }

    /**
     * 删除签名映射关系
     */
    @RequiresPermissions("sign:related:remove")
    @Log(title = "签名映射关系", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(eSignRelatedService.deleteESignRelatedByIds(ids));
    }
}
