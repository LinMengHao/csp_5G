package com.xzkj.platform.model.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.config.RuoYiConfig;
import com.xzkj.platform.common.constant.Constants;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.page.TableDataInfo;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.file.FileNameUtils;
import com.xzkj.platform.common.utils.file.FileUploadUtils;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.model.domain.ModelInfo;
import com.xzkj.platform.model.domain.ModelMaterial;
import com.xzkj.platform.model.service.IModelInfoService;
import com.xzkj.platform.model.service.IModelMaterialService;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.redis.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板信息e_model_infoController
 * 
 * @author ruoyi
 * @date 2022-11-24
 */
@Controller
@RequestMapping("/model/model")
public class ModelInfoController extends BaseController
{
    private String prefix = "model/model";

    @Value("${model.filePath}")
    private String modelFilePath;

    @Autowired
    private IModelInfoService modelInfoService;
    @Autowired
    private IModelMaterialService modelMaterialService;

    @Autowired
    private IChannelService channelService;


    @RequiresPermissions("model:model:view")
    @GetMapping()
    public String model()
    {
        return prefix + "/model";
    }

    @PostMapping("/report")
    @ResponseBody
    public void report(String msg){
        System.out.println("=====================模版审核状态回调=========================");
        System.out.println("msg: "+msg);
    }

