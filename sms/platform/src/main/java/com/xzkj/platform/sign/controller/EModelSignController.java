package com.xzkj.platform.sign.controller;

import com.alibaba.fastjson.JSONObject;
import com.xzkj.platform.common.annotation.Log;
import com.xzkj.platform.common.config.RuoYiConfig;
import com.xzkj.platform.common.config.ServerConfig;
import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.common.core.domain.AjaxResult;
import com.xzkj.platform.common.core.domain.Ztree;
import com.xzkj.platform.common.enums.BusinessType;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.common.utils.file.FileUploadUtils;
import com.xzkj.platform.common.utils.file.FileUtils;
import com.xzkj.platform.common.utils.poi.ExcelUtil;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.redis.RedisUtils;
import com.xzkj.platform.sign.domain.EModelSign;
import com.xzkj.platform.sign.service.IEModelSignService;
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
import java.util.Arrays;
import java.util.List;


/**
 * 签名Controller
 * 
 * @author linmenghao
 * @date 2023-01-31
 */
@Controller
@RequestMapping("/emodel/sign")
public class EModelSignController extends BaseController
{
    private String prefix = "emodel/sign";

    @Value("${sign.filePath}")
    private String signFilePath;
    @Value("${ruoyi.profile}")
    private String profile;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private IChannelService channelService;
    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private IEModelSignService eModelSignService;
    @Autowired
    private ITApplicationService tApplicationService;


    @RequiresPermissions("emodel:sign:view")
    @GetMapping()
    public String sign()
    {
        return prefix + "/sign";
    }

    /**
     * 查询签名树列表
     */
    @RequiresPermissions("emodel:sign:list")
    @PostMapping("/list")
    @ResponseBody
    public List<EModelSign> list(EModelSign eModelSign)
    {

        List<EModelSign> list = eModelSignService.selectEModelSignList(eModelSign);
        return list;
    }


