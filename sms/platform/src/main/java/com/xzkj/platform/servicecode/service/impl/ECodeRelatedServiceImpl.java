package com.xzkj.platform.servicecode.service.impl;

import java.util.List;
import com.xzkj.platform.common.utils.DateUtils;
import com.xzkj.platform.khd.domain.TApplication;
import com.xzkj.platform.khd.service.ITApplicationService;
import com.xzkj.platform.model.domain.EModelInfo;
import com.xzkj.platform.model.service.IEModelInfoService;
import com.xzkj.platform.operator.domain.Channel;
import com.xzkj.platform.operator.service.IChannelService;
import com.xzkj.platform.sign.domain.EModelSign;
import com.xzkj.platform.sign.service.IEModelSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xzkj.platform.servicecode.mapper.ECodeRelatedMapper;
import com.xzkj.platform.servicecode.domain.ECodeRelated;
import com.xzkj.platform.servicecode.service.IECodeRelatedService;
import com.xzkj.platform.common.core.text.Convert;

/**
 * 服务码号管理Service业务层处理
 * 
 * @author linmenghao
 * @date 2023-03-16
 */
@Service
public class ECodeRelatedServiceImpl implements IECodeRelatedService 
{
    @Autowired
    private ECodeRelatedMapper eCodeRelatedMapper;

    @Autowired
    private ITApplicationService tApplicationService;

    @Autowired
    private IEModelInfoService modelInfoService;

    @Autowired
    private IEModelSignService signService;

    @Autowired
    private IChannelService channelService;

    /**
     * 查询服务码号管理
     * 
     * @param id 服务码号管理主键
     * @return 服务码号管理
     */
    @Override
    public ECodeRelated selectECodeRelatedById(Long id)
    {
        return eCodeRelatedMapper.selectECodeRelatedById(id);
    }

    /**
     * 查询服务码号管理列表
     * 
     * @param eCodeRelated 服务码号管理
     * @return 服务码号管理
     */
    @Override
    public List<ECodeRelated> selectECodeRelatedList(ECodeRelated eCodeRelated)
    {
        return eCodeRelatedMapper.selectECodeRelatedList(eCodeRelated);
    }

    /**
     * 新增服务码号管理
     * 
     * @param eCodeRelated 服务码号管理
     * @return 结果
     */
    @Override
    public int insertECodeRelated(ECodeRelated eCodeRelated)
    {
        Long appId = eCodeRelated.getAppId();
        TApplication application = tApplicationService.selectTApplicationById(appId);
        eCodeRelated.setAppName(application.getAppName());
        eCodeRelated.setCompanyId(application.getCompanyId());
        eCodeRelated.setCreateTime(DateUtils.getNowDate());
        return eCodeRelatedMapper.insertECodeRelated(eCodeRelated);
    }

    /**
     * 修改服务码号管理
     * 
     * @param eCodeRelated 服务码号管理
     * @return 结果
     */
    @Override
    public int updateECodeRelated(ECodeRelated eCodeRelated)
    {
        Long appId = eCodeRelated.getAppId();
        TApplication application = tApplicationService.selectTApplicationById(appId);
        eCodeRelated.setAppName(application.getAppName());
        eCodeRelated.setUpdateTime(DateUtils.getNowDate());
        return eCodeRelatedMapper.updateECodeRelated(eCodeRelated);
    }

    /**
     * 批量删除服务码号管理
     * 
     * @param ids 需要删除的服务码号管理主键
     * @return 结果
     */
    @Override
    public int deleteECodeRelatedByIds(String ids)
    {
        return eCodeRelatedMapper.deleteECodeRelatedByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除服务码号管理信息
     * 
     * @param id 服务码号管理主键
     * @return 结果
     */
    @Override
    public int deleteECodeRelatedById(Long id)
    {
        return eCodeRelatedMapper.deleteECodeRelatedById(id);
    }

    @Override
    public ECodeRelated autoComplete(ECodeRelated codeRelated) {
        ECodeRelated eCodeRelated=new ECodeRelated();

        //账号扩展码补全
        TApplication application = tApplicationService.selectTApplicationById(codeRelated.getAppId());
        if(application!=null){
            eCodeRelated.setAppExt(application.getAppExt());
        }

        //服务码号补全
        EModelSign sign=new EModelSign();
        sign.setpSignId(codeRelated.getSignId());
        sign.setChannelId(codeRelated.getChannelId());
        List<EModelSign> eModelSigns = signService.selectEModelSignList(sign);
        if (eModelSigns.size()==1){
            eCodeRelated.setServiceCode(eModelSigns.get(0).getServiceCode());
        }

        //接入码补全
        Channel channel = channelService.selectChannelById(codeRelated.getChannelId());
        if(channel!=null){
            String sourceMent = channel.getSourceMent();
            eCodeRelated.setAccessExt(sourceMent);
            eCodeRelated.setAccessVirtualExt(sourceMent);
        }


        //模板扩展码补全
        EModelInfo modelInfo=new EModelInfo();
        modelInfo.setpModelId(codeRelated.getModelId());
        modelInfo.setChannelId(codeRelated.getChannelId());
        List<EModelInfo> modelInfos = modelInfoService.selectSonModelInfoList(modelInfo);
        if(modelInfos.size()==1){
            eCodeRelated.setModelExt(String.valueOf(modelInfos.get(0).getModelExt()));
        }
        return eCodeRelated;
    }
}
