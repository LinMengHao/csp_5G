package com.xzkj.platform.khd.domain;


import com.xzkj.platform.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 发件箱对象 t_outbox
 * 
 * @author lixl
 * @date 2022-02-07
 */
@Data
public class TOutbox extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /**  */
    private String msgId;

    /**  */
    private Long appId;

    /**  */
    private String fileName;

    /** 提交备注 */
    private String remarks;

    /**  */
    private Long sendNum;

    /**  */
    private Long sendSuccNum;

    /**  */
    private Long sendFailNum;

    /** 彩信主题 */
    private String subject;

    /** 附件保存路径 */
    private String savePath;

    /**  */
    private Date dateTime;

    /**  */
    private String status;


}
