package com.xzkj.platform.operator.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 全局黑名单对象 e_black_info
 * 
 * @author ruoyi
 * @date 2022-11-06
 */
public class BlackInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 号码 */
    @Excel(name = "号码")
    private String mobile;

    /** 黑名单等级1-高危2-中危3-低危 */
    @Excel(name = "黑名单等级1-高危2-中危3-低危")
    private Long ruleLevel;

    /** 黑名单来源 12321,miit,operator,other */
    @Excel(name = "黑名单来源 12321,miit,operator,other")
    private String source;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setMobile(String mobile) 
    {
        this.mobile = mobile;
    }

    public String getMobile() 
    {
        return mobile;
    }
    public void setRuleLevel(Long ruleLevel) 
    {
        this.ruleLevel = ruleLevel;
    }

    public Long getRuleLevel() 
    {
        return ruleLevel;
    }
    public void setSource(String source) 
    {
        this.source = source;
    }

    public String getSource() 
    {
        return source;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("mobile", getMobile())
            .append("ruleLevel", getRuleLevel())
            .append("source", getSource())
            .append("remark", getRemark())
            .append("updateTime", getUpdateTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
