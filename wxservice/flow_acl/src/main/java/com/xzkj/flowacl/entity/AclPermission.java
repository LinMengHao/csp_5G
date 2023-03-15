package com.xzkj.flowacl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
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
 * @since 2023-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AclPermission implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 所属上级
     */
    private String pid;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型(1:菜单,2:按钮)
     */
    private Integer type;

    /**
     * 权限值
     */
    private String permissionValue;

    /**
     * 访问路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态(0:禁止,1:正常)
     */
    private Integer status;

    /**
     * 描述
     */
    private String info;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    private Date createTime;

    private Date updateTime;


}
