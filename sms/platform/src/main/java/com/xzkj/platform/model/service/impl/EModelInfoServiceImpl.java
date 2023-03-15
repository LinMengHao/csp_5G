package com.xzkj.platform.model.service.impl;

import com.xzkj.platform.common.core.domain.Ztree;
import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.ShiroUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.model.domain.EModelInfo;
import com.xzkj.platform.model.domain.ModelMaterial;
import com.xzkj.platform.model.mapper.EModelInfoMapper;
import com.xzkj.platform.model.mapper.ModelMaterialMapper;
import com.xzkj.platform.model.service.IEModelInfoService;
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
import java.util.*;


/**
 * 模板信息e_model_infoService业务层处理
 * 
 * @author linmenghao
 * @date 2023-02-06
 */
@Service
public class EModelInfoServiceImpl implements IEModelInfoService 
{
    @Autowired
    private EModelInfoMapper eModelInfoMapper;
    @Autowired
    private ITApplicationService tApplicationService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ModelMaterialMapper modelMaterialMapper;

    @Value("${model.filePath}")
    private String modelFilePath;
    @Value("${ruoyi.profile}")
    private String modelFilePaths;

    /**
     * 查询模板信息e_model_info
     * 
     * @param id 模板信息e_model_info主键
     * @return 模板信息e_model_info
     */
    @Override
    public EModelInfo selectEModelInfoById(Long id)
    {
        EModelInfo modelInfo = eModelInfoMapper.selectEModelInfoById(id);
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
     * @param eModelInfo 模板信息e_model_info
     * @return 模板信息e_model_info
     */
    @Override
    public List<EModelInfo> selectEModelInfoList(EModelInfo eModelInfo)
    {
        List<EModelInfo> list = eModelInfoMapper.selectEModelInfoList(eModelInfo);
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

        for (EModelInfo en:list){
            en.setAppName(mapApp.containsKey(en.getAppId())?mapApp.get(en.getAppId()):en.getAppId()+":");
            en.setCompanyName(mapDept.getOrDefault(en.getCompanyId(),""));
            en.setUserName(mapUser.containsKey(en.getUserId())?mapUser.get(en.getUserId()):en.getUserId()+"");
        }
        return list;
    }

    /**
     * 新增模板信息e_model_info
     * 
     * @param eModelInfo 模板信息e_model_info
     * @return 结果
     */
    @Override
    public int insertEModelInfo(EModelInfo eModelInfo)
    {
        TApplication app = tApplicationService.selectTApplicationById(eModelInfo.getAppId());
        eModelInfo.setBackUrl((app==null||app.getRptModelAddress()==null)?"":app.getRptModelAddress());
        //来源
        eModelInfo.setSource(2L);
        long maxId = getMaxId("e_model_info_new","id");
        //模板id
//        String modelId="M"+modelInfo.getVariate()+"2"+modelInfo.getAppId()+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        String modelId="M"+new SimpleDateFormat("MMdd").format(new Date())+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        eModelInfo.setModelId(modelId);
        eModelInfo.setStatus(3L);//新增-待审核状态
        eModelInfo.setInfo("待审核");
        // 获取当前的用户ID
        Long userId = ShiroUtils.getUserId();
        eModelInfo.setUserId(userId);
        eModelInfo.setUpdateTime(DateUtils.getNowDate());
        eModelInfo.setCreateTime(DateUtils.getNowDate());
        eModelInfo.setAppExt(Long.parseLong(app.getAppExt()));
        return eModelInfoMapper.insertEModelInfo(eModelInfo);
    }

    /**
     * 修改模板信息e_model_info
     * 
     * @param eModelInfo 模板信息e_model_info
     * @return 结果
     */
    @Override
    public int updateEModelInfo(EModelInfo eModelInfo)
    {
        eModelInfo.setUpdateTime(DateUtils.getNowDate());
        return eModelInfoMapper.updateEModelInfo(eModelInfo);
    }

    /**
     * 批量删除模板信息e_model_info
     * 
     * @param ids 需要删除的模板信息e_model_info主键
     * @return 结果
     */
    @Override
    public int deleteEModelInfoByIds(String ids)
    {
        return eModelInfoMapper.deleteEModelInfoByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除模板信息e_model_info信息
     * 
     * @param id 模板信息e_model_info主键
     * @return 结果
     */
    @Override
    public int deleteEModelInfoById(Long id)
    {
        return eModelInfoMapper.deleteEModelInfoById(id);
    }

    /**
     * 查询模板信息e_model_info树列表
     * 
     * @return 所有模板信息e_model_info信息
     */
    @Override
    public List<Ztree> selectEModelInfoTree()
    {
        List<EModelInfo> eModelInfoList = eModelInfoMapper.selectEModelInfoList(new EModelInfo());
        List<Ztree> ztrees = new ArrayList<Ztree>();
        for (EModelInfo eModelInfo : eModelInfoList)
        {
            Ztree ztree = new Ztree();
            ztree.setId(eModelInfo.getId());
            ztree.setpId(eModelInfo.getPid());
            ztree.setName(eModelInfo.getModelId());
            ztree.setTitle(eModelInfo.getModelId());
            ztrees.add(ztree);
        }
        return ztrees;
    }
    @Override
    public long getMaxId(String tableName, String field) {
        long max = 0;
        try {
            String key = RedisUtils.HASH_TABLE_MAX_INDEX;
            long object=RedisUtils.hash_incrBy(key,"model",1);
            if(object==1){
                int maxSize = eModelInfoMapper.selectMaxId(tableName,field);
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
    public int copyEModelInfo(EModelInfo modelInfo, List<ModelMaterial> modelMaterials) {
        //复制时，设置父id
        modelInfo.setPid(modelInfo.getId());
        modelInfo.setpModelId(modelInfo.getModelId());
        long maxId = getMaxId("e_model_info_new","id");
        //模板id
        String modelId="M"+new SimpleDateFormat("MMdd").format(new Date())+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        modelInfo.setModelId(modelId);
        TApplication app = tApplicationService.selectTApplicationById(modelInfo.getAppId());
        modelInfo.setBackUrl((app==null||app.getRptModelAddress()==null)?"":app.getRptModelAddress());

        modelInfo.setSource(2L);
        modelInfo.setStatus(3L);//新增-待审核状态
        modelInfo.setInfo("待审核");
        // 获取当前的用户ID
        Long userId = ShiroUtils.getUserId();
        modelInfo.setUserId(userId);
        modelInfo.setIdea(null);
        modelInfo.setToInternational(0L);
        modelInfo.setToUnicom(0L);
        modelInfo.setToTelecom(0L);
        modelInfo.setToCmcc(0L);
        modelInfo.setUpdateTime(DateUtils.getNowDate());
        modelInfo.setCreateTime(DateUtils.getNowDate());
        int i= eModelInfoMapper.insertEModelInfo(modelInfo);

        //新存一次文件素材信息
        for (ModelMaterial modelMaterial:modelMaterials) {
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
                    Files.copy(file.toPath(),new FileOutputStream(file1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //保存媒体文件后，更改文件路径和content字段
                modelMaterial.setContent(content);
                modelMaterial.setFilePath(fileAllPaths);
            }
        }
        int i1 = modelMaterialMapper.insertModelMaterials(modelMaterials);
        if(i>0&&i1>0){
            return i;
        }
        return 0;
    }

    @Override
    public int updateModelInfoByModelId(EModelInfo modelInfo) {
        return eModelInfoMapper.updateModelInfoByModelId(modelInfo);
    }

    @Override
    public EModelInfo selectEModelInfoByModelId(String s) {
        return eModelInfoMapper.selectEModelInfoByModelId(s);
    }

    @Override
    public List<EModelInfo> selectPEModelInfoList(EModelInfo eModelInfo) {
        return eModelInfoMapper.selectPEModelInfoList(eModelInfo);
    }

    @Override
    public List<EModelInfo> selectSonModelInfoList(EModelInfo info) {
        return eModelInfoMapper.selectSonModelInfoList(info);
    }

    @Override
    public List<EModelInfo> selectByPModelId(String modelId) {
        return eModelInfoMapper.selectByPModelId(modelId);
    }
}
