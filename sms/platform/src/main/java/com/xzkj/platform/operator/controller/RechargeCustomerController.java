package com.xzkj.platform.operator.controller;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.operator.domain.RechargeCustomer;
import com.xzkj.platform.operator.service.IRechargeCustomerService;
import com.xzkj.platform.system.service.ISysUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户充值记录Controller
 * 
 * @author ruoyi
 * @date 2022-10-24
 */
@Controller
@RequestMapping("/operator/recharge")
public class RechargeCustomerController extends BaseController
{
    private String prefix = "operator/recharge";

    @Autowired
    private IRechargeCustomerService rechargeCustomerService;
    @Autowired
    private ITApplicationService tApplicationService;
    @Autowired
    private ISysUserService sysUserService;

    @RequiresPermissions("operator:recharge:view")
    @GetMapping()
    public String recharge(ModelMap mmap)
    {
        List<SysUser> userList = sysUserService.selectUserList(new SysUser());
        mmap.put("userList", userList);
        return prefix + "/recharge";
    }

    /**
     * 查询客户充值记录列表
     */
    @RequiresPermissions("operator:recharge:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(RechargeCustomer rechargeCustomer)
    {
        startPage();
        List<RechargeCustomer> list = rechargeCustomerService.selectRechargeCustomerList(rechargeCustomer);
        return getDataTable(list);
    }

    /**
     * 导出客户充值记录列表
     */
    @RequiresPermissions("operator:recharge:export")
    @Log(title = "客户充值记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(RechargeCustomer rechargeCustomer)
    {
        List<RechargeCustomer> list = rechargeCustomerService.selectRechargeCustomerList(rechargeCustomer);
        ExcelUtil<RechargeCustomer> util = new ExcelUtil<RechargeCustomer>(RechargeCustomer.class);
        return util.exportExcel(list, "客户充值记录数据");
    }

    /**
     * 新增客户充值记录
     */
    @GetMapping("/add/{companyId}/{companyName}/{appId}/{appName}")
    public String add(@PathVariable("companyId") Long companyId,@PathVariable("companyName") String companyName,@PathVariable("appId") Long appId,@PathVariable("appName") String appName, ModelMap mmap)
    {
        TApplication tApplication = tApplicationService.selectTApplicationById(appId);
        mmap.put("appId", appId);
        mmap.put("appName", appName);
        mmap.put("companyId", companyId);
        mmap.put("companyName", companyName);
        mmap.put("limitCount", tApplication.getLimitCount());
        return prefix + "/add";
    }

    /**
     * 新增保存客户充值记录
     */
    @RequiresPermissions("operator:recharge:add")
    @Log(title = "客户充值记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(RechargeCustomer rechargeCustomer)
    {
        return toAjax(rechargeCustomerService.insertRechargeCustomer(rechargeCustomer));
    }

    /**
     * 修改客户充值记录
     */
    @RequiresPermissions("operator:recharge:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        RechargeCustomer rechargeCustomer = rechargeCustomerService.selectRechargeCustomerById(id);
        mmap.put("rechargeCustomer", rechargeCustomer);
        return prefix + "/edit";
    }

    /**
     * 客户充值记录详情
     */
    @RequiresPermissions("operator:recharge:detail")
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, ModelMap mmap)
    {
        RechargeCustomer rechargeCustomer = rechargeCustomerService.selectRechargeCustomerById(id);
        mmap.put("rechargeCustomer", rechargeCustomer);
        return prefix + "/detail";
    }

    /**
     * 修改保存客户充值记录
     */
    @RequiresPermissions("operator:recharge:edit")
    @Log(title = "客户充值记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(RechargeCustomer rechargeCustomer)
    {
        return toAjax(rechargeCustomerService.updateRechargeCustomer(rechargeCustomer));
    }

    /**
     * 删除客户充值记录
     */
    @RequiresPermissions("operator:recharge:remove")
    @Log(title = "客户充值记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(rechargeCustomerService.deleteRechargeCustomerByIds(ids));
    }
}
