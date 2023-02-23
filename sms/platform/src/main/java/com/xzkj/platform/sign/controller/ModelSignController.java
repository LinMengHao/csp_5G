package com.xzkj.platform.sign.controller;


import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.model.service.IModelMaterialService;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.sign.domain.ModelSign;
import com.xzkj.platform.sign.service.IModelSignService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 模板信息e_model_infoController
 * 
 * @author ruoyi
 * @date 2022-11-24
 */
@Controller
@RequestMapping("/model/sign")
public class ModelSignController extends BaseController
{
    private String prefix = "model/sign";

    @Value("${model.filePath}")
    private String modelFilePath;

    @Autowired
    private IModelSignService modelSignService;
    @Autowired
    private IModelMaterialService modelMaterialService;

    @Autowired
    private IChannelService channelService;


    @RequiresPermissions("model:sign:view")
    @GetMapping()
    public String model()
    {
        return prefix + "/sign";
    }



    /**
     * 查询模板信息e_model_info列表
     */
    @RequiresPermissions("model:sign:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ModelSign modelSign)
    {
        startPage();
        List<ModelSign> list = modelSignService.selectModelSignList(modelSign);
        return getDataTable(list);
    }

    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    @RequiresPermissions("model:sign:add")
    @Log(title = "模板信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ModelSign modelSign, HttpServletRequest request)
    {
        System.out.println("ModelSign:"+modelSign.toString());
        return toAjax(1);
    }

    @PostMapping("/report")
    @ResponseBody
    public void report(String msg){
        System.out.println("=====================模版审核状态回调=========================");
        System.out.println("msg: "+msg);
    }


}