    /**
     * 导出签名列表
     */
    @RequiresPermissions("emodel:sign:export")
    @Log(title = "签名", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(EModelSign eModelSign)
    {
        List<EModelSign> list = eModelSignService.selectEModelSignList(eModelSign);
        ExcelUtil<EModelSign> util = new ExcelUtil<EModelSign>(EModelSign.class);
        return util.exportExcel(list, "签名数据");
    }

    /**
     * 新增签名
     */
    @GetMapping(value = { "/add/{id}", "/add/" })
    public String add(@PathVariable(value = "id", required = false) Long id, ModelMap mmap)
    {
        if (StringUtils.isNotNull(id))
        {
            mmap.put("eModelSign", eModelSignService.selectEModelSignById(id));
        }
        return prefix + "/add";
    }


    /**
     * 新增保存签名
     */
    @RequiresPermissions("emodel:sign:add")
    @Log(title = "签名", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(EModelSign eModelSign)
    {
        /*
        companyId: 104 companyName: 单元科技 appId: 6 pid:  ecProvince: 222 ecCity: 222 serviceCode: 222 ecName: 1
        backUrl: 1 customerType: 0 uploadFile:  reportSignContent: ooo industry:  1 rcsIndustry: 1
         */
        String filePath = RuoYiConfig.getProfile()+signFilePath;
        // 上传并返回新文件名称 例如：http://localhost:9100/profile/signFile/20230202/1sucai_20230202113155A001.jpg
        String uploadFile = eModelSign.getUploadFile();
        String[] profiles = uploadFile.trim().split("/profile/");
        String filepath=profile+profiles[profiles.length-1];
        eModelSign.setFilepath(filepath);
//        String path = fileName.replace("/profile/", profile);
        eModelSign.setOperatorType(1l);
        String rcsIndustry = eModelSign.getRcsIndustry();
        String[] split = rcsIndustry.split(",");
        if(split.length<1||split.length>5){
            return AjaxResult.error("行业属性错误（最少一个，最多五个）");
        }
        return toAjax(eModelSignService.insertEModelSign(eModelSign));
    }

    /**
     * 修改签名
     */
    @RequiresPermissions("emodel:sign:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        EModelSign eModelSign = eModelSignService.selectEModelSignById(id);
        String industry = eModelSign.getIndustry();
        String rcsIndustry = eModelSign.getRcsIndustry();
        List<String> industryList = Arrays.asList(industry.split(","));
        List<String> rcsIndustryList = Arrays.asList(rcsIndustry.split(","));
        mmap.put("industryList",industryList);
        mmap.put("rcsIndustryList",rcsIndustryList);
        mmap.put("eModelSign", eModelSign);
        return prefix + "/edit";
    }

    /**
     * 签名审核详情
     */
    @RequiresPermissions("emodel:sign:check")
    @GetMapping("/check/{id}")
    public String check(@PathVariable("id") Long id, ModelMap mmap)
    {
        EModelSign eModelSign = eModelSignService.selectEModelSignById(id);
        //素材回显
        String industry = eModelSign.getIndustry();
        String rcsIndustry = eModelSign.getRcsIndustry();
        List<String> industryList = Arrays.asList(industry.split(","));
        List<String> rcsIndustryList = Arrays.asList(rcsIndustry.split(","));

        List<Channel> channellist = channelService.selectChannelListAll(0L);
        mmap.put("channellist", channellist);

        mmap.put("industryList",industryList);
        mmap.put("rcsIndustryList",rcsIndustryList);
        mmap.put("eModelSign", eModelSign);
        return prefix + "/check";
    }

    /**
     * 修改保存签名
     */
    @RequiresPermissions("emodel:sign:edit")
    @Log(title = "签名", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(EModelSign eModelSign)
    {
        // 上传并返回新文件名称 例如：http://localhost:9100/profile/signFile/20230202/1sucai_20230202113155A001.jpg
        String uploadFile = eModelSign.getUploadFile();
        String[] profiles = uploadFile.trim().split("/profile/");
        String filepath=profile+profiles[profiles.length-1];
        eModelSign.setFilepath(filepath);
        String rcsIndustry = eModelSign.getRcsIndustry();
        String[] split = rcsIndustry.split(",");
        if(split.length<1||split.length>5){
            return AjaxResult.error("行业属性错误（最少一个，最多五个）");
        }
        return toAjax(eModelSignService.updateEModelSign(eModelSign));
    }

    /**
     * 删除
     */
    @RequiresPermissions("emodel:sign:remove")
    @Log(title = "签名", businessType = BusinessType.DELETE)
    @GetMapping("/remove/{id}")
    @ResponseBody
    public AjaxResult remove(@PathVariable("id") Long id)
    {
        return toAjax(eModelSignService.deleteEModelSignById(id));
    }

    /**
     * 复制签名信息e_model_sign
     */
    @RequiresPermissions("emodel:sign:add")
    @PostMapping("/copy")
    @ResponseBody
    public AjaxResult copy(String id)
    {
        System.out.println("id: "+id);
        long l = Long.parseLong(id.trim());
        //获取源文件基本信息
        EModelSign eModelSign = eModelSignService.selectEModelSignById(l);
        System.out.println("copy: "+eModelSign.toString());


        int result=eModelSignService.copyEModelSign(eModelSign);


        return toAjax(result);
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
        EModelSign eModelSign = eModelSignService.selectEModelSignById(l);
        //获取真子集
        EModelSign eModelSign1 = new EModelSign();
        eModelSign1.setPid(l);
        List<EModelSign> list=eModelSignService.selectEModelSignList(eModelSign1);

        mmap.put("eModelSign",eModelSign);
        String idea = eModelSign.getIdea();
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
        mmap.put("eModelSign",eModelSign);

        return prefix+"/idea";
    }

    @RequiresPermissions("emodel:sign:edit")
    @Log(title = "签名", businessType = BusinessType.UPDATE)
    @PostMapping("/idea")
    @ResponseBody
    public AjaxResult ideaSave(EModelSign eModelSign)
    {
        return toAjax(eModelSignService.updateEModelSign(eModelSign));
    }


    /**
     * TODO
     * 编辑策略
     */
    @GetMapping("/signView/{appId}")
    public String signView(@PathVariable("appId")String appId, ModelMap mmap)
    {
        System.out.println("appId: "+appId);
        long l = Long.parseLong(appId.trim());
        EModelSign sign=new EModelSign();
        sign.setAppId(l);
        List<EModelSign> list=eModelSignService.selectEModelSignList(sign);
        mmap.put("list",list);
        return prefix+"/signView";
    }


    @GetMapping("/checkSuccess/{id}")
    public String checkSuccess(@PathVariable("id") Long id,ModelMap mmap){
        //获取源文件基本信息
        EModelSign eModelSign = eModelSignService.selectEModelSignById(id);
        mmap.put("eModelSign",eModelSign);
        return prefix+"/checkSuccess";
    }

    @RequiresPermissions("emodel:sign:edit")
    @Log(title = "签名", businessType = BusinessType.UPDATE)
    @PostMapping("/checkSuccess")
    @ResponseBody
    public AjaxResult checkSuccessSave(EModelSign eModelSign)
    {
        Long companyId = eModelSign.getCompanyId();
        eModelSign.setStatus(1L);
        int i=eModelSignService.updateEModelSign(eModelSign);
        EModelSign sign = eModelSignService.selectEModelSignById(eModelSign.getId());
        String info=eModelSign.getInfo();
        if(StringUtils.isBlank(info)){
            info="审核成功";
        }
        sign.setStatus(1L);
        sign.setToCmcc(eModelSign.getToCmcc());
        sign.setToUnicom(eModelSign.getToUnicom());
        sign.setToTelecom(eModelSign.getToTelecom());
        sign.setToInternational(eModelSign.getToInternational());
        sign.setInfo(info);
        String s = JSONObject.toJSONString(sign);
        RedisUtils.fifo_push(RedisUtils.FIFO_SIGN_MT_CLIENT+companyId,s);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SIGN_MT_COUNT, companyId+"", 1);

        return toAjax(i);
    }


    @GetMapping("/checkFailed/{id}")
    public String checkFailed(@PathVariable("id") Long id,ModelMap mmap){
        //获取源文件基本信息
        EModelSign eModelSign = eModelSignService.selectEModelSignById(id);
        mmap.put("eModelSign",eModelSign);
        return prefix+"/checkFailed";
    }

    @RequiresPermissions("emodel:sign:edit")
    @Log(title = "签名", businessType = BusinessType.UPDATE)
    @PostMapping("/checkFailed")
    @ResponseBody
    public AjaxResult checkFailedSave(EModelSign eModelSign)
    {
        Long companyId = eModelSign.getCompanyId();
        eModelSign.setStatus(2L);
        int i=eModelSignService.updateEModelSign(eModelSign);
        EModelSign sign = eModelSignService.selectEModelSignById(eModelSign.getId());
        String info=eModelSign.getInfo();
        if(StringUtils.isBlank(info)){
            info="审核失败";
        }
        sign.setStatus(2L);
        sign.setToCmcc(eModelSign.getToCmcc());
        sign.setToUnicom(eModelSign.getToUnicom());
        sign.setToTelecom(eModelSign.getToTelecom());
        sign.setToInternational(eModelSign.getToInternational());
        sign.setInfo(info);
        String s = JSONObject.toJSONString(sign);
        RedisUtils.fifo_push(RedisUtils.FIFO_SIGN_MT_CLIENT+companyId,s);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SIGN_MT_COUNT, companyId+"", 1);

        return toAjax(i);
    }



    /**
     * 签名提交通道审核
     * @param eModelSign
     * @param request
     * @return
     */
    @PostMapping("/submitToChannel")
    @ResponseBody
    public AjaxResult submitToChannel(EModelSign eModelSign, HttpServletRequest request){
        int i = eModelSignService.updateEModelSign(eModelSign);
        eModelSign.setOperatorType(1l);
        System.out.println("EModelSign "+eModelSign.toString());
        HttpHeaders headers=new HttpHeaders();
        headers.set("Content-Type","application/json");
        String s = JSONObject.toJSONString(eModelSign);
        JSONObject jsonObject = JSONObject.parseObject(s);
        System.out.println("jsonObject: "+jsonObject.toString());
        HttpEntity entity=new HttpEntity(jsonObject,headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:19131/mms/sign/submitToChannel", entity, String.class);
        //TODO 将模版提交到运营商
        return toAjax(1);
    }



    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    @ResponseBody
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getProfile()+signFilePath;
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            String path = fileName.replace("/profile/", profile);
//            ajax.put("url", url);
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            System.out.println("fileName: "+fileName);
            System.out.println("path: "+path);
            System.out.println("newFileName: "+FileUtils.getName(fileName));
            System.out.println("originalFilename: "+file.getOriginalFilename());
            System.out.println("url: "+url);
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 选择签名树
     */
    @GetMapping(value = { "/selectSignTree/{id}", "/selectSignTree/" })
    public String selectSignTree(@PathVariable(value = "id", required = false) Long id, ModelMap mmap)
    {
        if (StringUtils.isNotNull(id))
        {
            mmap.put("eModelSign", eModelSignService.selectEModelSignById(id));
        }
        return prefix + "/tree";
    }

    /**
     * 加载签名树列表
     */
    @GetMapping("/treeData")
    @ResponseBody
    public List<Ztree> treeData()
    {
        List<Ztree> ztrees = eModelSignService.selectEModelSignTree();
        return ztrees;
    }

    @GetMapping("/selectSignByAppId/{appId}")
    @ResponseBody
    public AjaxResult selectSignByAppId(@PathVariable(value = "appId") Long appId){
        EModelSign sign=new EModelSign();
        sign.setAppId(appId);
        sign.setStatus(1l);
//        sign.setStatus(5l);
        List<EModelSign> applist = eModelSignService.selectSignByAppId(sign);
        return success(applist);

    }

}
