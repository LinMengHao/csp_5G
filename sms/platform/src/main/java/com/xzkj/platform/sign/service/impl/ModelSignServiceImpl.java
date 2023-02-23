package com.xzkj.platform.sign.service.impl;

import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.utils.PageUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.sign.domain.ModelSign;
import com.xzkj.platform.sign.mapper.ModelSignMapper;
import com.xzkj.platform.sign.service.IModelSignService;
import com.xzkj.platform.system.service.ISysDeptService;
import com.xzkj.platform.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelSignServiceImpl implements IModelSignService {
    @Value("${model.filePath}")
    private String modelFilePath;
    @Value("${ruoyi.profile}")
    private String modelFilePaths;

    @Autowired
    private ModelSignMapper modelSignMapper;

    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private ITApplicationService tApplicationService;
    @Autowired
    private ISysUserService sysUserService;




    @Override
    public List<ModelSign> selectModelSignList(ModelSign modelSign) {
        List<ModelSign> list = modelSignMapper.selectModelSignList(modelSign);
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

        for (ModelSign en:list){
            en.setAppName(mapApp.containsKey(en.getAppId())?mapApp.get(en.getAppId()):en.getAppId()+":");
            en.setCompanyName(mapDept.getOrDefault(en.getCompanyId(),""));
            en.setUserName(mapUser.containsKey(en.getUserId())?mapUser.get(en.getUserId()):en.getUserId()+"");
        }

        return list;
    }
}
