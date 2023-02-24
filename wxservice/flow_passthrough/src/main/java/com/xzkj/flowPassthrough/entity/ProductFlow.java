package com.xzkj.flowPassthrough.entity;

import java.math.BigDecimal;

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
 * @since 2023-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProductFlow implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 产品包编码
     */
    private String pkgcode;

    /**
     * 运营商
     */
    private String operatorName;

    /**
     * 流量包大小(单位：M)
     */
    private Double pkgsize;

    /**
     * 标准价格(单位：元)
     */
    private BigDecimal pkgstprice;

    /**
     * 充值省份
     */
    private String provinceName;

    /**
     * 使用类型（全国包，省内包）
     */
    private String areatypename;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;

    /**
     * 用户标识
     */
    private Long uid;

}
