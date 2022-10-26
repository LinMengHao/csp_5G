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
public class TOrder implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * 32位uuid，保持唯一
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * EC集团客户编码，EC ID
     */
    @TableField("customerNum")
    private String customerNum;

    /**
     * 产品订单号
     */
    @TableField("prodordSkuNum")
    private String prodordSkuNum;

    /**
     * 产品订购关系ID，计费编码
     */
    @TableField("prodistSkuNum")
    private String prodistSkuNum;

    /**
     * 集团客户联系人
     */
    @TableField("contactName")
    private String contactName;

    /**
     * 集团客户联系人手机号码
     */
    @TableField("contactPhone")
    private String contactPhone;

    /**
     * 大区：
01-华北
02-东北
03-华东北
04-华东南
05-华南
06-华中
07-西南
08-西北
     */
    @TableField("officeCode")
    private String officeCode;

    /**
     * 产品类型：
01-5G消息
     */
    @TableField("prodType")
    private String prodType;

    /**
     * 服务代码类型：
01-5G消息专有码号
02-全网短信自携号
03-省内短信自携号
04-已有全网短信码号
05-已有省内短信码号 
     */
    @TableField("serviceCodeType")
    private String serviceCodeType;

    /**
     * 服务代码，“服务代码类型”为03、05，则数字服务代码后加省份后缀，格式为数字+省份后缀；其他则格式为数字
     */
    @TableField("serviceCode")
    private String serviceCode;

    /**
     * 是否为CSP平台
1-是
0-否
     */
    @TableField("cspFlag")
    private String cspFlag;

    /**
     * CSP平台编码，CSP平台Flag等于1时必填
     */
    @TableField("cspId")
    private String cspId;

    /**
     * 操作类型：
1-新增
2-修改
3-取消
4-暂停
5-恢复
     */
    @TableField("opType")
    private String opType;

    /**
     * 操作时间，新增时必填，例子2020-04-04T23:59:00Z
     */
    @TableField("opTime")
    private String opTime;

    /**
     * 版本号，从1开始递增
     */
    @TableField("eTag")
    private String eTag;

    /**
     * 操作流水号
     */
    @TableField("messageId")
    private String messageId;

    /**
     * 创建时间
     */
    @TableField("gmtCreate")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField("gmtModified")
    private Date gmtModified;


}
