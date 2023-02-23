package com.xzkj.platform.operator.service;



import com.xzkj.platform.operator.domain.Channel;

import java.util.List;

/**
 * 通道信息Service接口
 * 
 * @author admin
 * @date 2022-09-25
 */
public interface IChannelService 
{
    /**
     * 查询通道信息
     * 
     * @param id 通道信息主键
     * @return 通道信息
     */
    public Channel selectChannelById(Long id);

    /**
     * 查询通道信息列表
     * 
     * @param channel 通道信息
     * @return 通道信息集合
     */
    public List<Channel> selectChannelListPage(Channel channel);
    public List<Channel> selectChannelList(Long CompanyId);

    /**
     * 新增通道信息
     * 
     * @param channel 通道信息
     * @return 结果
     */
    public int insertChannel(Channel channel);

    /**
     * 修改通道信息
     * 
     * @param channel 通道信息
     * @return 结果
     */
    public int updateChannel(Channel channel);

    /**
     * 批量删除通道信息
     * 
     * @param ids 需要删除的通道信息主键集合
     * @return 结果
     */
    public int deleteChannelByIds(String ids);

    /**
     * 删除通道信息信息
     * 
     * @param id 通道信息主键
     * @return 结果
     */
    public int deleteChannelById(Long id);
}
