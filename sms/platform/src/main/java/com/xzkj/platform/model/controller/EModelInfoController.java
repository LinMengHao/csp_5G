package com.xzkj.platform.model.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.config.RuoYiConfig;
import com.xzkj.platform.common.constant.Constants;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.domain.Ztree;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.common.utils.file.FileNameUtils;
import com.xzkj.platform.common.utils.file.FileUploadUtils;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.model.domain.EModelInfo;
import com.xzkj.platform.model.domain.ModelMaterial;
import com.xzkj.platform.model.service.IEModelInfoService;
import com.xzkj.platform.model.service.IModelMaterialService;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.redis.RedisUtils;
import com.xzkj.platform.sign.domain.ESignRelated;
import com.xzkj.platform.sign.service.IESignRelatedService;
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
import java.util.*;


/**
 * 模板信息e_model_infoController
 * 
 * @author linmenghao
 * @date 2023-02-06
 */
@Controller
@RequestMapping("/emodel/model")
public class EModelInfoController extends BaseController
{
    private String prefix = "emodel/model";

    @Value("${model.filePath}")
    private String modelFilePath;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private IEModelInfoService eModelInfoService;

    @Autowired
    private IModelMaterialService modelMaterialService;

    @Autowired
    private IESignRelatedService signRelatedService;

    @Autowired
    private IChannelService channelService;

    @Autowired
    private ITApplicationService tApplicationService;


    @RequiresPermissions("emodel:model:view")
    @GetMapping()
    public String modelInfo()
    {
        return prefix + "/model";
    }

    /**
     * 查询模板信息e_model_info树列表
     */
    @RequiresPermissions("emodel:model:list")
    @PostMapping("/list")
    @ResponseBody
    public List<EModelInfo> list(EModelInfo eModelInfo)
    {
        List<EModelInfo> list = eModelInfoService.selectEModelInfoList(eModelInfo);
        return list;
    }

