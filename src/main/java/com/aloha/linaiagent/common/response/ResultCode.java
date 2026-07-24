package com.aloha.linaiagent.common.response;

/**
 * Standard application result codes.
 */
public enum ResultCode {

    SUCCESS("20000", "success"),
    BIZ_ERROR("40000", "business error"),
    PARAM_ERROR("40001", "invalid request parameter"),
    UNAUTHORIZED("40100", "unauthorized"),
    FORBIDDEN("40300", "forbidden"),
    NOT_FOUND("40400", "resource not found"),
    SYSTEM_ERROR("50000", "system error");

    private final String code;
    private final String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
