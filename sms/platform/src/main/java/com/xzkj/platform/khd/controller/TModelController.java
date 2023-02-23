package com.xzkj.platform.khd.controller;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.UUIDUtils;
import com.xzkj.platform.common.utils.file.FileNameUtils;
import com.xzkj.platform.common.utils.file.FileUploadUtils;
import com.xzkj.platform.common.utils.file.FileUtilsNew;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.domain.TModel;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.khd.service.ITModelService;
import com.xzkj.platform.khd.utils.DateUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 模板管理Controller
 *
 * @author Lixl
 * @date 2022-02-09
 */
@Controller
@RequestMapping("/mmskhd/model")
public class TModelController<psv> extends BaseController
{
    private String prefix = "mmskhd/model";

    @Value("${model.filePath}")
    private String modelFilePath;
    @Value("${devNumber}")
    private String devNumber;

    @Autowired
    private ITModelService tModelService;
    @Autowired
    private ITApplicationService tApplicationService;

    @RequiresPermissions("mmskhd:model:view")
    @GetMapping()
    public String model()
    {
        return prefix + "/model";
    }

    /**
     * 查询模板管理列表
     */
    @RequiresPermissions("mmskhd:model:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(TModel tModel)
    {
        startPage();
        List<TModel> list = tModelService.selectTModelList(tModel);

        return getDataTable(list);
    }

    /**
     * 导出模板管理列表
     */
    @RequiresPermissions("mmskhd:model:export")
    @Log(title = "模板管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(TModel tModel)
    {
        List<TModel> list = tModelService.selectTModelList(tModel);
        ExcelUtil<TModel> util = new ExcelUtil<TModel>(TModel.class);
        return util.exportExcel(list, "模板管理数据");
    }
    @RequestMapping("/uploadFile")
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file){

        Map<String, Object> map = new HashMap<>();
        try {

            String profile = modelFilePath;
            String filepath = FileUploadUtils.upload(profile,file);

            long size = file.getSize();
            size = size /1024; //KB

            String suffix = FileNameUtils.getSuffix(filepath);
            int type = 0;
            if(".mp4".equals(suffix) || ".3gp".equals(suffix) || ".gif".equals(suffix)) {
                type = 3;
            }else if(".jpg".equals(suffix) || ".png".equals(suffix) || ".jpeg".equals(suffix)) {
                type = 2;
            }

            map.put("size", size);
            map.put("filepath", filepath);
            map.put("type", type);

            logger.info(filepath);
        }catch(Exception e){
            e.printStackTrace();
        }

//        String localPath = ClassUtils.getDefaultClassLoader().getResource("static").getPath();
//        String fileName = FileNameUtils.getFileName(file.getOriginalFilename());
//        String realPath = localPath+ "/upload" + "/" + fileName;
//        logger.info("[M_MODEL][UPLOAD]realPath="+realPath);
//        logger.info("[M_MODEL][UPLOAD]fileName="+fileName);
//
//        boolean upload = FileUtilsNew.upload(file, realPath);
//        if(upload) {
//            logger.debug("文件名："+file.getOriginalFilename()+",上传成功！");
//        }
//        long size = file.getSize();
//        size = size /1024; //KB
//
//        String suffix = FileNameUtils.getSuffix(realPath);
//        int type = 0;
//        if(".mp4".equals(suffix) || ".3gp".equals(suffix) || ".gif".equals(suffix)) {
//            type = 3;
//        }else if(".jpg".equals(suffix) || ".png".equals(suffix) || ".jpeg".equals(suffix)) {
//            type = 2;
//        }


//        map.put("size", size);
//        map.put("filepath", "/upload/"+fileName);
//        map.put("type", type);

        return map;
    }

    /**
     * 新增模板管理
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        List<TApplication> applist = tApplicationService.selectTApplicationListN(user.getCompanyId());
        logger.info("[applist][RECHARGE] data="+ JSONObject.toJSONString(applist));
        mmap.put("applist", applist);
        return prefix + "/add";
    }

    /**
     * 新增保存模板管理
     */
    @RequiresPermissions("mmskhd:model:add")
    @Log(title = "模板管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public String addSave(TModel tModel, HttpServletRequest request)
    {
        tModel.setAddTime(new Date());
//        tModel.setAppId(0L);
        String modelId = null;
        modelId = DateUtil.formatDate(new Date(), "yyMMddHHmmss")+devNumber+ UUIDUtils.getSerial();
        tModel.setModelId(modelId);
        int id = 0;
        try {
            id = tModelService.insertTModel(tModel);
        } catch (Exception e) {
            logger.error("[M_MODEL] EX=",e);
        }
        if(id <= 0){
            return null;
        }

        String filePath = modelFilePath;
        String index= request.getParameterValues("index")[0];
        String[] indexValues = index.split(",");
        int i=1;
//        String localPath = ClassUtils.getDefaultClassLoader().getResource("static").getPath();
        if(indexValues != null && indexValues.length>0) {
            for(String st:indexValues) {
                String file = request.getParameterValues("file["+st+"]")[0];
                String file_size = request.getParameterValues("file_size["+st+"]")[0];
                String listorderFile = request.getParameterValues("listorder[file]["+st+"]")[0];
                String text = request.getParameterValues("text["+st+"]")[0];
                String listorderText = request.getParameterValues("listorder[text]["+st+"]")[0];
                boolean fileFlag = false;
                boolean textFlag = false;
                if(file !=null && Integer.parseInt(file_size)>0) {
                    if(ObjectUtils.isEmpty(text)) {
                        listorderFile = "0";
                    }
                    File rootfile = new File(filePath+File.separator+"model_"+modelId+File.separator+"frame_"+i+File.separator+"content_"+listorderFile);
                    if(rootfile.isDirectory()) {
                        rootfile.mkdirs();
                    }
                    String profile = modelFilePath;
                    String oldFilePath = profile+file.substring(file.lastIndexOf("/"),file.length());
                    logger.info(oldFilePath);
                    File oldFile = new File(oldFilePath);
                    File newFile = new File(rootfile+"//"+file.substring(file.lastIndexOf("/")+1));
                    fileFlag = FileUtilsNew.copyFile(oldFile, newFile);
                }
                if(!ObjectUtils.isEmpty(text)) {
                    if(Integer.parseInt(file_size)==0) {
                        listorderText = "0";
                    }
                    File rootfile = new File(filePath+File.separator+"model_"+modelId+File.separator+"frame_"+i+File.separator+"content_"+listorderText);
                    if(rootfile.isDirectory()) {
                        rootfile.mkdirs();
                    }
                    File txtfile = new File(filePath+File.separator+"model_"+modelId+File.separator+"frame_"+i+File.separator+"content_"+listorderText+File.separator+UUIDUtils.getUUID()+".txt");
                    try {
                        FileUtilsNew.createFile(txtfile);
                        FileWriter fw = new FileWriter(txtfile, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.append(text);
                        bw.close();
                        fw.close();
                        textFlag = true;
                    } catch (IOException e) {
                        logger.error("[M_MODEL] EX=",e);
                    }
                }
                if(fileFlag || textFlag) {
                    i++;
                }
            }
        }

//        return AjaxResult.success();

        return prefix + "/modelView_jump";

        //return toAjax(tModelService.insertTModel(tModel));
    }

    /**
     * 修改模板管理
     */
    @RequiresPermissions("mmskhd:model:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        TModel tModel = tModelService.selectTModelById(id);
        mmap.put("tModel", tModel);

        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        List<TApplication> applist = tApplicationService.selectTApplicationListN(user.getCompanyId());
        mmap.put("applist", applist);

        mmap.put("data", getFile(tModel));

        return prefix + "/edit";
    }

    /**
     * 修改保存模板管理
     */
    @RequiresPermissions("mmskhd:model:edit")
    @Log(title = "模板管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public String editSave(TModel tModel, HttpServletRequest request)
    {
        int id = 0;
        try {
            id = tModelService.updateTModel(tModel);
        } catch (Exception e) {
            logger.error("[M_MODEL] EX=",e);
        }
        if(id <= 0){
            return null;
        }
        TModel tempTModel =  tModelService.selectTModelById(tModel.getId());

        String filePath = modelFilePath;

        FileUtilsNew.deleteDir(filePath+File.separator+"model_"+tempTModel.getModelId());//删除原有文件

        String index= request.getParameterValues("index")[0];
        String[] indexValues = index.split(",");
        int i=1;
        String localPath = ClassUtils.getDefaultClassLoader().getResource("static").getPath();
        if(indexValues != null && indexValues.length>0) {
            for(String st:indexValues) {
                String file = request.getParameterValues("file["+st+"]")[0];
                String file_size = request.getParameterValues("file_size["+st+"]")[0];
                String listorderFile = request.getParameterValues("listorder[file]["+st+"]")[0];
                String text = request.getParameterValues("text["+st+"]")[0];
                String listorderText = request.getParameterValues("listorder[text]["+st+"]")[0];
                boolean fileFlag = false;
                boolean textFlag = false;
                //file !=null && Integer.parseInt(file_size)>0
                if(file !=null) {
                    if(ObjectUtils.isEmpty(text)) {
                        listorderFile = "0";
                    }
                    File rootfile = new File(filePath+File.separator+"model_"+tempTModel.getModelId()+File.separator+"frame_"+i+File.separator+"content_"+listorderFile);
                    if(rootfile.isDirectory()) {
                        rootfile.mkdirs();
                    }
                    String profile = modelFilePath;
                    String oldFilePath = profile+file.substring(file.lastIndexOf("/"),file.length());
                    logger.info(oldFilePath);
                    File oldFile = new File(oldFilePath);
                    File newFile = new File(rootfile+"//"+file.substring(file.lastIndexOf("/")+1));
                    fileFlag = FileUtilsNew.copyFile(oldFile, newFile);
                }
                if(!ObjectUtils.isEmpty(text)) {
                    if(Integer.parseInt(file_size)==0) {
                        listorderText = "0";
                    }
                    File rootfile = new File(filePath+File.separator+"model_"+tempTModel.getModelId()+File.separator+"frame_"+i+File.separator+"content_"+listorderText);
                    if(rootfile.isDirectory()) {
                        rootfile.mkdirs();
                    }
                    File txtfile = new File(filePath+File.separator+"model_"+tempTModel.getModelId()+File.separator+"frame_"+i+File.separator+"content_"+listorderText+File.separator+UUIDUtils.getUUID()+".txt");
                    try {
                        FileUtilsNew.createFile(txtfile);
                        FileWriter fw = new FileWriter(txtfile, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.append(text);
                        bw.close();
                        fw.close();
                        textFlag = true;
                    } catch (IOException e) {
                        logger.error("[M_MODEL] EX=",e);
                    }
                }
                if(fileFlag || textFlag) {
                    i++;
                }
            }
        }

        return prefix + "/modelView_jump";
    }

    /**
     * 删除模板管理
     */
    @RequiresPermissions("mmskhd:model:remove")
    @Log(title = "模板管理", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(tModelService.deleteTModelByIds(ids));
    }

    /**
     * 模板详情
     */
    @RequiresPermissions("mmskhd:model:edit")
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, ModelMap mmap)
    {
        TModel tModel = tModelService.selectTModelById(id);
        logger.info("[tModel][tModel] data="+ JSONObject.toJSONString(tModel));
        mmap.put("tModel", tModel);

        mmap.put("data", getFile(tModel));
        return prefix + "/detail";
    }
    @GetMapping("/selectByAppID")
    @ResponseBody
    public AjaxResult selectByAppID(long appId)
    {
        List<TModel> model = tModelService.selectModelByAppId(appId);
        logger.info("[model][model] data="+ JSONObject.toJSONString(model));
        return success(model);

    }

    @GetMapping("/selectTModelById")
    @ResponseBody
    public AjaxResult selectTModelById(long id)
    {
        TModel tModel = tModelService.selectTModelById(id);
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("tModel",tModel);
        resultMap.put("file",getFile(tModel));
        return success(resultMap);
    }

    private Map<String, Object>  getFile(TModel tModel){
        Map<String, Object> map=new HashMap<String,Object>();
        String filePath = modelFilePath;
        File file = new File(filePath+File.separator+"model_"+tModel.getModelId());

        map.put("title", tModel.getTitle());
        Map<String, Object> contentMap = new HashMap<String,Object>();

        List<File> listFiles = FileUtilsNew.sortFileName(file);
        int i = 1;
        if(!ObjectUtils.isEmpty(listFiles) && listFiles.size()>0) {
            List<Map<String, String>> list = null;
            for(File subFile : listFiles) {
                list = new ArrayList<Map<String,String>>();
                List<File> listFiles2 = FileUtilsNew.sortFileName(subFile);
                if(!ObjectUtils.isEmpty(listFiles2) && listFiles2.size() > 0) {
                    for(File subFile2 : listFiles2) {

                        List<File> listFiles3 = FileUtilsNew.sortFileName(subFile2);
                        if(!ObjectUtils.isEmpty(listFiles3) && listFiles3.size() > 0) {
                            for(File subFile3 : listFiles3) {
                                logger.info(subFile3.getPath());
                                ///home/upload/model_220304222825331/frame_1/content_0/34322e10caa94c00b17c6198bdffc0f7.txt
                                String temp = subFile3.getPath().replace("/","\\");
                                String sort = temp.substring(temp.lastIndexOf("\\")-1,temp.lastIndexOf("\\"));
                                String fileName = subFile3.getName();
                                String suffix = FileNameUtils.getSuffix(fileName);
                                int type = 0;
                                String content = "";
                                if(".mp4".equals(suffix) || ".3gp".equals(suffix) || ".gif".equals(suffix)) {
                                    type = 3;
                                    content = "/profile/"+fileName;
                                }else if(".jpg".equals(suffix) || ".png".equals(suffix) || ".jpeg".equals(suffix)) {
                                    type = 2;
                                    content = "/profile/"+fileName;
                                }else if(".txt".equals(suffix)) {
                                    type = 1;
                                    content = FileUtilsNew.txt2String(subFile3);
                                }
                                if(type != 0) {
                                    Map<String, String> subMap = new HashMap<>();
                                    subMap.put("type", String.valueOf(type));
                                    subMap.put("content", content);
                                    subMap.put("sort",sort);
                                    list.add(subMap);
                                }
                            }
                        }
                    }
                }
                contentMap.put(String.valueOf(i), list);
                i++;
            }
        }
        return contentMap;
    }


    /**
     * 删除模板
     */
    @RequiresPermissions("mmskhd:model:delete")
    @Log(title = "删除模板", businessType = BusinessType.DELETE)
    @PostMapping( "/delete/{id}")
    @ResponseBody
    public AjaxResult delete(@PathVariable("id") Long id)
    {
        return toAjax(tModelService.deleteTModelById(id));
    }


    /**
     * 复制模板管理
     */
    @RequiresPermissions("mmskhd:model:copy")
    @GetMapping("/copy/{id}")
    public String copy(@PathVariable("id") Long id, ModelMap mmap)
    {
        TModel tModel = tModelService.selectTModelById(id);
        mmap.put("tModel", tModel);
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        List<TApplication> applist = tApplicationService.selectTApplicationListN(user.getCompanyId());
        mmap.put("applist", applist);
        mmap.put("data", getFile(tModel));
        return prefix + "/copy";
    }


    /**
     * 复制模板管理
     */
    @RequiresPermissions("mmskhd:model:send")
    @GetMapping("/send/{id}")
    public String send(@PathVariable("id") Long id, ModelMap mmap)
    {
        TModel tModel = tModelService.selectTModelById(id);
//        mmap.put("tModel", tModel);
//        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
//        List<TApplication> applist = tApplicationService.selectTApplicationListN(user.getCompanyId());
        mmap.put("tModelid", tModel);
//        mmap.put("data", getFile(tModel));
        return "mmskhd/staticSender/mmsSender";
    }


    /**
     * 修改保存模板管理
     */
    @RequiresPermissions("mmskhd:model:copy")
    @Log(title = "复制模板管理", businessType = BusinessType.UPDATE)
    @PostMapping("/copySave")
    public String copySave(TModel tModel, HttpServletRequest request)
    {
        tModel.setAddTime(new Date());
        int id = 0;
        try {
            TModel tempTModel = tModelService.selectTModelById(tModel.getId());
            tModel.setModelId(tempTModel.getModelId());
            id = tModelService.insertTModel(tModel);
        } catch (Exception e) {
            logger.error("[M_MODEL] EX=",e);
        }
        /*if(id <= 0){
            return null;
        }*/

        /*String filePath = modelFilePath;

        String index= request.getParameterValues("index")[0];
        String[] indexValues = index.split(",");
        int i=1;
        String localPath = ClassUtils.getDefaultClassLoader().getResource("static").getPath();
        if(indexValues != null && indexValues.length>0) {
            for(String st:indexValues) {
                String file = request.getParameterValues("file["+st+"]")[0];
                String file_size = request.getParameterValues("file_size["+st+"]")[0];
                String listorderFile = request.getParameterValues("listorder[file]["+st+"]")[0];
                String text = request.getParameterValues("text["+st+"]")[0];
                String listorderText = request.getParameterValues("listorder[text]["+st+"]")[0];
                boolean fileFlag = false;
                boolean textFlag = false;
                if(file !=null && Integer.parseInt(file_size)>0) {
                    if(ObjectUtils.isEmpty(text)) {
                        listorderFile = "1";
                    }
                    File rootfile = new File(filePath+File.separator+"model_"+modelId+File.separator+"frame_"+i+File.separator+"content_"+listorderFile);
                    if(rootfile.isDirectory()) {
                        rootfile.mkdirs();
                    }
                    File oldFile = new File(localPath+file);
                    File newFile = new File(rootfile+"//"+file.substring(file.lastIndexOf("/")+1));
                    fileFlag = FileUtilsNew.copyFile(oldFile, newFile);
                }
                if(!ObjectUtils.isEmpty(text)) {
                    if(Integer.parseInt(file_size)==0) {
                        listorderText = "1";
                    }
                    File rootfile = new File(filePath+File.separator+"model_"+modelId+File.separator+"frame_"+i+File.separator+"content_"+listorderText);
                    if(rootfile.isDirectory()) {
                        rootfile.mkdirs();
                    }
                    File txtfile = new File(filePath+File.separator+"model_"+modelId+File.separator+"frame_"+i+File.separator+"content_"+listorderText+File.separator+UUIDUtils.getUUID()+".txt");
                    try {
                        FileUtilsNew.createFile(txtfile);
                        FileWriter fw = new FileWriter(txtfile, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.append(text);
                        bw.close();
                        fw.close();
                        textFlag = true;
                    } catch (IOException e) {
                        logger.error("[M_MODEL] EX=",e);
                    }
                }
                if(fileFlag || textFlag) {
                    i++;
                }
            }
        }*/

        return prefix + "/modelView_jump";
    }


}
