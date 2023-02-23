package com.xzkj.platform.model.service.impl;

import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.PageUtils;
import com.xzkj.platform.common.utils.ShiroUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.model.domain.ModelInfo;
import com.xzkj.platform.model.domain.ModelMaterial;
import com.xzkj.platform.model.mapper.ModelInfoMapper;
import com.xzkj.platform.model.mapper.ModelMaterialMapper;
import com.xzkj.platform.model.service.IModelInfoService;
import com.xzkj.platform.redis.RedisUtils;
import com.xzkj.platform.system.service.ISysDeptService;
import com.xzkj.platform.system.service.ISysUserService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板信息e_model_infoService业务层处理
 * 
 * @author ruoyi
 * @date 2022-11-24
 */
@Service
public class ModelInfoServiceImpl implements IModelInfoService
{
    @Value("${model.filePath}")
    private String modelFilePath;
    @Value("${ruoyi.profile}")
    private String modelFilePaths;
    @Autowired
    private ModelInfoMapper modelInfoMapper;
    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private ITApplicationService tApplicationService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ModelMaterialMapper modelMaterialMapper;

    /**
     * 查询模板信息e_model_info
     * 
     * @param id 模板信息e_model_info主键
     * @return 模板信息e_model_info
     */
    @Override
    public ModelInfo selectModelInfoById(Long id){
        ModelInfo modelInfo = modelInfoMapper.selectModelInfoById(id);
        SysDept dept = deptService.selectDeptById(modelInfo.getCompanyId());
        TApplication app = tApplicationService.selectTApplicationById(modelInfo.getAppId());
        SysUser user = sysUserService.selectUserById(modelInfo.getUserId());
        modelInfo.setCompanyName(dept!=null?dept.getCompanyName():"未知");
        modelInfo.setAppName(app!=null?app.getAppName():modelInfo.getAppId()+"");
        //账号拓展码
        modelInfo.setAppExt(app!=null?Long.parseLong(app.getAppExt()):1l);
        modelInfo.setUserName(user!=null?user.getUserName():modelInfo.getUserId()+"");

        return modelInfo;
    }

    /**
     * 查询模板信息e_model_info列表
     * 
     * @param modelInfo 模板信息e_model_info
     * @return 模板信息e_model_info
     */
    @Override
    public List<ModelInfo> selectModelInfoList(ModelInfo modelInfo)
    {
        List<ModelInfo> list = modelInfoMapper.selectModelInfoList(modelInfo);
        PageUtils.clearPage();

        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Map<Long,String> mapDept = new HashMap<Long,String>();
        for (SysDept dept:deptList){
            mapDept.put(dept.getCompanyId(),dept.getCompanyName());
        }

        List<TApplication> applist = tApplicationService.selectTApplicationListN(0L);
        Map<Long,String> mapApp = new HashMap<Long,String>();
        for (TApplication app:applist){
            mapApp.put(app.getId(),app.getId()+":"+app.getAppName());
        }

        List<SysUser> userList = sysUserService.selectUserList(new SysUser());
        Map<Long,String> mapUser = new HashMap<Long,String>();
        for (SysUser user:userList){
            mapUser.put(user.getUserId(),user.getUserName());
        }

        for (ModelInfo en:list){
            en.setAppName(mapApp.containsKey(en.getAppId())?mapApp.get(en.getAppId()):en.getAppId()+":");
            en.setCompanyName(mapDept.getOrDefault(en.getCompanyId(),""));
            en.setUserName(mapUser.containsKey(en.getUserId())?mapUser.get(en.getUserId()):en.getUserId()+"");
        }

        return list;
    }

    /**
     * 新增模板信息e_model_info
     * 
     * @param modelInfo 模板信息e_model_info
     * @return 结果
     */
    @Override
    public int insertModelInfo(ModelInfo modelInfo)
    {
        TApplication app = tApplicationService.selectTApplicationById(modelInfo.getAppId());
        modelInfo.setBackUrl((app==null||app.getRptModelAddress()==null)?"":app.getRptModelAddress());
        //来源
        modelInfo.setSource(2L);
        long maxId = getMaxId("e_model_info","id");
        //模板id
//        String modelId="M"+modelInfo.getVariate()+"2"+modelInfo.getAppId()+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        String modelId="M"+new SimpleDateFormat("yyyyMMdd").format(new Date())+modelInfo.getVariate()+"2"+modelInfo.getAppId()+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        modelInfo.setModelId(modelId);
        modelInfo.setStatus(3L);//新增-待审核状态
        modelInfo.setInfo("待审核");
        // 获取当前的用户ID
        Long userId = ShiroUtils.getUserId();
        modelInfo.setUserId(userId);
        modelInfo.setUpdateTime(DateUtils.getNowDate());
        modelInfo.setCreateTime(DateUtils.getNowDate());
        return modelInfoMapper.insertModelInfo(modelInfo);
    }

    /**
     * 修改模板信息e_model_info
     * 
     * @param modelInfo 模板信息e_model_info
     * @return 结果
     */
    @Override
    public int updateModelInfo(ModelInfo modelInfo)
    {
//        String modelId="M"+modelInfo.getVariate()+modelInfo.getSource()+modelInfo.getAppId();
//        String suffix = modelInfo.getModelId().substring(modelInfo.getModelId().length()-4);
//        modelInfo.setModelId(modelId+suffix);
        modelInfo.setUpdateTime(DateUtils.getNowDate());
        return modelInfoMapper.updateModelInfo(modelInfo);
    }

