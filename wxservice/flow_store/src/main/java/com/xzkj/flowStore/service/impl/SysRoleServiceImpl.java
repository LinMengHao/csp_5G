package com.xzkj.flowStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xzkj.flowStore.entity.SysRole;
import com.xzkj.flowStore.mapper.SysRoleMapper;
import com.xzkj.flowStore.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 系统角色表 服务实现类
 * </p>
 *
 * @author dashan
 * @since 2020-01-06
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {


    @Override
    public SysRole getByName(String name) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        wrapper.eq("state", 0);
        wrapper.last("limit 1");
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public Map<Long, SysRole> getMapByIdSet(Set<Long> roleIdSet) {

        Map<Long, SysRole> map = Maps.newHashMap();
        if (roleIdSet == null || roleIdSet.size() == 0) {
            return map;
        }

        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.in("id", roleIdSet);
        wrapper.eq("state", 0);
        List<SysRole> list = baseMapper.selectList(wrapper);


        if (list != null && list.size() > 0) {
            for (SysRole role : list) {
                map.put(role.getId(), role);
            }
        }
        return map;

    }
}
