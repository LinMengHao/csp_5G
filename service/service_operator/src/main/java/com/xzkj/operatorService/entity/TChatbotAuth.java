package com.xzkj.operatorService.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
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
public class TChatbotAuth implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * uuid
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 审核类型：
1-新增审核
2-变更审核
3-调试白名单审核
4-上架
     */
    @TableField("authType")
    private String authType;

    /**
     * Chatbot ID，包含域名部分
     */
    @TableField("chatbotId")
    private String chatbotId;

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

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;
}
