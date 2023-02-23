package com.xzkj.platform.khd.domain;


import com.xzkj.platform.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * @author WHY
 * @date 2022年03月06日 21:00
 */
@Data
public class DataStatisticAppChannelVo  extends BaseEntity
{

    //日期
    private String logDate;
    //提交总数
    private Integer sendTotal;
    //状态成功
    private Integer reportDelivrd;
    //状态失败
    private Integer reportUndeliv;
    //状态未知
    private Integer reportUnknown;
}
