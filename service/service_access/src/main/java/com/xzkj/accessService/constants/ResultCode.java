package com.xzkj.accessService.constants;

public enum ResultCode implements IResultCode{
    /* 成功 */
    SUCCESS("200", "成功"),

    /* 默认失败 */
    COMMON_FAIL("999", "失败"),

    /* 参数错误：1000～1999 */
    PARAM_NOT_VALID("1001", "参数无效"),
    PARAM_IS_BLANK("1002", "参数为空"),
    PARAM_TYPE_ERROR("1003", "参数类型错误"),
    PARAM_NOT_COMPLETE("1004", "参数缺失"),

    /* 用户错误 */
    USER_NOT_LOGIN("2001", "用户未登录"),
    USER_ACCOUNT_EXPIRED("2002", "账号已过期"),
    USER_CREDENTIALS_ERROR("2003", "密码错误"),
    USER_CREDENTIALS_EXPIRED("2004", "密码过期"),
    USER_ACCOUNT_DISABLE("2005", "账号不可用"),
    USER_ACCOUNT_LOCKED("2006", "账号被锁定"),
    USER_ACCOUNT_NOT_EXIST("2007", "账号不存在"),
    USER_ACCOUNT_ALREADY_EXIST("2008", "账号已存在"),
    USER_ACCOUNT_USE_BY_OTHERS("2009", "账号下线"),

    /*Chatbot下行消息的状态报告*/
    DELIVERYIMPOSSIBLE("DeliveryImpossible","下行消息投递失败"),
    MESSAGESENT("MessageSent","消息已发送到5G消息接入层"),
    DELIVEREDTONETWORK("DeliveredToNetwork","发送到终端成功"),
    MESSAGEDISPLAYED("MessageDisplayed ","消息已阅"),
    DELIVEREDTOTERMINAL("DeliveredToTerminal","以消息的形态达到终端，包括MaaP消息和普通P2P消息送达"),
    ;

    private String code;
    private String message;

    ResultCode(String code,String message){
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
