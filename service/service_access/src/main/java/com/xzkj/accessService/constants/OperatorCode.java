package com.xzkj.accessService.constants;

/**
 * 对接运营平台相关字典
 */
public enum OperatorCode implements IResultCode{
//    服务代码类型
    SERVICE_CODE_ONLY("01","5G消息专有码号"),
    SERVICE_CODE_ALL("02","全网短信自携号"),
    SERVICE_CODE_PROVINCE("03","省内短信自携号"),
    SERVICE_CODE_HAVE_ALL("04","已有全网短信码号"),
    SERVICE_CODE_HAVE_PROVINCE("05","已有省内短信码号"),


//    操作类型
    OPTYPE_ADD("1","新增"),
    OPTYPE_UPDATE("2","修改"),
    OPTYPE_CANCEL("3","取消"),
    OPTYPE_STOP("4","暂停"),
    OPTYPE_RECOVER("5","恢复"),

//    大区
    OFFICE_CODE_NC("01","华北"),
    OFFICE_CODE_EN("02","东北"),
    OFFICE_CODE_ENC("03","华东北"),
    OFFICE_CODE_ESC("04","华东北"),
    OFFICE_CODE_SC("05","华南"),
    OFFICE_CODE_CC("06","华中"),
    OFFICE_CODE_WS("07","西南"),
    OFFICE_CODE_WN("08","西北"),

//    chatbot状态
    CHATBOT_STATUS_NOT_ADD("11","新增审核不通过"),
    CHATBOT_STATUS_NOT_UPDATE("12","变更审核不通过"),
    CHATBOT_STATUS_AUDIT_ADD("20","管理平台新增审核中"),
    CHATBOT_STATUS_AUDIT_UPDATE("21","管理平台变更审核中"),
    CHATBOT_STATUS_AUDIT_PUTAWAY("24","上架审核中"),
    CHATBOT_STATUS_NOT_PUTAWAY("25","上架审核不通过"),
    CHATBOT_STATUS_AUDIT_IPW("26","调试白名单审核"),
    CHATBOT_STATUS_NOT_IPW("27","调试白名单审核不通过"),
    CHATBOT_STATUS_ONLINE("30","在线"),
    CHATBOT_STATUS_OLD_ONLINE("31","已在线"),
    CHATBOT_STATUS_STOP("40","暂停"),
    CHATBOT_STATUS_BLACK("41","黑名单"),
    CHATBOT_STATUS_OUT_ONLINE("42","已下线"),
    CHATBOT_STATUS_DEBUG("50","调试"),

//    审核结果
    AUTHSTATUS_OK("1","通过"),
    AUTHSTATUS_ERROR("2","不通过"),

//    审核类型
    AUTHTYPE_ADD("1","新增审核"),
    AUTHTYPE_UPDATE("2","变更审核"),
    AUTHTYPE_DEBUG("2","调试白名单审核"),
    AUTHTYPE_PUTAWAY("3","上架")
    ;

    private String code;
    private String message;

    OperatorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
