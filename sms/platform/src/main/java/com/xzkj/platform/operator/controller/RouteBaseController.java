package com.xzkj.platform.operator.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.domain.RouteBase;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.operator.service.IRouteBaseService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路由信息e_route_baseController
 * @author ruoyi
 * @date 2022-09-18
 */
@Controller
@RequestMapping("/operator/routeBase")
public class RouteBaseController extends BaseController
{
    private String prefix = "operator/routeBase";

    @Autowired
    private IRouteBaseService routeBaseService;
    @Autowired
    private ITApplicationService tApplicationService;
    @Autowired
    private IChannelService channelService;

    @RequiresPermissions("operator:routeBase:view")
    @GetMapping()
    public String base(ModelMap mmap){
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        return prefix + "/base";
    }

    /**
     * 查询路由信息e_route_base列表
     */
    @RequiresPermissions("operator:routeBase:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(RouteBase routeBase)
    {
        startPage();
        List<RouteBase> list = routeBaseService.selectRouteBaseList(routeBase);
        return getDataTable(list);
    }

    /**
     * 导出路由信息e_route_base列表
     */
    @RequiresPermissions("operator:routeBase:export")
    @Log(title = "路由信息e_route_base", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(RouteBase routeBase)
    {
        List<RouteBase> list = routeBaseService.selectRouteBaseList(routeBase);
        ExcelUtil<RouteBase> util = new ExcelUtil<RouteBase>(RouteBase.class);
        return util.exportExcel(list, "路由信息e_route_base数据");
    }

    /**
     * 新增路由信息e_route_base
     */
    @GetMapping("/add")
    public String add(ModelMap mmap){
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        return prefix + "/add";
    }

    /**
     * 新增保存路由信息e_route_base
     */
    @RequiresPermissions("operator:routeBase:add")
    @Log(title = "路由信息e_route_base", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(RouteBase routeBase)
    {
        return toAjax(routeBaseService.insertRouteBase(routeBase));
    }

    /**
     * 修改路由信息e_route_base
     */
    @RequiresPermissions("operator:routeBase:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        RouteBase eRouteBase = routeBaseService.selectRouteBaseById(id);
        mmap.put("eRouteBase", eRouteBase);
        return prefix + "/edit";
    }

    /**
     * 修改保存路由信息e_route_base
     */
    @RequiresPermissions("operator:routeBase:edit")
    @Log(title = "路由信息e_route_base", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(RouteBase eRouteBase)
    {
        return toAjax(routeBaseService.updateRouteBase(eRouteBase));
    }

    /**
     * 删除路由信息e_route_base
     */
    @RequiresPermissions("operator:routeBase:remove")
    @Log(title = "路由信息e_route_base", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(routeBaseService.deleteRouteBaseByIds(ids));
    }

    @GetMapping("/selectByCompanyID")
    @ResponseBody
    public AjaxResult selectByCompanyID(long companyId){
        List<TApplication> applist = tApplicationService.selectTApplicationListN(companyId);
        return success(applist);

    }
}
