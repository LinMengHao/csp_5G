package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.mapper.ChannelMapper;
import com.xzkj.platform.operator.service.IChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通道信息Service业务层处理
 * 
 * @author admin
 * @date 2022-09-25
 */
@Service
public class ChannelServiceImpl implements IChannelService 
{
    @Autowired
    private ChannelMapper channelMapper;

    /**
     * 查询通道信息
     * 
     * @param id 通道信息主键
     * @return 通道信息
     */
    @Override
    public Channel selectChannelById(Long id)
    {
        return channelMapper.selectChannelById(id);
    }

    /**
     * 查询通道信息列表
     * 
     * @param channel 通道信息
     * @return 通道信息
     */
    @Override
    public List<Channel> selectChannelListPage(Channel channel)
    {
        List<Channel> channleList = channelMapper.selectChannelListPage(channel);
        for (Channel en:channleList) {
            String provide =en.getToCmcc().equals("yes")?"支持/":"<font color='red'>不支持</font>/";
            provide +=en.getToUnicom().equals("yes")?"支持/":"<font color='red'>不支持</font>/";
            provide +=en.getToTelecom().equals("yes")?"支持/":"<font color='red'>不支持</font>/";
            //provide +=en.getToInternational().equals("yes")?"支持":"<font color='red'>不支持</font>";
            en.setToProvide(provide);
            String haveRMS=en.getHaveReport().equals("yes")?"有/":"<font color='red'>无</font>/";
            haveRMS +=en.getHaveMo().equals("yes")?"有/":"<font color='red'>无</font>/";
            haveRMS +=en.getStatus().equals("normal")?"启用":"<font color='red'>暂停</font>";
            en.setHaveRMS(haveRMS);

        }
        return channleList;
    }
    @Override
    public List<Channel> selectChannelList(Long CompanyId)
    {
        return channelMapper.selectChannelList(CompanyId);
    }

    /**
     * 新增通道信息
     * 
     * @param channel 通道信息
     * @return 结果
     */
    @Override
    public int insertChannel(Channel channel)
    {
        channel.setUpdateTime(DateUtils.getNowDate());
        channel.setCreateTime(DateUtils.getNowDate());
        return channelMapper.insertChannel(channel);
    }

    /**
     * 修改通道信息
     * 
     * @param channel 通道信息
     * @return 结果
     */
    @Override
    public int updateChannel(Channel channel)
    {
        channel.setUpdateTime(DateUtils.getNowDate());
        return channelMapper.updateChannel(channel);
    }

    /**
     * 批量删除通道信息
     * 
     * @param ids 需要删除的通道信息主键
     * @return 结果
     */
    @Override
    public int deleteChannelByIds(String ids)
    {
        return channelMapper.deleteChannelByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除通道信息信息
     * 
     * @param id 通道信息主键
     * @return 结果
     */
    @Override
    public int deleteChannelById(Long id)
    {
        return channelMapper.deleteChannelById(id);
    }
}