    /**
     * 查询模板信息e_model_info列表
     */
    @RequiresPermissions("model:model:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ModelInfo modelInfo)
    {
        startPage();
        List<ModelInfo> list = modelInfoService.selectModelInfoList(modelInfo);
        return getDataTable(list);
    }

    /**
     * 导出模板信息e_model_info列表
     */
    @RequiresPermissions("model:model:export")
    @Log(title = "模板信息e_model_info", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ModelInfo modelInfo)
    {
        List<ModelInfo> list = modelInfoService.selectModelInfoList(modelInfo);
        ExcelUtil<ModelInfo> util = new ExcelUtil<ModelInfo>(ModelInfo.class);
        return util.exportExcel(list, "模板信息e_model_info数据");
    }

    /**
     * 新增模板信息e_model_info
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存模板信息e_model_info
     */
    @RequiresPermissions("model:model:add")
    @Log(title = "模板信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ModelInfo modelInfo, HttpServletRequest request)
    {
        int result = modelInfoService.insertModelInfo(modelInfo);
        if(result <= 0){
            return toAjax(result);
        }

        String modelId=modelInfo.getModelId();
        handleModelFile(modelId,request);
        return toAjax(result);
    }

    /**
     * 修改模板信息e_model_info
     */
    @RequiresPermissions("model:model:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        ModelInfo modelInfo = modelInfoService.selectModelInfoById(id);
        mmap.put("modelInfo", modelInfo);

        Map<Integer, Object> contentMap = selectModelFile(modelInfo.getModelId());
        mmap.put("data", contentMap);

        return prefix + "/edit";
    }
    /**
     * TODO
     * 复制模板信息e_model_info
     */
    @RequiresPermissions("model:model:add")
    @PostMapping("/copy")
    @ResponseBody
    public AjaxResult copy(String id)
    {
        System.out.println("id: "+id);
        long l = Long.parseLong(id.trim());
        //获取源文件基本信息
        ModelInfo modelInfo = modelInfoService.selectModelInfoById(l);
        System.out.println("copy: "+modelInfo.toString());

        //获取源素材信息
        ModelMaterial material=new ModelMaterial();
        material.setModelId(modelInfo.getModelId());
        List<ModelMaterial> modelMaterials = modelMaterialService.selectModelMaterialList(material);

        int result=modelInfoService.copyModelInfo(modelInfo,modelMaterials);



        return toAjax(result);
    }

    /**
     * 模板审核详情
     */
    @RequiresPermissions("model:model:check")
    @GetMapping("/check/{id}")
    public String check(@PathVariable("id") Long id, ModelMap mmap)
    {
        ModelInfo modelInfo = modelInfoService.selectModelInfoById(id);
        mmap.put("modelInfo", modelInfo);

        Map<Integer, Object> contentMap = selectModelFile(modelInfo.getModelId());
        mmap.put("data", contentMap);
        return prefix + "/check";
    }

    /**
     * 模板审核通过
     */
    @GetMapping("/checkSuccess/{modelId}/{id}")
    public String checkSuccess(@PathVariable("modelId") String modelId,@PathVariable("id") Long id,ModelMap mmap)
    {
        System.out.println("id: "+id);
        ModelInfo modelInfo = modelInfoService.selectModelInfoById(id);
        mmap.put("modelInfo", modelInfo);
        mmap.put("id",modelInfo.getId());


        //TODO 获取渠道信息列表
        List<Channel> channellist = channelService.selectChannelListAll(0L);
        mmap.put("channellist", channellist);
        mmap.put("modelId",modelId);
        System.out.println(modelId);

        Map<Integer, Object> contentMap = selectModelFile(modelId);
        mmap.put("data", contentMap);
        return prefix + "/checkSuccess";
    }

    //手动审核成功
    @PostMapping("/checkedSuccess")
    @ResponseBody
    public AjaxResult checkedSuccess(ModelInfo modelInfo,HttpServletRequest request){
        System.out.println("modelInfo channelId: "+modelInfo.getChannelId());
        ModelInfo modelInfo1 = modelInfoService.selectModelInfoById(modelInfo.getId());
        modelInfo1.setChannelId(modelInfo.getChannelId());
        modelInfo1.setExtNum(modelInfo.getExtNum());
        modelInfo1.setToCmcc(modelInfo.getToCmcc());
        modelInfo1.setToTelecom(modelInfo.getToTelecom());
        modelInfo1.setToUnicom(modelInfo.getToUnicom());
        modelInfo1.setToInternational(modelInfo.getToInternational());
        Long companyId = modelInfo.getCompanyId();
        modelInfo1.setStatus(1L);
        modelInfo1.setInfo("审核成功");
        String s = JSONObject.toJSONString(modelInfo1);

        int result=modelInfoService.updateModelInfoByModelId(modelInfo1);

        //回调客户
        RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,s);
        RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);

        return toAjax(result);

    }
    /**
     * 模板审核拒绝
     */
    @GetMapping("/checkFailed/{modelId}/{id}")
    public String checkFailed(@PathVariable("modelId") String modelId,@PathVariable("id") Long id,ModelMap mmap)
    {
        System.out.println("id: "+id);
        ModelInfo modelInfo = modelInfoService.selectModelInfoById(id);
        mmap.put("modelInfo", modelInfo);
        mmap.put("id",modelInfo.getId());


        //TODO 获取渠道信息列表
        List<Channel> channellist = channelService.selectChannelListAll(0L);
        mmap.put("channellist", channellist);
        mmap.put("modelId",modelId);
        System.out.println(modelId);

        Map<Integer, Object> contentMap = selectModelFile(modelId);
        mmap.put("data", contentMap);
        return prefix + "/checkFailed";
    }

