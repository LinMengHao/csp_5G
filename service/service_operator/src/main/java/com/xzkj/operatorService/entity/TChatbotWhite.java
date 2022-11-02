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
public class TChatbotWhite implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * uuid
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * chatbotid
     */
    @TableField("chatbotId")
    private String chatbotId;

    /**
     * 调试白名单列表。只能是数字和逗号，逗号前后需为11位数字
     */
    @TableField("chatBotWhiteList")
    private String chatBotWhiteList;

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;
}
