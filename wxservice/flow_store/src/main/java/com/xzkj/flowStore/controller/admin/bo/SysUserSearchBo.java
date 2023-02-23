package com.xzkj.flowStore.controller.admin.bo;

import io.swagger.annotations.ApiModelProperty;

public class SysUserSearchBo extends BaseBo{

    @ApiModelProperty(value = "模糊搜索name")
    private String name;

    @ApiModelProperty(value = "模糊搜索email")
    private String email;

    @ApiModelProperty(value = "模糊搜索phone")
    private String phone;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
