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
public class TCustomerCode implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * uuid
     */
    private String id;

    /**
     * 服务代码
必须归属非直签客户关联的代理商
     */
    @TableField("serviceCode")
    private String serviceCode;

    /**
     * 扩展码（服务代码+扩展码不超过20位
     */
    @TableField("extCode")
    private String extCode;

    /**
     * 非直签客户编码
     */
    @TableField("customerNum")
    private String customerNum;

    /**
     * 操作类型
1-分配 2-收回
     */
    private String type;

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;

}
