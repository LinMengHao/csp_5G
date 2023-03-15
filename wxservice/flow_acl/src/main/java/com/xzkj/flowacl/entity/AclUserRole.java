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
public class AclUserRole implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    private String roleId;

    private String userId;

    private Integer isDelete;

    private Date createTime;

    private Date updateTime;


}
