package com.xzkj.platform.khd.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.xzkj.platform.common.annotation.Excel;

import com.xzkj.platform.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 模板管理对象 t_model
 * 
 * @author Lixl
 * @date 2022-02-09
 */
@Data
public class TModel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 模板id */
    @Excel(name = "模板id")
    private String modelId;

    /** 模板标题 */
    @Excel(name = "模板标题")
    private String title;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "添加时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date addTime;

    /** 账号id */
    @Excel(name = "账号id")
    private Long appId;

    /** 产品id */
    @Excel(name = "产品id")
    private String productId;

    /** 是否有变量 */
    @Excel(name = "是否有变量")
    private String variate;

//    @Excel(name = "签名")
//    private String reportSign;
    /** 账号 */
//    @Excel(name = "账号")
//    private String appname;
    private TApplication application;
    private TSignReport signReport;
    private MmsModel mmsModel;

//    public TApplication getApplication() {
//        if (application == null)
//        {
//            application = new TApplication();
//        }
//        return application;
//    }
//
//    public void setApplication(TApplication application) {
//        this.application = application;
//    }


}
