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
public class TPutaway implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * uuid
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 测试报告URl，根据地址获取实体附件（附件类型支持：pdf、doc、docx、jpg、jpeg、gif、docx、rar；大小限10M）
     */
    @TableField("testReportUrl")
    private String testReportUrl;

    /**
     * 上架申请说明
     */
    @TableField("putAwayExplain")
    private String putAwayExplain;

    /**
     * Chatbot ID
     */
    @TableField("chatbotId")
    private String chatbotId;

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;


}
