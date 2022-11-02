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
public class TChatbotMenu implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * 32位uuid
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * Chatbot ID, 包含域名部分
     */
    @TableField("chatbotId")
    private String chatbotId;

    /**
     * Chatbot固定菜单Json内容，采用Base64编码，编码前的内容长度不超过2048字节
需按照唯一性规则填写菜单（建议操作/建议回复）中包含的postback.data，可采用UUID
支持2级3*5菜单
     */
    private String menu;

    /**
     * 操作类型：
1-送审
2-应用(业务未定义，暂不联调)
     */
    @TableField("opType")
    private String opType;

    /**
     * 版本号，从1开始递增
     */
    @TableField("eTag")
    private String eTag;

    /**
     * 操作日期，例子2020-04-04T23:59:00Z
     */
    @TableField("opTime")
    private String opTime;

    /**
     * 操作流水号
     */
    @TableField("messageId")
    private String messageId;

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;
}
