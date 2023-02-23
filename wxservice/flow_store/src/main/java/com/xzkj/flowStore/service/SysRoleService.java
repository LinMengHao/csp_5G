package com.xzkj.flowStore.service;

import com.xzkj.flowStore.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 系统角色表 服务类
 * </p>
 *
 * @author dashan
 * @since 2020-01-06
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 根据角色名称查询
     * @param name
     * @return
     */
    SysRole getByName(String name);

    /**
     * 根据id集合查询
     * @param roleIdSet
     * @return
     */
    Map<Long, SysRole> getMapByIdSet(Set<Long> roleIdSet);
}
