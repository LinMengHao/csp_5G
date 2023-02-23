package com.xzkj.platform.sign.service.impl;


import com.xzkj.platform.common.config.ServerConfig;
import com.xzkj.platform.common.core.domain.Ztree;
import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.PageUtils;
import com.xzkj.platform.common.utils.ShiroUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.redis.RedisUtils;
import com.xzkj.platform.sign.domain.EModelSign;
import com.xzkj.platform.sign.mapper.EModelSignMapper;
import com.xzkj.platform.sign.service.IEModelSignService;
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
 * 签名Service业务层处理
 * 
 * @author linmenghao
 * @date 2023-01-31
 */
@Service
public class EModelSignServiceImpl implements IEModelSignService
{

    @Value("${sign.filePath}")
    private String signFilePath;
    @Value("${ruoyi.profile}")
    private String profile;

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private EModelSignMapper eModelSignMapper;

    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private ITApplicationService tApplicationService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询签名
     * 
     * @param id 签名主键
     * @return 签名
     */
    @Override
    public EModelSign selectEModelSignById(Long id)
    {
        EModelSign eModelSign = eModelSignMapper.selectEModelSignById(id);
        SysDept dept = deptService.selectDeptById(eModelSign.getCompanyId());
        TApplication app = tApplicationService.selectTApplicationById(eModelSign.getAppId());
        SysUser user = sysUserService.selectUserById(eModelSign.getUserId());
        eModelSign.setCompanyName(dept!=null?dept.getCompanyName():"未知");
        eModelSign.setAppName(app!=null?app.getAppName():eModelSign.getAppId()+"");
        return eModelSign;
    }

    /**
     * 查询签名列表
     * 
     * @param eModelSign 签名
     * @return 签名
     */
    @Override
    public List<EModelSign> selectEModelSignList(EModelSign eModelSign)
    {
        List<EModelSign> list = eModelSignMapper.selectEModelSignList(eModelSign);
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

        for (EModelSign en:list){
            en.setAppName(mapApp.containsKey(en.getAppId())?mapApp.get(en.getAppId()):en.getAppId()+":");
            en.setCompanyName(mapDept.getOrDefault(en.getCompanyId(),""));
            en.setUserName(mapUser.containsKey(en.getUserId())?mapUser.get(en.getUserId()):en.getUserId()+"");
        }

        return list;
    }

    /**
     * 新增签名
     * 
     * @param eModelSign 签名
     * @return 结果
     */
    @Override
    public int insertEModelSign(EModelSign eModelSign)
    {
        //账号下签名回调地址，暂时还未完善
        TApplication app = tApplicationService.selectTApplicationById(eModelSign.getAppId());
        eModelSign.setBackUrl((app==null||app.getRptSignAddress()==null)?"":app.getRptSignAddress());
        eModelSign.setAppName((app==null||app.getAppName()==null)?"":app.getAppName());
        //来源
        eModelSign.setSource(2L);
        long maxId = getMaxId("e_model_sign","id");
        //模板id
        String signId="S"+new SimpleDateFormat("MMdd").format(new Date())+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        eModelSign.setSignId(signId);
        eModelSign.setStatus(3L);//新增-待审核状态
        eModelSign.setInfo("待审核");
        // 获取当前的用户ID
        Long userId = ShiroUtils.getUserId();
        eModelSign.setUserId(userId);
        eModelSign.setUpdateTime(DateUtils.getNowDate());
        eModelSign.setCreateTime(DateUtils.getNowDate());
        return eModelSignMapper.insertEModelSign(eModelSign);

    }

    /**
     * 修改签名
     * 
     * @param eModelSign 签名
     * @return 结果
     */
    @Override
    public int updateEModelSign(EModelSign eModelSign)
    {
        eModelSign.setUpdateTime(DateUtils.getNowDate());
        return eModelSignMapper.updateEModelSign(eModelSign);
    }

