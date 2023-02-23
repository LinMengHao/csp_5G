package com.xzkj.platform.khd.domain;


import com.xzkj.platform.common.annotation.Excel;

import com.xzkj.platform.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 下行日志的显示查询对象 mms_sender
 * 
 * @author lixl
 * @date 2022-01-24
 */
@Data

public class MmsBSender extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "下发手机号")
    private String phones;


    @Excel(name = "变量")
    private String variable;






}
