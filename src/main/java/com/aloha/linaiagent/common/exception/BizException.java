package com.aloha.linaiagent.common.exception;

import com.aloha.linaiagent.common.response.ResultCode;

/**
 * Business exception for expected domain failures.
 */
public class BizException extends RuntimeException {

    private final String code;
    private final Object data;

    public BizException(String message) {
        this(ResultCode.BIZ_ERROR.getCode(), message, null, null);
    }

    public BizException(String code, String message) {
        this(code, message, null, null);
    }

    public BizException(String code, String message, Object data) {
        this(code, message, data, null);
    }

    public BizException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg(), null, null);
    }

    public BizException(ResultCode resultCode, String message) {
        this(resultCode.getCode(), message, null, null);
    }

    public BizException(ResultCode resultCode, String message, Object data) {
        this(resultCode.getCode(), message, data, null);
    }

    public BizException(String code, String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}
