package com.xzkj.platform.khd.domain;

import lombok.Data;

import java.util.Date;

@Data
public class TSignReport {
    private Integer id;

    private Integer channelId;

    private String exCode;

    private String reportSign;

    private String realSendCompany;

    private String category;

    private Date dateTime;

    private String productId;

    private String sendStatus;

    private Date sendTime;

    private String reportStatus;

    private Date reportTime;

    private Integer userId;

    private Integer auditId;

    private String reportDescribe;
    private   String  userName;
    private   String  auditName;
    private  String   customerClassification;
    private  String   customerNum;
    private  String   operationType;

}