package com.xzkj.flowStore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户日登录日志
 * </p>
 *
 * @author dashan
 * @since 2019-05-23
 */
@Data
public class LoginDayLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 时间
     */
    @TableField("date")
    private Long date;

    /**
     * 用户Id
     */
    @TableField("user_id")
    private Long userId;

}