    /**
     * 批量删除模板信息e_model_info
     * 
     * @param ids 需要删除的模板信息e_model_info主键
     * @return 结果
     */
    @Override
    public int deleteModelInfoByIds(String ids)
    {
        return modelInfoMapper.deleteModelInfoByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除模板信息e_model_info信息
     * 
     * @param id 模板信息e_model_info主键
     * @return 结果
     */
    @Override
    public int deleteModelInfoById(Long id)
    {
        return modelInfoMapper.deleteModelInfoById(id);
    }

    @Override
    public long getMaxId(String tableName, String field) {
        long max = 0;
        try {
            String key = RedisUtils.HASH_TABLE_MAX_INDEX;
            long object=RedisUtils.hash_incrBy(key,"model",1);
            if(object==1){
                int maxSize = modelInfoMapper.selectMaxId(tableName,field);
                if(maxSize!=0){
                    RedisUtils.hash_incrBy(key,"model",maxSize);
                    object+=maxSize;
                }
            }
            max = object + 1000;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取e_model_info主键最大id失败");
        }
        return max;
    }

    @Override
    public int updateModelInfoByModelId(ModelInfo info) {
        return modelInfoMapper.updateModelInfoByModelId(info);
    }

    @Override
    public int copyModelInfo(ModelInfo modelInfo,List<ModelMaterial> materials) {
        /*
            id=1
            companyId=100
            appId=3
            signId=1
            modelId=M1233424
            title=测试1
            variate=1
            status=3   x
            info=待审核  x
            source=2   x
            backUrl=
            userId=1   x
            updateTime=Fri Dec 09 12:34:24 CST 2022  x
            createTime=Fri Dec 09 12:34:24 CST 2022  x
            modelExt=1  x
            appExt=106102  x
            channelId=5  x
         */

        //新存一次模版信息
        ModelInfo modelInfo1=new ModelInfo();
        modelInfo1.setCompanyId(modelInfo.getCompanyId());
        modelInfo1.setAppId(modelInfo.getAppId());
        modelInfo1.setTitle(modelInfo.getTitle());
        modelInfo1.setVariate(modelInfo.getVariate());
        modelInfo1.setBackUrl(modelInfo.getBackUrl()!=null?modelInfo.getBackUrl():"");
        //复制时，设置父id
        modelInfo.setPid(modelInfo.getModelId());
        long maxId = getMaxId("e_model_info","id");
        //模板id
//        String modelId="M"+modelInfo.getVariate()+"2"+modelInfo.getAppId()+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        String modelId="M"+new SimpleDateFormat("yyyyMMdd").format(new Date())+modelInfo.getVariate()+"2"+modelInfo.getAppId()+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        modelInfo.setModelId(modelId);
        TApplication app = tApplicationService.selectTApplicationById(modelInfo.getAppId());
        modelInfo.setBackUrl((app==null||app.getRptModelAddress()==null)?"":app.getRptModelAddress());

        modelInfo.setSource(2L);
        modelInfo.setStatus(3L);//新增-待审核状态
        modelInfo.setInfo("待审核");
        // 获取当前的用户ID
        Long userId = ShiroUtils.getUserId();
        modelInfo.setUserId(userId);
        modelInfo.setUpdateTime(DateUtils.getNowDate());
        modelInfo.setCreateTime(DateUtils.getNowDate());
        int i= modelInfoMapper.insertModelInfo(modelInfo);

        //新存一次文件素材信息
        for (ModelMaterial modelMaterial:materials) {
            modelMaterial.setId(null);
            modelMaterial.setModelId(modelId);
            modelMaterial.setUserId(userId);
            modelMaterial.setUpdateTime(DateUtils.getNowDate());
            modelMaterial.setCreateTime(DateUtils.getNowDate());
            if(!"txt".equals(modelMaterial.getExt())){
                String fileAllPaths=null;
                String content=null;
                //媒体文件需要新存一份
                String filePath=modelFilePaths+modelFilePath;
                String fileName= "/"+ DateFormatUtils.format(new Date(), "yyyyMMdd")+"/F"+(new Date()).getTime()+modelMaterial.getMediaType();
                fileAllPaths = filePath+fileName+"."+modelMaterial.getExt();
                content="/profile/"+modelFilePath+fileName+"."+modelMaterial.getExt();
                String filePath1 = modelMaterial.getFilePath();
                File file=new File(filePath1);
                File file1 = new File(fileAllPaths);
                try {
                    if(!file1.getParentFile().exists()){
                        file1.getParentFile().mkdirs();
                        file1.createNewFile();
                    }
                    Files.copy(file.toPath(),new BufferedOutputStream(new FileOutputStream(file1)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //保存媒体文件后，更改文件路径和content字段
                modelMaterial.setContent(content);
                modelMaterial.setFilePath(fileAllPaths);
            }
        }
        int i1 = modelMaterialMapper.insertModelMaterials(materials);
        if(i>0&&i1>0){
            return i;
        }
        return 0;
    }

    @Override
    public ModelInfo selectModelInfoByModelId(String modelId) {
        ModelInfo modelInfo=modelInfoMapper.selectModelInfoByModelId(modelId);
        return modelInfo;
    }



}