    //手动审核失败
    @PostMapping("/checkedFailed")
    @ResponseBody
    public AjaxResult checkedFailed(ModelInfo modelInfo,HttpServletRequest request){
        System.out.println("modelInfo channelId: "+modelInfo.getChannelId());
        ModelInfo modelInfo1 = modelInfoService.selectModelInfoById(modelInfo.getId());
        modelInfo1.setChannelId(modelInfo.getChannelId());
        modelInfo1.setExtNum(modelInfo.getExtNum());
        modelInfo1.setToCmcc(modelInfo.getToCmcc());
        modelInfo1.setToTelecom(modelInfo.getToTelecom());
        modelInfo1.setToUnicom(modelInfo.getToUnicom());
        modelInfo1.setToInternational(modelInfo.getToInternational());
        Long companyId = modelInfo.getCompanyId();
        modelInfo1.setStatus(2L);
        modelInfo1.setInfo("审核失败");
        String s = JSONObject.toJSONString(modelInfo1);


        int result=modelInfoService.updateModelInfoByModelId(modelInfo1);

        //回调客户
        RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,s);
        RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);

        return toAjax(result);

    }
    /**
     * 模板提交通道审核
     */
    @GetMapping("/checkChannel/{modelId}/{id}")
    public String checkChannel(@PathVariable("modelId") String modelId, @PathVariable("id") Long id, ModelMap mmap)
    {
        System.out.println("id: "+id);
        ModelInfo modelInfo = modelInfoService.selectModelInfoById(id);
        mmap.put("modelInfo", modelInfo);
        mmap.put("id",modelInfo.getId());


        //TODO 获取渠道信息列表
        List<Channel> channellist = channelService.selectChannelListAll(0L);
        mmap.put("channellist", channellist);
        mmap.put("modelId",modelId);
        System.out.println(modelId);

        Map<Integer, Object> contentMap = selectModelFile(modelId);
        mmap.put("data", contentMap);
        return prefix + "/checkChannel";
    }

    @Autowired
    RestTemplate restTemplate;

    @PostMapping("/submitToChannel")
    @ResponseBody
    public AjaxResult submitToChannel(ModelInfo modelInfo,HttpServletRequest request){
        System.out.println("modelInfo channelId: "+modelInfo.getChannelId());
        ModelInfo modelInfo1 = modelInfoService.selectModelInfoById(modelInfo.getId());
        modelInfo1.setChannelId(modelInfo.getChannelId());
        modelInfo1.setToCmcc(modelInfo.getToCmcc());
        modelInfo1.setToTelecom(modelInfo.getToTelecom());
        modelInfo1.setToUnicom(modelInfo.getToUnicom());
        modelInfo1.setToInternational(modelInfo.getToInternational());
        //模版拓展码号
        modelInfo1.setModelExt(modelInfo.getId());
        Long appExt = modelInfo1.getAppExt();
        String s1 = String.valueOf(appExt);

        modelInfo1.setExtNum(s1+modelInfo1.getModelExt());


        //更新模版的拓展号
        ModelInfo modelInfo2=new ModelInfo();
        modelInfo2.setModelId(modelInfo.getModelId());
        modelInfo2.setAppExt(appExt);
        modelInfo2.setModelExt(modelInfo1.getModelExt());
        modelInfo2.setChannelId(modelInfo.getChannelId());
        modelInfoService.updateModelInfoByModelId(modelInfo2);

        System.out.println("modelInfo1: "+modelInfo1.toString());
        JSONObject jsonObject = selectModelFileJson(modelInfo.getModelId());
        System.out.println("json: "+jsonObject.toJSONString());
        Map<Integer,JSONArray> data = (Map<Integer,JSONArray>)jsonObject.get("data");
        System.out.println("data: "+data.toString());
        for(Map.Entry<Integer,JSONArray> entry:data.entrySet()){
            System.out.println("data["+entry.getKey()+"]: "+entry.getValue().toJSONString());
        }
        String s = JSONObject.toJSONString(modelInfo1);
        jsonObject.put("modelInfo",s);
        JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.getString("modelInfo"));
        System.out.println("jsonObject1: "+jsonObject1.toJSONString());
        System.out.println("modelId: "+jsonObject1.getString("modelId"));
        HttpHeaders headers=new HttpHeaders();
        headers.set("Content-Type","application/json");
        HttpEntity entity=new HttpEntity(jsonObject,headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:9131/mms/model/submitToChannel", entity, String.class);
        System.out.println("response: "+response.getBody());
        JSONObject jsonObject2 = JSONObject.parseObject(response.getBody());
        String mid = jsonObject2.getString("mid");
        String code = jsonObject2.getString("code");
        String msg = jsonObject2.getString("msg");
        int result=0;
        if("000".equals(code)){
            //TODO 根据mid修改状态
            ModelInfo info=new ModelInfo();
            info.setModelId(mid);
            info.setStatus(4L);
            info.setInfo("通道审核中");
            result=modelInfoService.updateModelInfoByModelId(info);
        }
        //将模版提交到运营商
        return toAjax(result);
    }

    /**
     * 修改保存模板信息e_model_info
     */
    @RequiresPermissions("model:model:edit")
    @Log(title = "模板信息e_model_info", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ModelInfo modelInfo, HttpServletRequest request)
    {
        int result = modelInfoService.updateModelInfo(modelInfo);
        if(result < 0){
            return toAjax(result);
        }

        String modelId=modelInfo.getModelId();
        int res = modelMaterialService.deleteModelMaterialByModelId(modelId);
        handleModelFile(modelId,request);

        return toAjax(result);
    }

    /**
     * 删除模板信息e_model_info
     */
    @RequiresPermissions("model:model:remove")
    @Log(title = "模板信息e_model_info", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(modelInfoService.deleteModelInfoByIds(ids));
    }


    /**
     * 视频短信模板文件上传
     * @param file
     * @return
     */
    @RequestMapping("/uploadFile")
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file){

        Map<String, Object> map = new HashMap<>();
        try {

            String profile = RuoYiConfig.getProfile()+modelFilePath;
            String filepath = FileUploadUtils.upload(profile,file);

            long size = file.getSize();
            size = size /1024; //KB

            String suffix = FileNameUtils.getSuffix(filepath);
            int type = 4;
            if(".mp4".equals(suffix) || ".3gp".equals(suffix) || ".gif".equals(suffix)) {
                type = 3;
            }else if(".jpg".equals(suffix) || ".png".equals(suffix) || ".jpeg".equals(suffix)) {
                type = 2;
            }

            map.put("size", size);
            map.put("filepath", filepath);
            map.put("type", type);

           // logger.info(filepath);
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }

    @GetMapping("/selectByAppID")
    @ResponseBody
    public AjaxResult selectByAppID(Long appId)
    {
        ModelInfo info=new ModelInfo();
        info.setAppId(appId);
        List<ModelInfo> list = modelInfoService.selectModelInfoList(info);
        return success(list);
    }

    @GetMapping("/selectTModelById")
    @ResponseBody
    public AjaxResult selectTModelById(String modelId)
    {
        System.out.println("modelId: "+modelId);
        ModelInfo modelInfo = modelInfoService.selectModelInfoByModelId(modelId);
        Map<Integer, Object> contentMap = selectModelFile(modelInfo.getModelId());
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("tModel",modelInfo);
        resultMap.put("file",contentMap);
        return success(resultMap);
    }
    //保存模板文件
    private void handleModelFile(String modelId,HttpServletRequest request){
        String filePath = RuoYiConfig.getProfile();
        String indexStr= request.getParameterValues("index")[0];
        String[] indexArr = indexStr.split(",");
        int i=1;
        if(indexArr.length>0) {
            List<ModelMaterial> materialList = new ArrayList<ModelMaterial>();
            for(String index:indexArr) {
                String file = request.getParameterValues("file["+index+"]")[0];
                String fileSizes = request.getParameterValues("file_size["+index+"]")[0];
                String fileType = request.getParameterValues("file_type["+index+"]")[0];
                String fileOrder = request.getParameterValues("listorder[file]["+index+"]")[0];
                int fileIndex = Integer.parseInt(fileOrder);
                String text = request.getParameterValues("text["+index+"]")[0];
                String textOrder = request.getParameterValues("listorder[text]["+index+"]")[0];
                int textIndex = Integer.parseInt(textOrder);
                boolean fileFlag = false;
                boolean textFlag = false;
                int fileSize = Integer.parseInt(fileSizes);
                if(!StringUtils.isEmpty(file) && fileSize>0) {
                    ModelMaterial material = new ModelMaterial();
                    int mediaType=Integer.parseInt(fileType);//媒体类型 1-文本 2-图片 3-视频 4-音频
                    int frameIndex = i;
                    int frameSort = fileIndex<=textIndex?1:2;
                    String ext = file.substring(file.lastIndexOf(".")+1);
                    String content=filePath +file.substring(Constants.RESOURCE_PREFIX.length()+1);

                    material.setModelId(modelId);
                    material.setMediaType(mediaType);
                    material.setFrameIndex(frameIndex);
                    material.setFrameSort(frameSort);
                    material.setExt(ext);
                    material.setContent(file);
                    material.setFilePath(content);
                    material.setFileSize(fileSize);

                    materialList.add(material);
                    fileFlag=true;
                }
                if(!StringUtils.isEmpty(text)) {
                    ModelMaterial material = new ModelMaterial();
                    int mediaType=1;//媒体类型 1-文本 2-图片 3-视频 4-音频
                    int frameIndex = i;
                    int frameSort = (fileFlag&&fileIndex<=textIndex)?2:1;
                    String ext = "txt";
                    String content=text;
                    fileSize = text.length();

                    material.setModelId(modelId);
                    material.setMediaType(mediaType);
                    material.setFrameIndex(frameIndex);
                    material.setFrameSort(frameSort);
                    material.setExt(ext);
                    material.setContent(content);
                    material.setFilePath("");
                    material.setFileSize(fileSize);

                    materialList.add(material);
                    textFlag = true;
                }
                if(fileFlag || textFlag) {
                    i++;
                }
            }
            if(materialList.size()!=0){
                modelMaterialService.insertModelMaterials(materialList);
            }
        }
    }
    //获取模板文件
    private Map<Integer, Object> selectModelFile(String modelId){
        ModelMaterial modelMaterial = new ModelMaterial();
        modelMaterial.setModelId(modelId);
        List<ModelMaterial> materialList = modelMaterialService.selectModelMaterialLists(modelMaterial);

        Map<Integer, Object> contentMap = new HashMap<Integer,Object>();
        List<Map<String, String>> list = null;
        int index=0;
        for (ModelMaterial material:materialList){
            int frameIndex = material.getFrameIndex();
            int mediaType = material.getMediaType();
            int frameSort = material.getFrameSort();
            int fileSize = material.getFileSize();
            String content = material.getContent();
            if(index!=frameIndex){
                if(index!=0&&list!=null){
                    contentMap.put(index, list);
                }
                index = frameIndex;
                list = new ArrayList<Map<String,String>>();
            }
            Map<String, String> subMap = new HashMap<>();
            subMap.put("type", String.valueOf(mediaType));
            subMap.put("size", String.valueOf(fileSize));
            subMap.put("content", content);
            subMap.put("sort",String.valueOf(frameSort));
            list.add(subMap);
        }
        if(index!=0&&list!=null&&list.size()!=0){
            contentMap.put(index, list);
        }
        return contentMap;
    }
    private JSONObject selectModelFileJson(String modelId){
        ModelMaterial modelMaterial = new ModelMaterial();
        modelMaterial.setModelId(modelId);
        List<ModelMaterial> materialList = modelMaterialService.selectModelMaterialLists(modelMaterial);

        JSONObject jsonObject=new JSONObject();
        Map<Integer,JSONArray> map=new HashMap<>();
        JSONArray jsonArray=null;
        int index=0;
        for (ModelMaterial material:materialList){
            int frameIndex = material.getFrameIndex();
            int mediaType = material.getMediaType();
            int frameSort = material.getFrameSort();
            int fileSize = material.getFileSize();
            String content = material.getContent();
            if(index!=frameIndex){
                if(index!=0&&jsonArray!=null){
//                    jsonObject.put(String.valueOf(index),jsonArray);
                    System.out.println("index: "+index+" jsonArray: "+jsonArray.toString());
                    map.put(index,jsonArray);
                }
                index = frameIndex;
                jsonArray=new JSONArray();
            }

            JSONObject jsonObject1=new JSONObject();

            jsonObject1.put("type", String.valueOf(mediaType));
            jsonObject1.put("size", String.valueOf(fileSize));
            jsonObject1.put("content", content);
            jsonObject1.put("sort",String.valueOf(frameSort));
            jsonArray.add(jsonObject1);
        }
        if(index!=0&&jsonArray!=null&&jsonArray.size()!=0){
//            jsonObject.put(String.valueOf(index),jsonArray);
            map.put(index,jsonArray);
        }
        jsonObject.put("data",map);
        return jsonObject;
    }


}