    /**
     * 导出模板信息e_model_info列表
     */
    @RequiresPermissions("emodel:model:export")
    @Log(title = "模板信息e_model_info", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(EModelInfo eModelInfo)
    {
        List<EModelInfo> list = eModelInfoService.selectEModelInfoList(eModelInfo);
        ExcelUtil<EModelInfo> util = new ExcelUtil<EModelInfo>(EModelInfo.class);
        return util.exportExcel(list, "模板信息e_model_info数据");
    }

    /**
     * 新增模板信息e_model_info
     */
    @GetMapping(value = { "/add/{id}", "/add/" })
    public String add(@PathVariable(value = "id", required = false) Long id, ModelMap mmap)
    {
        if (StringUtils.isNotNull(id))
        {
            mmap.put("eModelInfo", eModelInfoService.selectEModelInfoById(id));
        }
        return prefix + "/add";
    }

    /**
     * 新增保存模板信息e_model_info
     */
    @RequiresPermissions("emodel:model:add")
    @Log(title = "模板信息e_model_info", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(EModelInfo eModelInfo, HttpServletRequest request)
    {
        int result = eModelInfoService.insertEModelInfo(eModelInfo);
        if(result <= 0){
            return toAjax(result);
        }

        String modelId=eModelInfo.getModelId();
        handleModelFile(modelId,request);
        return toAjax(result);
    }

    /**
     * 修改模板信息e_model_info
     */
    @RequiresPermissions("emodel:model:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        EModelInfo eModelInfo = eModelInfoService.selectEModelInfoById(id);
        mmap.put("eModelInfo", eModelInfo);

        Map<Integer, Object> contentMap = selectModelFile(eModelInfo.getModelId());
        mmap.put("data", contentMap);
        return prefix + "/edit";
    }

    /**
     * 修改保存模板信息e_model_info
     */
    @RequiresPermissions("emodel:model:edit")
    @Log(title = "模板信息e_model_info", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(EModelInfo eModelInfo,HttpServletRequest request)
    {
        System.out.println(eModelInfo.getSignId());
        int result = eModelInfoService.updateEModelInfo(eModelInfo);
        if(result < 0){
            return toAjax(result);
        }

        String modelId=eModelInfo.getModelId();
        int res = modelMaterialService.deleteModelMaterialByModelId(modelId);
        handleModelFile(modelId,request);

        return toAjax(result);
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
        EModelInfo eModelInfo = eModelInfoService.selectEModelInfoById(l);
        System.out.println("copy: "+eModelInfo.toString());

        //获取源素材信息
        ModelMaterial material=new ModelMaterial();
        material.setModelId(eModelInfo.getModelId());
        List<ModelMaterial> modelMaterials = modelMaterialService.selectModelMaterialList(material);

        int result=eModelInfoService.copyEModelInfo(eModelInfo,modelMaterials);



        return toAjax(result);
    }

    /**
     * TODO
     * 查询模板信息在运营商侧的状态
     */
    @RequiresPermissions("model:model:add")
    @PostMapping("/query")
    @ResponseBody
    public AjaxResult query(String id)
    {
        System.out.println("id: "+id);
        long l = Long.parseLong(id.trim());
        //获取源文件基本信息
        EModelInfo eModelInfo = eModelInfoService.selectEModelInfoById(l);
        System.out.println("query: "+eModelInfo.toString());
        Long appId = eModelInfo.getAppId();
        Long companyId = eModelInfo.getCompanyId();
        Long channelId = eModelInfo.getChannelId();
        String body = JSONObject.toJSONString(eModelInfo);
        RedisUtils.fifo_push(RedisUtils.FIFO_QUERY_MODEL_LIST+companyId+":"+appId+":"+channelId,body);
        RedisUtils.hash_incrBy(RedisUtils.HASH_QUERY_MODEL_TOTAL, companyId+"_"+appId+"_"+channelId, 1);

        return toAjax(1);
    }

    /**
     * TODO
     * 编辑策略
     */
    @GetMapping("/idea/{id}")
    public String idea(@PathVariable("id")String id, ModelMap mmap)
    {
        System.out.println("id: "+id);
        long l = Long.parseLong(id.trim());
        //获取源文件基本信息
        EModelInfo eModelInfo = eModelInfoService.selectEModelInfoById(l);
        //获取真子集
        EModelInfo eModelInfo1 = new EModelInfo();
        eModelInfo1.setPid(l);
        List<EModelInfo> list=eModelInfoService.selectEModelInfoList(eModelInfo1);

        mmap.put("eModelInfo",eModelInfo);
        String idea = eModelInfo.getIdea();
        if(!StringUtils.isEmpty(idea)){
            String[] split = idea.split(",");
            if (split.length>0){
                List<String> ideas = Arrays.asList(split);
                mmap.put("ideaList",ideas);
            }else {
                List<String> ideas=new ArrayList<>();
                ideas.add("0");
                mmap.put("ideaList",ideas);
            }
        } else {
            List<String> ideas=new ArrayList<>();
            ideas.add("0");
            mmap.put("ideaList",ideas);
        }
        mmap.put("list",list);
        mmap.put("eModelInfo",eModelInfo);

        return prefix+"/idea";
    }

    @RequiresPermissions("emodel:model:edit")
    @Log(title = "签名", businessType = BusinessType.UPDATE)
    @PostMapping("/idea")
    @ResponseBody
    public AjaxResult ideaSave(EModelInfo eModelInfo)
    {
        return toAjax(eModelInfoService.updateEModelInfo(eModelInfo));
    }

    @GetMapping("/checkSuccess/{id}")
    public String checkSuccess(@PathVariable("id") Long id,ModelMap mmap){
        //获取源文件基本信息
        EModelInfo eModelInfo = eModelInfoService.selectEModelInfoById(id);
        mmap.put("eModelInfo",eModelInfo);
        return prefix+"/checkSuccess";
    }

    @RequiresPermissions("emodel:sign:edit")
    @Log(title = "签名", businessType = BusinessType.UPDATE)
    @PostMapping("/checkSuccess")
    @ResponseBody
    public AjaxResult checkSuccessSave(EModelInfo eModelInfo)
    {

        Long companyId = eModelInfo.getCompanyId();
        eModelInfo.setStatus(1L);
        int i=eModelInfoService.updateEModelInfo(eModelInfo);
        EModelInfo info = eModelInfoService.selectEModelInfoById(eModelInfo.getId());
        TApplication app = tApplicationService.selectTApplicationById(info.getAppId());
        info.setAppName(app.getAppName());
        info.setToCmcc(eModelInfo.getToCmcc());
        info.setToUnicom(eModelInfo.getToUnicom());
        info.setToTelecom(eModelInfo.getToTelecom());
        info.setToInternational(eModelInfo.getToInternational());
        info.setStatus(1L);
        String reason=eModelInfo.getInfo();
        if(StringUtils.isBlank(reason)){
            reason="审核成功";
        }
        info.setInfo(reason);
        String s = JSONObject.toJSONString(info);
        RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,s);
        RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);

        return toAjax(i);
    }


    @GetMapping("/checkFailed/{id}")
    public String checkFailed(@PathVariable("id") Long id,ModelMap mmap){
        //获取源文件基本信息
        EModelInfo eModelInfo = eModelInfoService.selectEModelInfoById(id);
        mmap.put("eModelInfo",eModelInfo);
        return prefix+"/checkFailed";
    }

    @RequiresPermissions("emodel:sign:edit")
    @Log(title = "签名", businessType = BusinessType.UPDATE)
    @PostMapping("/checkFailed")
    @ResponseBody
    public AjaxResult checkFailedSave(EModelInfo eModelInfo)
    {

        Long companyId = eModelInfo.getCompanyId();
        eModelInfo.setStatus(2l);
        int i=eModelInfoService.updateEModelInfo(eModelInfo);
        EModelInfo info = eModelInfoService.selectEModelInfoById(eModelInfo.getId());
        TApplication app = tApplicationService.selectTApplicationById(info.getAppId());
        info.setAppName(app.getAppName());
        info.setToCmcc(eModelInfo.getToCmcc());
        info.setToUnicom(eModelInfo.getToUnicom());
        info.setToTelecom(eModelInfo.getToTelecom());
        info.setToInternational(eModelInfo.getToInternational());
        info.setStatus(2L);
        String reason=eModelInfo.getInfo();
        if(StringUtils.isBlank(reason)){
            reason="审核失败";
        }
        info.setInfo(reason);
        String s = JSONObject.toJSONString(info);
        //回调客户
        RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,s);
        RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);