    /**
     * 批量删除签名
     * 
     * @param ids 需要删除的签名主键
     * @return 结果
     */
    @Override
    public int deleteEModelSignByIds(String ids)
    {
        return eModelSignMapper.deleteEModelSignByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除签名信息
     * 
     * @param id 签名主键
     * @return 结果
     */
    @Override
    public int deleteEModelSignById(Long id)
    {
        return eModelSignMapper.deleteEModelSignById(id);
    }

    /**
     * 查询签名树列表
     * 
     * @return 所有签名信息
     */
    @Override
    public List<Ztree> selectEModelSignTree()
    {
        List<EModelSign> eModelSignList = eModelSignMapper.selectEModelSignList(new EModelSign());
        List<Ztree> ztrees = new ArrayList<Ztree>();
        for (EModelSign eModelSign : eModelSignList)
        {
            Ztree ztree = new Ztree();
            ztree.setId(eModelSign.getId());
            ztree.setpId(eModelSign.getPid());
            ztree.setName(eModelSign.getSignId());
            ztree.setTitle(eModelSign.getSignId());
            ztrees.add(ztree);
        }
        return ztrees;
    }

    @Override
    public long getMaxId(String tableName, String field) {
        long max = 0;
        try {
            String key = RedisUtils.HASH_TABLE_MAX_INDEX;
            long object=RedisUtils.hash_incrBy(key,"sign",1);
            if(object==1){
                int maxSize = eModelSignMapper.selectMaxId(tableName,field);
                if(maxSize!=0){
                    RedisUtils.hash_incrBy(key,"sign",maxSize);
                    object+=maxSize;
                }
            }
            max = object + 1000;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取e_model_sign主键最大id失败");
        }
        return max;
    }

    @Override
    public int copyEModelSign(EModelSign eModelSign) {
        Long id = eModelSign.getId();
        String path = eModelSign.getFilepath();
        String psignId = eModelSign.getSignId();
        //账号下签名回调地址，暂时还未完善
        TApplication app = tApplicationService.selectTApplicationById(eModelSign.getAppId());
//        eModelSign.setBackUrl((app==null||app.getRptModelAddress()==null)?"":app.getRptModelAddress());
        eModelSign.setAppName((app==null||app.getAppName()==null)?"":app.getAppName());
        //来源
        eModelSign.setSource(2L);
        long maxId = getMaxId("e_model_sign","id");
        String signId="S"+new SimpleDateFormat("MMdd").format(new Date())+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);

        eModelSign.setpSignId(psignId);
        eModelSign.setSignId(signId);
        eModelSign.setPid(id);
        eModelSign.setStatus(3L);//新增-待审核状态
        eModelSign.setInfo("待审核");
        // 获取当前的用户ID
        Long userId = ShiroUtils.getUserId();
        eModelSign.setUserId(userId);
        eModelSign.setIdea(null);
        eModelSign.setToInternational(0L);
        eModelSign.setToUnicom(0L);
        eModelSign.setToTelecom(0L);
        eModelSign.setToCmcc(0L);
        eModelSign.setUpdateTime(DateUtils.getNowDate());
        eModelSign.setCreateTime(DateUtils.getNowDate());
        eModelSign.setId(null);
        //新存一份素材
        String[] split = path.split("\\.");
        String filePath=profile+signFilePath;
        String fileName= "/"+ DateFormatUtils.format(new Date(), "yyyyMMdd")+"/F"+(new Date()).getTime()+"."+split[split.length-1];
        String fileAllPaths = filePath+fileName;
        File file=new File(path);
        File file1 = new File(fileAllPaths);
        try {
            if(!file1.getParentFile().exists()){
                file1.getParentFile().mkdirs();
                file1.createNewFile();
            }
            Files.copy(file.toPath(),new BufferedOutputStream(new FileOutputStream(file1)));
            eModelSign.setUploadFile(serverConfig.getUrl()+"/profile/"+signFilePath+fileName);
            eModelSign.setFilepath(fileAllPaths);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return eModelSignMapper.insertEModelSign(eModelSign);
    }

    @Override
    public List<EModelSign> selectPEModelSignList(EModelSign eModelSign) {
        List<EModelSign> list = eModelSignMapper.selectPEModelSignList(eModelSign);
        return list;
    }

    @Override
    public EModelSign selectEModelSignBySignId(String s) {
        return eModelSignMapper.selectEModelSignBySignId(s);
    }

    @Override
    public List<EModelSign> selectSignByAppId(EModelSign sign) {
        return eModelSignMapper.selectSignByAppId(sign);
    }
}
