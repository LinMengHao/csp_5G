package com.xzkj.platform.sign.service.impl;

import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.sign.domain.ESignRelated;
import com.xzkj.platform.sign.mapper.ESignRelatedMapper;
import com.xzkj.platform.sign.service.IESignRelatedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 签名映射关系Service业务层处理
 * 
 * @author linmenghao
 * @date 2023-02-06
 */
@Service
public class ESignRelatedServiceImpl implements IESignRelatedService 
{
    @Autowired
    private ESignRelatedMapper eSignRelatedMapper;
    @Autowired
    private IChannelService channelService;

    /**
     * 查询签名映射关系
     * 
     * @param id 签名映射关系主键
     * @return 签名映射关系
     */
    @Override
    public ESignRelated selectESignRelatedById(Long id)
    {
        return eSignRelatedMapper.selectESignRelatedById(id);
    }

    /**
     * 查询签名映射关系列表
     * 
     * @param eSignRelated 签名映射关系
     * @return 签名映射关系
     */
    @Override
    public List<ESignRelated> selectESignRelatedList(ESignRelated eSignRelated)
    {
        List<ESignRelated> eSignRelateds = eSignRelatedMapper.selectESignRelatedList(eSignRelated);
        List<Channel> channellist = channelService.selectChannelList(0L);
        Map<Long,String> map = new HashMap<Long,String>();
        for (Channel channel:channellist){
            map.put(channel.getId(),channel.getId()+":"+channel.getChannelName());
        }
        for (ESignRelated related:eSignRelateds){
            related.setChannelName(map.containsKey(related.getChannelId())?map.get(related.getChannelId()):related.getChannelId()+"");
        }
        return eSignRelateds;
    }

    /**
     * 新增签名映射关系
     * 
     * @param eSignRelated 签名映射关系
     * @return 结果
     */
    @Override
    public int insertESignRelated(ESignRelated eSignRelated)
    {
        eSignRelated.setCreateTime(DateUtils.getNowDate());
        eSignRelated.setUpdateTime(DateUtils.getNowDate());
        return eSignRelatedMapper.insertESignRelated(eSignRelated);
    }

    /**
     * 修改签名映射关系
     * 
     * @param eSignRelated 签名映射关系
     * @return 结果
     */
    @Override
    public int updateESignRelated(ESignRelated eSignRelated)
    {
        eSignRelated.setUpdateTime(DateUtils.getNowDate());
        return eSignRelatedMapper.updateESignRelated(eSignRelated);
    }

    /**
     * 批量删除签名映射关系
     * 
     * @param ids 需要删除的签名映射关系主键
     * @return 结果
     */
    @Override
    public int deleteESignRelatedByIds(String ids)
    {
        return eSignRelatedMapper.deleteESignRelatedByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除签名映射关系信息
     * 
     * @param id 签名映射关系主键
     * @return 结果
     */
    @Override
    public int deleteESignRelatedById(Long id)
    {
        return eSignRelatedMapper.deleteESignRelatedById(id);
    }
}