        return toAjax(i);
    }


    /**
     * 模板审核详情
     */
    @RequiresPermissions("emodel:model:check")
    @GetMapping("/check/{id}")
    public String check(@PathVariable("id") Long id, ModelMap mmap)
    {
        EModelInfo modelInfo = eModelInfoService.selectEModelInfoById(id);
        mmap.put("eModelInfo", modelInfo);

        Map<Integer, Object> contentMap = selectModelFile(modelInfo.getModelId());
        mmap.put("data", contentMap);
        List<Channel> channellist = channelService.selectChannelList(0L);
        mmap.put("channellist", channellist);
        return prefix + "/check";
    }

    @PostMapping("/submitToChannel")
    @ResponseBody
    public AjaxResult submitToChannel(EModelInfo modelInfo,HttpServletRequest request){
        System.out.println("modelInfo channelId: "+modelInfo.getChannelId());

        Long toCmcc = modelInfo.getToCmcc();
        Long toUnicom = modelInfo.getToUnicom();
        Long toTelecom = modelInfo.getToTelecom();
        Long toInternational = modelInfo.getToInternational();

        //移动模版审核
        if(toCmcc==1){
            //根据签名ID和通道ID查询出通道侧签名ID
            ESignRelated related=new ESignRelated();
            related.setSignId(modelInfo.getSignId());
            related.setChannelId(modelInfo.getChannelId());
            //映射启用状态
            related.setStatus(1l);
            List<ESignRelated> list = signRelatedService.selectESignRelatedList(related);
            if(list == null||list.size()!=1){
                return AjaxResult.error("签名异常");
            }
            ESignRelated related1 = list.get(0);
            modelInfo.setSignId(related1.getChannelSignId());
        }

        //模版拓展码号
        modelInfo.setModelExt(modelInfo.getId());
        Long appExt = modelInfo.getAppExt();
        String s1 = String.valueOf(appExt);

        modelInfo.setExtNum(s1+modelInfo.getModelExt());


        //更新模版的拓展号
        EModelInfo modelInfo2=new EModelInfo();
        modelInfo2.setModelId(modelInfo.getModelId());
        modelInfo2.setAppExt(appExt);
        modelInfo2.setModelExt(modelInfo.getModelExt());
        modelInfo2.setChannelId(modelInfo.getChannelId());
        modelInfo2.setToCmcc(toCmcc);
        modelInfo2.setToTelecom(toTelecom);
        modelInfo2.setToUnicom(toUnicom);
        modelInfo2.setToInternational(toInternational);
        eModelInfoService.updateModelInfoByModelId(modelInfo2);

        System.out.println("modelInfo: "+modelInfo.toString());
        JSONObject jsonObject = selectModelFileJson(modelInfo.getModelId());
        System.out.println("json: "+jsonObject.toJSONString());
        Map<Integer, JSONArray> data = (Map<Integer,JSONArray>)jsonObject.get("data");
        System.out.println("data: "+data.toString());
        for(Map.Entry<Integer,JSONArray> entry:data.entrySet()){
            System.out.println("data["+entry.getKey()+"]: "+entry.getValue().toJSONString());
        }
        String s = JSONObject.toJSONString(modelInfo);
        jsonObject.put("modelInfo",s);
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
            EModelInfo info=new EModelInfo();
            info.setModelId(mid);
            info.setStatus(4L);
            info.setInfo("通道审核中");
            result=eModelInfoService.updateModelInfoByModelId(info);
        }
        //将模版提交到运营商
        return toAjax(result);
    }

    /**
     * 删除
     */
    @RequiresPermissions("emodel:model:remove")
    @Log(title = "模板信息e_model_info", businessType = BusinessType.DELETE)
    @GetMapping("/remove/{id}")
    @ResponseBody
    public AjaxResult remove(@PathVariable("id") Long id)
    {
        return toAjax(eModelInfoService.deleteEModelInfoById(id));
    }

    /**
     * 选择模板信息e_model_info树
     */
    @GetMapping(value = { "/selectModelInfoTree/{id}", "/selectModelInfoTree/" })
    public String selectModelInfoTree(@PathVariable(value = "id", required = false) Long id, ModelMap mmap)
    {
        if (StringUtils.isNotNull(id))
        {
            mmap.put("eModelInfo", eModelInfoService.selectEModelInfoById(id));
        }
        return prefix + "/tree";
    }

    /**
     * 加载模板信息e_model_info树列表
     */
    @GetMapping("/treeData")
    @ResponseBody
    public List<Ztree> treeData()
    {
        List<Ztree> ztrees = eModelInfoService.selectEModelInfoTree();
        return ztrees;
    }

    /**
     * 视频短信模板文件上传
     * @param file
     * @return
     */
    @RequestMapping("add/uploadFile")
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
                if(!org.apache.commons.lang3.StringUtils.isEmpty(file) && fileSize>0) {
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
                if(!org.apache.commons.lang3.StringUtils.isEmpty(text)) {
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
