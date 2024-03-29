package com.xzkj.platform.system.mapper;

import com.xzkj.platform.common.core.domain.entity.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门管理 数据层
 * 
 * @author ruoyi
 */
public interface SysDeptMapper
{
    /**
     * 查询部门人数
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int selectDeptCount(SysDept dept);

    /**
     * 查询部门是否存在用户
     *
     * @param companyId 部门ID
     * @return 结果
     */
    public int checkDeptExistUser(Long companyId);

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 删除部门管理信息
     *
     * @param companyId 部门ID
     * @return 结果
     */
    public int deleteDeptById(Long companyId);

    /**
     * 新增部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(SysDept dept);

    /**
     * 修改部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(SysDept dept);

    /**
     * 修改子元素关系
     *
     * @param depts 子元素
     * @return 结果
     */
    public int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 根据部门ID查询信息
     *
     * @param companyId 部门ID
     * @return 部门信息
     */
    public SysDept selectDeptById(Long companyId);

    /**
     * 校验公司名称是否唯一
     *
     * @param companyName 公司名称
     * @param parentId 父部门ID
     * @return 结果
     */
    public SysDept checkCompanyNameUnique(@Param("companyName") String companyName, @Param("parentId") Long parentId);

    /**
     * 根据角色ID查询部门
     *
     * @param roleId 角色ID
     * @return 部门列表
     */
    public List<String> selectRoleDeptTree(Long roleId);

    /**
     * 修改所在部门正常状态
     *
     * @param companyIds 部门ID组
     */
    public void updateDeptStatusNormal(Long[] companyIds);

    /**
     * 根据ID查询所有子部门
     *
     * @param companyId 部门ID
     * @return 部门列表
     */
    public List<SysDept> selectChildrenDeptById(Long companyId);

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param companyId 部门ID
     * @return 子部门数
     */
    public int selectNormalChildrenDeptById(Long companyId);
}