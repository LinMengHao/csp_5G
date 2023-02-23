package com.xzkj.platform.operator.service.impl;

import com.xzkj.platform.common.core.text.Convert;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.common.utils.StringUtils;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.domain.ModelRelated;
import com.xzkj.platform.operator.mapper.ModelRelatedMapper;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.operator.service.IModelRelatedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板关系e_model_relatedService业务层处理
 * 
 * @author ruoyi
 * @date 2022-10-12
 */
@Service
public class ModelRelatedServiceImpl implements IModelRelatedService 
{
    private static Logger logger = LoggerFactory.getLogger(ModelRelatedMapper.class);
    @Autowired
    private ModelRelatedMapper modelRelatedMapper;
    @Autowired
    private IChannelService channelService;

    /**
     * 查询模板关系e_model_related
     * 
     * @param id 模板关系e_model_related主键
     * @return 模板关系e_model_related
     */
    @Override
    public ModelRelated selectModelRelatedById(Long id)
    {
        return modelRelatedMapper.selectModelRelatedById(id);
    }

    /**
     * 查询模板关系e_model_related列表
     * 
     * @param modelRelated 模板关系e_model_related
     * @return 模板关系e_model_related
     */
    @Override
    public List<ModelRelated> selectModelRelatedList(ModelRelated modelRelated){
        List<ModelRelated> relatedList = modelRelatedMapper.selectModelRelatedList(modelRelated);
        List<Channel> channellist = channelService.selectChannelList(0L);
        Map<Long,String> map = new HashMap<Long,String>();
        for (Channel channel:channellist){
            map.put(channel.getId(),channel.getId()+":"+channel.getChannelName());
        }
        for (ModelRelated related:relatedList){
            related.setChannelName(map.containsKey(related.getChannelId())?map.get(related.getChannelId()):related.getChannelId()+"");
        }
        return relatedList;
    }

    /**
     * 新增模板关系e_model_related
     * 
     * @param modelRelated 模板关系e_model_related
     * @return 结果
     */
    @Override
    public int insertModelRelated(ModelRelated modelRelated)
    {
        modelRelated.setCreateTime(DateUtils.getNowDate());
        return modelRelatedMapper.insertModelRelated(modelRelated);
    }

    /**
     * 修改模板关系e_model_related
     * 
     * @param modelRelated 模板关系e_model_related
     * @return 结果
     */
    @Override
    public int updateModelRelated(ModelRelated modelRelated)
    {
        modelRelated.setUpdateTime(DateUtils.getNowDate());
        return modelRelatedMapper.updateModelRelated(modelRelated);
    }

    /**
     * 批量删除模板关系e_model_related
     * 
     * @param ids 需要删除的模板关系e_model_related主键
     * @return 结果
     */
    @Override
    public int deleteModelRelatedByIds(String ids)
    {
        return modelRelatedMapper.deleteModelRelatedByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除模板关系e_model_related信息
     * 
     * @param id 模板关系e_model_related主键
     * @return 结果
     */
    @Override
    public int deleteModelRelatedById(Long id)
    {
        return modelRelatedMapper.deleteModelRelatedById(id);
    }

    /**
     * 批量导入
     * @param list
     * @return
     */
    @Override
    public String importModelRelated(List<ModelRelated> list) {
        if (StringUtils.isNull(list) || list.size() == 0)
        {
            throw new Error("导入模版映射数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (ModelRelated modelRelated : list)
        {
            try
            {

                if (!StringUtils.isNull(modelRelated))
                {
                    List<ModelRelated> list1 = modelRelatedMapper.selectModelRelatedList(modelRelated);
                    if (StringUtils.isNull(list1) || list1.size() == 0) {
                        int i = modelRelatedMapper.insertModelRelated(modelRelated);
                        successNum++;
                        successMsg.append("<br/>" + successNum + "映射信息" + modelRelated.getModelId() + " 导入成功");
                    }else {
                        successMsg.append("<br/>" + successNum + "映射信息" + modelRelated.getModelId() + " 导入重复");
                    }
                }

            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "映射信息" + modelRelated.getModelId() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                logger.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new Error(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    @Override
    public int updateModelRelatedByQuery(ModelRelated modelRelated) {
        modelRelated.setUpdateTime(DateUtils.getNowDate());
        return modelRelatedMapper.updateModelRelatedByQuery(modelRelated);
    }

}
