package com.xzkj.platform.operator.mapper;

import com.xzkj.platform.operator.domain.RouteBase;

import java.util.List;

/**
 * 路由信息e_route_baseMapper接口
 * 
 * @author ruoyi
 * @date 2022-09-18
 */
public interface RouteBaseMapper
{
    /**
     * 查询路由信息e_route_base
     * 
     * @param id 路由信息e_route_base主键
     * @return 路由信息e_route_base
     */
    public RouteBase selectRouteBaseById(Long id);

    /**
     * 查询路由信息e_route_base列表
     * 
     * @param routeBase 路由信息e_route_base
     * @return 路由信息e_route_base集合
     */
    public List<RouteBase> selectRouteBaseList(RouteBase routeBase);

    /**
     * 新增路由信息e_route_base
     * 
     * @param routeBase 路由信息e_route_base
     * @return 结果
     */
    public int insertRouteBase(RouteBase routeBase);

    /**
     * 修改路由信息e_route_base
     * 
     * @param routeBase 路由信息e_route_base
     * @return 结果
     */
    public int updateRouteBase(RouteBase routeBase);

    /**
     * 删除路由信息e_route_base
     * 
     * @param id 路由信息e_route_base主键
     * @return 结果
     */
    public int deleteRouteBaseById(Long id);

    /**
     * 批量删除路由信息e_route_base
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRouteBaseByIds(String[] ids);
}
