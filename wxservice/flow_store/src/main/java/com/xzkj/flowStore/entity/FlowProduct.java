package com.xzkj.flowStore.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2023-02-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FlowProduct implements Serializable {

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

    private Integer isDeleted;

    @TableField(fill=FieldFill.INSERT)
    private Date createTime;

    @TableField(fill=FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private Integer uid;

    /**
     * 描述
     */
    private String info;

    /**
     * 1 移动 
2 电信
3 联通
4 国际
     */
    private Integer operatorType;

    /**
     * 系统生成本地产品包编码
     */
    private String mypkgcode;

    private Integer status;


}
