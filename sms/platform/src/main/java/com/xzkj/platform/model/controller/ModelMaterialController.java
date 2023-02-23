package com.xzkj.platform.model.controller;


import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.model.domain.ModelMaterial;
import com.xzkj.platform.model.service.IModelMaterialService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模板素材Controller
 * 
 * @author ruoyi
 * @date 2022-11-30
 */
@Controller
@RequestMapping("/model/modelMaterial")
public class ModelMaterialController extends BaseController
{
    private String prefix = "model/modelMaterial";

    @Autowired
    private IModelMaterialService modelMaterialService;

    @RequiresPermissions("model:modelMaterial:view")
    @GetMapping()
    public String modelMaterial()
    {
        return prefix + "/modelMaterial";
    }

    /**
     * 查询模板素材列表
     */
    @RequiresPermissions("model:modelMaterial:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ModelMaterial modelMaterial)
    {
        startPage();
        List<ModelMaterial> list = modelMaterialService.selectModelMaterialList(modelMaterial);
        return getDataTable(list);
    }

    /**
     * 导出模板素材列表
     */
    @RequiresPermissions("model:modelMaterial:export")
    @Log(title = "模板素材", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ModelMaterial modelMaterial)
    {
        List<ModelMaterial> list = modelMaterialService.selectModelMaterialList(modelMaterial);
        ExcelUtil<ModelMaterial> util = new ExcelUtil<ModelMaterial>(ModelMaterial.class);
        return util.exportExcel(list, "模板素材数据");
    }

    /**
     * 新增模板素材
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存模板素材
     */
    @RequiresPermissions("model:modelMaterial:add")
    @Log(title = "模板素材", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ModelMaterial modelMaterial)
    {
        return toAjax(modelMaterialService.insertModelMaterial(modelMaterial));
    }

    /**
     * 修改模板素材
     */
    @RequiresPermissions("model:modelMaterial:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        ModelMaterial modelMaterial = modelMaterialService.selectModelMaterialById(id);
        mmap.put("modelMaterial", modelMaterial);
        return prefix + "/edit";
    }

    /**
     * 修改保存模板素材
     */
    @RequiresPermissions("model:modelMaterial:edit")
    @Log(title = "模板素材", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ModelMaterial modelMaterial)
    {
        return toAjax(modelMaterialService.updateModelMaterial(modelMaterial));
    }

    /**
     * 删除模板素材
     */
    @RequiresPermissions("model:modelMaterial:remove")
    @Log(title = "模板素材", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(modelMaterialService.deleteModelMaterialByIds(ids));
    }
}
