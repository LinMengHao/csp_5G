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
public class TChatbot implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * 32位uuid，保持唯一性
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * Chatbot ID，包含域名部分。数字部分全平台唯一，数字部分不能超过20位。@符号前面只能是数字；@符号后面不能有中文字符
     */
    @TableField("chatbotId")
    private String chatbotId;

    /**
     * Chatbot归属的EC ID（客户编码），当agentCustomerNum非空时，此字段为非直签客户编码
     */
    @TableField("customerNum")
    private String customerNum;

    /**
     * 归属代理商的EC集团客户编码（对于BBOSS直签客户，如该客户下的订单“是否为CSP”字段值为“是”时，则此客户属于代理商），如Chatbot所属客户为非直签客户时，该字段必填
     */
    @TableField("agentCustomerNum")
    private String agentCustomerNum;

    /**
     * Chatbot归属的CSP ID
     */
    @TableField("cspId")
    private String cspId;

    /**
     * 服务代码
     */
    @TableField("serviceCode")
    private String serviceCode;

    /**
     * Chatbot名称。不可携带英文双引号、\、emoji
     */
    private String name;

    /**
     * 机器人logo图标的url。返回实体附件（支持的文件类型：png、jpg、jpeg，尺寸400*400，大小限50K
     */
    private String logo;

    /**
     * 短信端口号。（系统自动取ChatbotId数字部分）
     */
    private String sms;

    /**
     * 服务电话。不能有中文和中文字符
     */
    private String callback;

    /**
     * Chatbot邮箱。不能有中文和中文字符
     */
    private String email;

    /**
     * Chatbot官网，必须以http://或https://开头。不能有中文和中文字符
     */
    private String website;

    /**
     * 服务条款，外网地址访问富文本
     */
    @TableField("tcPage")
    private String tcPage;

    /**
     * 办公地址。不能录入中英文中括号(【】[])
     */
    private String address;

    /**
     * 气泡颜色，例子：#1E90FF
     */
    private String colour;

    /**
     * 背景图片url地址，获取附件实体（附件类型支持：png、jpg、jpeg，大小20K）
     */
    private String backgroundImage;

    /**
     * 行业类型(多个类型用逗号隔开)
     */
    private String category;

    /**
     * 提供者信息。提供者开关的值是1时必填
必须是归属非直签客户名称
     */
    private String provider;

    /**
     * 提供者开关，默认为1 (1-显示 0-不显示)
     */
    private String providerSwitchCode;

    /**
     * 描述信息。不可携带英文双引号、\、emoji
     */
    private String description;

    /**
     * 归属省份编号。参见附录1.14
必须与归属CSP平台的省份一致
     */
    private String provinceCode;

    /**
     * 归属地市编号
     */
    @TableField("cityCode")
    private String cityCode;

    /**
     * 归属大区
     */
    @TableField("officeCode")
    private String officeCode;

    /**
     * 地理位置经度。只能是数字，最高百位，小数点后最多3位
     */
    private String lon;

    /**
     * 地理位置纬度。只能是数字，最高百位，小数点后最多3位
     */
    private String lat;

    /**
     * 签名
     */
    private String autograph;

    /**
     * 附件url地址。返回实体附件（支持的文件类型：pdf;doc;jpg;jpeg;gif;docx;rar;zip，大小限5M）
     */
    private String attachment;

    /**
     * Chatbot创建时间，例子YYYY-MM-DD
     */
    @TableField("createTime")
    private String createTime;

    /**
     * 操作时间，例子YYYY-MM-DD
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
     * 调试白名单，多个手机号用逗号隔开。只能是数字和逗号，逗号前后需为11位数字
     */
    @TableField("debugWhiteAddress")
    private String debugWhiteAddress;

    /**
     * 审核人员
     */
    @TableField("auditPerson")
    private String auditPerson;

    /**
     * 审核意见
     */
    @TableField("auditOpinion")
    private String auditOpinion;

    /**
     * 审核时间YYYY-MM-DD
     */
    private String v;

    /**
     * 实际下发行业：
1-党政军
2-民生
3-金融
4-物流
5-游戏
6-电商
7-微商（个人）
8-沿街商铺（中小）
9-企业（大型）
10-教育培训
11-房地产
12-医疗器械、药店
13-其他
     */
    @TableField("actualIssueIndustry")
    private String actualIssueIndustry;

    /**
     * 10-待提交
11-新增审核不通过
12-变更审核不通过
20-管理平台新增审核中  
21-管理平台变更审核中  
22-待新增审核  
23-待变更审核
20-上架审核中
21-上架审核不通过
22-调试白名单审核  
23-调试白名单审核不通过  
30-在线
31-已下线
40-暂停  
41-黑名单  
40-下架
50-调试

     */
    private String status;

    @TableField("gmtCreate")
    private Date gmtCreate;

    @TableField("gmtModified")
    private Date gmtModified;


}
