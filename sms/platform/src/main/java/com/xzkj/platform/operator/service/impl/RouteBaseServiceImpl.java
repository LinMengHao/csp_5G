package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.domain.entity.SysDept;
import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.PageUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.domain.RouteBase;
import com.xzkj.platform.operator.mapper.RouteBaseMapper;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.operator.service.IRouteBaseService;
import com.xzkj.platform.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由信息e_route_baseService业务层处理
 * 
 * @author ruoyi
 * @date 2022-09-18
 */
@Service
public class RouteBaseServiceImpl implements IRouteBaseService
{
    @Autowired
    private RouteBaseMapper routeBaseMapper;
    @Autowired
    private IChannelService channelService;
    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private ITApplicationService tApplicationService;

    /**
     * 查询路由信息e_route_base
     * 
     * @param id 路由信息e_route_base主键
     * @return 路由信息e_route_base
     */
    @Override
    public RouteBase selectRouteBaseById(Long id)
    {
        RouteBase routeBase = routeBaseMapper.selectRouteBaseById(id);
        SysDept dept = deptService.selectDeptById(routeBase.getCompanyId());
        routeBase.setCompanyName(dept!=null?dept.getCompanyName():"未知");
        return routeBase;
    }

    /**
     * 查询路由信息e_route_base列表
     * 
     * @param routeBase 路由信息e_route_base
     * @return 路由信息e_route_base
     */
    @Override
    public List<RouteBase> selectRouteBaseList(RouteBase routeBase)
    {
        List<RouteBase> list = routeBaseMapper.selectRouteBaseList(routeBase);
        PageUtils.clearPage();
        List<Channel> channellist = channelService.selectChannelList(0L);
        Map<Long,String> map = new HashMap<Long,String>();
        for (Channel channel:channellist){
            map.put(channel.getId(),channel.getId()+":"+channel.getChannelName());
        }

        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Map<Long,String> mapDept = new HashMap<Long,String>();
        for (SysDept dept:deptList){
            mapDept.put(dept.getCompanyId(),dept.getCompanyId()+":"+dept.getCompanyName());
        }

        List<TApplication> applist = tApplicationService.selectTApplicationListN(0L);
        Map<Long,String> mapApp = new HashMap<Long,String>();
        for (TApplication app:applist){
            mapApp.put(app.getId(),app.getId()+":"+app.getAppName());
        }

        for (RouteBase en:list){
            String provide =en.getToCmcc().equals("yes")?"支持/":"<font color='red'>不支持</font>/";
            provide +=en.getToUnicom().equals("yes")?"支持/":"<font color='red'>不支持</font>/";
            provide +=en.getToTelecom().equals("yes")?"支持/":"<font color='red'>不支持</font>/";
            provide +=en.getToInternational().equals("yes")?"支持":"<font color='red'>不支持</font>";
            en.setToProvide(provide);
            en.setChannelName(map.containsKey(en.getChannelId())?map.get(en.getChannelId()):en.getChannelId()+":");
            en.setAppName(mapApp.containsKey(en.getAppId())?mapApp.get(en.getAppId()):en.getAppId()+":");
            en.setCompanyName(mapDept.containsKey(en.getCompanyId())?mapDept.get(en.getCompanyId()):en.getCompanyId()+":");
        }
        return list;
    }

    /**
     * 新增路由信息e_route_base
     * 
     * @param routeBase 路由信息e_route_base
     * @return 结果
     */
    @Override
    public int insertRouteBase(RouteBase routeBase)
    {
        routeBase.setUpdateTime(DateUtils.getNowDate());
        routeBase.setCreateTime(DateUtils.getNowDate());
        return routeBaseMapper.insertRouteBase(routeBase);
    }

    /**
     * 修改路由信息e_route_base
     * 
     * @param routeBase 路由信息e_route_base
     * @return 结果
     */
    @Override
    public int updateRouteBase(RouteBase routeBase)
    {
        routeBase.setUpdateTime(DateUtils.getNowDate());
        return routeBaseMapper.updateRouteBase(routeBase);
    }

    /**
     * 批量删除路由信息e_route_base
     * 
     * @param ids 需要删除的路由信息e_route_base主键
     * @return 结果
     */
    @Override
    public int deleteRouteBaseByIds(String ids)
    {
        return routeBaseMapper.deleteRouteBaseByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除路由信息e_route_base信息
     * 
     * @param id 路由信息e_route_base主键
     * @return 结果
     */
    @Override
    public int deleteRouteBaseById(Long id)
    {
        return routeBaseMapper.deleteRouteBaseById(id);
    }
}
