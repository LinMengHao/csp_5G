package com.xzkj.platform.system.service.impl;

import com.xzkj.platform.common.annotation.DataScope;
import com.xzkj.platform.common.constant.UserConstants;
import com.xzkj.platform.common.core.domain.Ztree;
import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.domain.entity.SysRole;
import com.xzkj.platform.common.core.domain.entity.SysUser;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.exception.ServiceException;
import com.xzkj.platform.common.utils.ShiroUtils;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.common.utils.spring.SpringUtils;
import com.xzkj.platform.system.mapper.SysDeptMapper;
import com.xzkj.platform.system.service.ISysDeptService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 部门管理 服务实现
 * 
 * @author ruoyi
 */
@Service
public class SysDeptServiceImpl implements ISysDeptService
{
    @Autowired
    private SysDeptMapper deptMapper;

    /**
     * 查询部门管理数据
     * 
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept)
    {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门管理树
     * 
     * @param dept 部门信息
     * @return 所有部门信息
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<Ztree> selectDeptTree(SysDept dept)
    {
        List<SysDept> deptList = deptMapper.selectDeptList(dept);
        List<Ztree> ztrees = initZtree(deptList);
        return ztrees;
    }

    /**
     * 查询部门管理树（排除下级）
     * 
     * @param dept 部门ID
     * @return 所有部门信息
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<Ztree> selectDeptTreeExcludeChild(SysDept dept)
    {
        Long excludeId = dept.getExcludeId();
        List<SysDept> deptList = deptMapper.selectDeptList(dept);
        Iterator<SysDept> it = deptList.iterator();
        while (it.hasNext())
        {
            SysDept d = (SysDept) it.next();
            if (d.getCompanyId().intValue() == excludeId
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), excludeId + ""))
            {
                it.remove();
            }
        }
        List<Ztree> ztrees = initZtree(deptList);
        return ztrees;
    }

    /**
     * 根据角色ID查询部门（数据权限）
     *
     * @param role 角色对象
     * @return 部门列表（数据权限）
     */
    @Override
    public List<Ztree> roleDeptTreeData(SysRole role)
    {
        Long roleId = role.getRoleId();
        List<Ztree> ztrees = new ArrayList<Ztree>();
        List<SysDept> deptList = selectDeptList(new SysDept());
        if (StringUtils.isNotNull(roleId))
        {
            List<String> roleDeptList = deptMapper.selectRoleDeptTree(roleId);
            ztrees = initZtree(deptList, roleDeptList);
        }
        else
        {
            ztrees = initZtree(deptList);
        }
        return ztrees;
    }

    /**
     * 对象转部门树
     *
     * @param deptList 部门列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysDept> deptList)
    {
        return initZtree(deptList, null);
    }

    /**
     * 对象转部门树
     *
     * @param deptList 部门列表
     * @param roleDeptList 角色已存在菜单列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysDept> deptList, List<String> roleDeptList)
    {

        List<Ztree> ztrees = new ArrayList<Ztree>();
        boolean isCheck = StringUtils.isNotNull(roleDeptList);
        for (SysDept dept : deptList)
        {
            if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()))
            {
                Ztree ztree = new Ztree();
                ztree.setId(dept.getCompanyId());
                ztree.setpId(dept.getParentId());
                ztree.setName(dept.getCompanyName());
                ztree.setTitle(dept.getCompanyName());
                if (isCheck)
                {
                    ztree.setChecked(roleDeptList.contains(dept.getCompanyId() + dept.getCompanyName()));
                }
                ztrees.add(ztree);
            }
        }
        return ztrees;
    }

    /**
     * 查询部门人数
     * 
     * @param parentId 部门ID
     * @return 结果
     */
    @Override
    public int selectDeptCount(Long parentId)
    {
        SysDept dept = new SysDept();
        dept.setParentId(parentId);
        return deptMapper.selectDeptCount(dept);
    }

    /**
     * 查询部门是否存在用户
     * 
     * @param companyId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long companyId)
    {
        int result = deptMapper.checkDeptExistUser(companyId);
        return result > 0 ? true : false;
    }

    /**
     * 删除部门管理信息
     * 
     * @param companyId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(Long companyId)
    {
        return deptMapper.deleteDeptById(companyId);
    }

    /**
     * 新增保存部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDept dept)
    {
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为"正常"状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus()))
        {
            throw new ServiceException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return deptMapper.insertDept(dept);
    }

    /**
     * 修改保存部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDept(SysDept dept)
    {
        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = selectDeptById(dept.getCompanyId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept))
        {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getCompanyId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getCompanyId(), newAncestors, oldAncestors);
        }
        int result = deptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals("0", dept.getAncestors()))
        {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     * 
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept)
    {
        String ancestors = dept.getAncestors();
        Long[] companyIds = Convert.toLongArray(ancestors);
        deptMapper.updateDeptStatusNormal(companyIds);
    }

    /**
     * 修改子元素关系
     * 
     * @param companyId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long companyId, String newAncestors, String oldAncestors)
    {
        List<SysDept> children = deptMapper.selectChildrenDeptById(companyId);
        for (SysDept child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 根据部门ID查询信息
     * 
     * @param companyId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(Long companyId)
    {
        return deptMapper.selectDeptById(companyId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     * 
     * @param companyId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long companyId)
    {
        return deptMapper.selectNormalChildrenDeptById(companyId);
    }

    /**
     * 校验公司名称是否唯一
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public String checkCompanyNameUnique(SysDept dept)
    {
        Long companyId = StringUtils.isNull(dept.getCompanyId()) ? -1L : dept.getCompanyId();
        SysDept info = deptMapper.checkCompanyNameUnique(dept.getCompanyName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && info.getCompanyId().longValue() != companyId.longValue())
        {
            return UserConstants.DEPT_NAME_NOT_UNIQUE;
        }
        return UserConstants.DEPT_NAME_UNIQUE;
    }

    /**
     * 校验部门是否有数据权限
     * 
     * @param companyId 部门id
     */
    @Override
    public void checkDeptDataScope(Long companyId)
    {
        if (!SysUser.isAdmin(ShiroUtils.getUserId()))
        {
            SysDept dept = new SysDept();
            dept.setCompanyId(companyId);
            List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
            if (StringUtils.isEmpty(depts))
            {
                throw new ServiceException("没有权限访问部门数据！");
            }
        }
    }
}
