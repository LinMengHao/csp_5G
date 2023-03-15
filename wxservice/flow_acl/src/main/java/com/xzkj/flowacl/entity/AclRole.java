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
public class AclRole implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * 主键id，采用ID_WORKER_STR
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    private Date createTime;

    private Date updateTime;


}
