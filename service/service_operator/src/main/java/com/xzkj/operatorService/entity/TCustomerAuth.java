package com.xzkj.operatorService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TCustomerAuth implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * uuid
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 审核类型：
3-管理平台新增审核
管理平台变更审核

     */
    @TableField("authType")
    private String authType;

    /**
     * 被审核的非直签客户编码
     */
    @TableField("customerNum")
    private String customerNum;

    /**
     * 审核结果：
1-通过
2-不通过
     */
    @TableField("authStatus")
    private String authStatus;

    /**
     * 审核原因
     */
    private String comment;

    /**
     * 审核人员
     */
    @TableField("authPerson")
    private String authPerson;

    /**
     * 审核时间
YYYY-MM-DD
     */
    @TableField("authTime")
    private String authTime;

    /**
     * 操作流水号
     */
    @TableField("messageId")
    private String messageId;

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;


}
