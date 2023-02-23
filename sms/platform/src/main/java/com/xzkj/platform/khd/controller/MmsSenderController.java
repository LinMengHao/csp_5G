package com.xzkj.platform.khd.controller;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.utils.MD5Utils;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.domain.TModel;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.khd.service.ITModelService;
import com.xzkj.platform.model.domain.ModelInfo;
import com.xzkj.platform.model.service.IModelInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

/**
 * 发送静态，还是动态发送.测试
 * 
 * @author lixl
 * @date 2022-01-24
 */
@Controller
@RequestMapping("/mmskhd/staticSender")
public class MmsSenderController extends BaseController
{
    private String prefix = "mmskhd/staticSender";

    @Autowired
    private ITModelService tModelService;
    @Autowired
    private ITApplicationService tApplicationService;

    @Autowired
    private ITApplicationService iTApplicationService;

    @Autowired
    private IModelInfoService modelInfoService;

    @Autowired
    RestTemplate restTemplate;

//    @RequiresPermissions("mmskhd:staticSender:view")
//    @GetMapping()
//    public String mt(ModelMap mmap)
//    {
//        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
//
//        List<TModel> modellist = tModelService.selectTModelListN(user.getCompanyId());
//        List<TApplication> applist = tApplicationService.selectTApplicationListN(user.getCompanyId());
////        logger.info("[applist][RECHARGE] data="+ JSONObject.toJSONString(applist));
//        mmap.put("applist", applist);
//        return prefix + "/mmsSender";
//    }
    //TODO 新版
    @RequiresPermissions("mmskhd:staticSender:view")
    @GetMapping("/test")
    public String mtest(ModelMap mmap)
    {
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();

        List<TModel> modellist = tModelService.selectTModelListN(user.getCompanyId());
        ModelInfo info = new ModelInfo();
        info.setCompanyId(user.getCompanyId());
        List<ModelInfo> modelInfos = modelInfoService.selectModelInfoList(info);
        List<TApplication> applist = tApplicationService.selectTApplicationList(new TApplication());
    //        logger.info("[applist][RECHARGE] data="+ JSONObject.toJSONString(applist));
        mmap.put("applist", applist);
        mmap.put("modelInfos",modelInfos);
        return prefix + "/mmsSenderTest";
    }

    //TODO 新版 升级版
    @RequiresPermissions("mmskhd:staticSender:view")
    @GetMapping()
    public String mt(ModelMap mmap)
    {
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();

        List<TModel> modellist = tModelService.selectTModelListN(user.getCompanyId());
        ModelInfo info = new ModelInfo();
        info.setCompanyId(user.getCompanyId());
        List<ModelInfo> modelInfos = modelInfoService.selectModelInfoList(info);
        List<TApplication> applist = tApplicationService.selectTApplicationListN(user.getCompanyId());
//        logger.info("[applist][RECHARGE] data="+ JSONObject.toJSONString(applist));
        mmap.put("applist", applist);
        mmap.put("modelInfos",modelInfos);
        return prefix + "/mmsSender";
    }

