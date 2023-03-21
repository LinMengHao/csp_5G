package com.xzkj.platform.servicecode.controller;

import java.util.List;

import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.servicecode.domain.ECodeRelated;
import com.xzkj.platform.servicecode.service.IECodeRelatedService;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.common.core.page.TableDataInfo;

/**
 * 服务码号管理Controller
 * 
 * @author linmenghao
 * @date 2023-03-16
 */
@Controller
@RequestMapping("/servicecode/servicecode")
public class ECodeRelatedController extends BaseController
{
    private String prefix = "servicecode/servicecode";

    @Autowired
    private IChannelService channelService;

    @Autowired
    private IECodeRelatedService eCodeRelatedService;

    @RequiresPermissions("servicecode:servicecode:view")
    @GetMapping()
    public String servicecode(ModelMap mmap)
    {
        List<Channel> channels = channelService.selectChannelListAll(0L);
        mmap.put("channellist", channels);
        return prefix + "/servicecode";
    }

    /**
     * 查询服务码号管理列表
     */
    @RequiresPermissions("servicecode:servicecode:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ECodeRelated eCodeRelated)
    {
        startPage();
        List<ECodeRelated> list = eCodeRelatedService.selectECodeRelatedList(eCodeRelated);
        return getDataTable(list);
    }

    /**
     * 导出服务码号管理列表
     */
    @RequiresPermissions("servicecode:servicecode:export")
    @Log(title = "服务码号管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ECodeRelated eCodeRelated)
    {
        List<ECodeRelated> list = eCodeRelatedService.selectECodeRelatedList(eCodeRelated);
        ExcelUtil<ECodeRelated> util = new ExcelUtil<ECodeRelated>(ECodeRelated.class);
        return util.exportExcel(list, "服务码号管理数据");
    }

    /**
     * 新增服务码号管理
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        List<Channel> channels = channelService.selectChannelListAll(0L);
        mmap.put("channellist", channels);
        return prefix + "/add";
    }

    /**
     * 新增保存服务码号管理
     */
    @RequiresPermissions("servicecode:servicecode:add")
    @Log(title = "服务码号管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ECodeRelated eCodeRelated)
    {
        return toAjax(eCodeRelatedService.insertECodeRelated(eCodeRelated));
    }

    /**
     * 修改服务码号管理
     */
    @RequiresPermissions("servicecode:servicecode:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        ECodeRelated eCodeRelated = eCodeRelatedService.selectECodeRelatedById(id);
        mmap.put("eCodeRelated", eCodeRelated);
        List<Channel> channels = channelService.selectChannelListAll(0L);
        mmap.put("channellist", channels);
        return prefix + "/edit";
    }

    /**
     * 修改保存服务码号管理
     */
    @RequiresPermissions("servicecode:servicecode:edit")
    @Log(title = "服务码号管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ECodeRelated eCodeRelated)
    {

        return toAjax(eCodeRelatedService.updateECodeRelated(eCodeRelated));
    }

    @GetMapping("autocomplete")
    @ResponseBody
    public AjaxResult autoComplete(Long appId,String signId,String modelId,Long channelId){
        ECodeRelated codeRelated=new ECodeRelated();
        codeRelated.setAppId(appId);
        codeRelated.setSignId(signId);
        codeRelated.setModelId(modelId);
        codeRelated.setChannelId(channelId);
        ECodeRelated eCodeRelated=eCodeRelatedService.autoComplete(codeRelated);
        return success(eCodeRelated);
    }

    /**
     * 删除服务码号管理
     */
    @RequiresPermissions("servicecode:servicecode:remove")
    @Log(title = "服务码号管理", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(eCodeRelatedService.deleteECodeRelatedByIds(ids));
    }
}