    //TODO 新版
    @RequiresPermissions("mmskhd:staticSender:view")
    @PostMapping("/signature")
    @ResponseBody
    public AjaxResult signature(Long appId, String modelId, String param1, String title, String url){
        logger.info("[LOGIN] appId=" + appId+"modelId="+modelId+"param1="+param1+"phones="+title+"url="+url);
        String[] titleArr = title.replaceAll("，",",").split(",");
        if(titleArr.length > 200){
            return AjaxResult.error("单次提交号码不能超过200个");
        }
        if(!ObjectUtils.isEmpty(param1)){
            String[] param1Arr = param1.split("\\|");
            System.out.println(param1Arr.length+param1Arr[0]);
            if(param1Arr.length != titleArr.length) {
                return AjaxResult.error("参数组与手机号码数量不一致");
            }
        }
        //TODO
        TApplication tApplication = iTApplicationService.selectTApplicationById(appId);
        String appName = tApplication.getAppName();
        String password = tApplication.getPassword();
        long time = new Date().getTime();
        String token = MD5Utils.MD5Encode("acc="+appName+"&ts="+time+"|||pwd="+password);
        JSONObject json=new JSONObject();
        json.put("acc",appName);
        json.put("ts",time);
        json.put("mid",modelId);
        json.put("token",token);
        json.put("mobs",title);
        if(!StringUtils.isEmpty(param1)){
            json.put("params",param1);

        }
        if (!StringUtils.isEmpty(url)){
            json.put("url",url);
        }
        logger.info("提交数据：{}",json);
        HttpHeaders headers=new HttpHeaders();
        headers.set("Content-Type","application/json");
        HttpEntity entity=new HttpEntity(json,headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:9131/mms/submit", entity, String.class);
        System.out.println("response: "+response.getBody());
        JSONObject jsonObject2 = JSONObject.parseObject(response.getBody());
        String mid = jsonObject2.getString("mid");
        String code = jsonObject2.getString("code");
        String msg = jsonObject2.getString("msg");
        logger.info("测试下发,提交数据：{},  响应：{}",json,jsonObject2);
        return AjaxResult.success(jsonObject2);
    }

//    @RequiresPermissions("mmskhd:staticSender:view")
//    @PostMapping("/signature")
//    @ResponseBody
//    public AjaxResult signature(Long appId, Long modelId, String param1, String title){
//        logger.info("[LOGIN] appId=" + appId+"modelId="+modelId+"param1"+param1);
//        String[] titleArr = title.split(",");
//        if(titleArr.length > 200){
//            return AjaxResult.error("单次提交号码不能超过200个");
//        }
////        if(!ObjectUtils.isEmpty(param1)){
////            String[] param1Arr = param1.split(",");
////            if(param1Arr.length != titleArr.length) {
////                return AjaxResult.error("参数组与手机号码数量一致");
////            }
////        }
//        TModel tModel = tModelService.selectTModelById(modelId);
//        TApplication tApplication = iTApplicationService.selectTApplicationById(appId);
//        String appid = tApplication.getAppName();
//        Long timestamp = System.currentTimeMillis();
//        //sign=md5(pwd+ appid + timestamp + pwd)
//        String signStr = tApplication.getPassword()+appid+timestamp+tApplication.getPassword();
//        String sign = Md5Utils.hash(signStr);
//        Map<String, Object> bodyMap = new HashMap<>();
//        bodyMap.put("mms_from","");
//        bodyMap.put("mms_id",tModel.getModelId());
//        bodyMap.put("phones",title);
//        if(!ObjectUtils.isEmpty(param1)){
////            bodyMap.put("templateParam",param1);
//            String[] param1Arr = param1.split(",");
//            List<Object> resultList = new ArrayList<>();
//            List<Object> tempList = new ArrayList<>();
//            for(String para : param1Arr){
//                if(tempList.size() == 2){
//                    resultList.add(tempList);
//                    tempList = new ArrayList<>();
//                }
//                tempList.add(para);
//            }
//            if(tempList.size() > 0){
//                resultList.add(tempList);
//            }
//            bodyMap.put("templateParam",JSON.toJSONString(resultList));
//        }
//        //base64
//        String  body = Base64.getEncoder().encodeToString(JSON.toJSONString(bodyMap).getBytes());
//        String url = "http://172.16.30.22:8090/mms_model/send";
//        if(!ObjectUtils.isEmpty(param1)){
//            url = "http://172.16.30.22:8090/mms_model/variable/send";
//        }
//
//        logger.info("url="+url+"body="+body+"appid="+appid+"sign="+sign);
//        String result = HttpUtils.sendPost(url,"appid="+appid+"&timestamp="+timestamp+"&sign="+sign+"&body="+body+"");
//        logger.info(result);
//        return AjaxResult.success(JSON.parse(result));
//    }



}
