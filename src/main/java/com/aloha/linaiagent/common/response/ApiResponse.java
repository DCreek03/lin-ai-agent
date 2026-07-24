package com.aloha.linaiagent.common.response;

/**
 * Unified API response envelope.
 *
 * @param <T> payload type
 */
public record ApiResponse<T>(boolean success, String code, String msg, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    public static ApiResponse<Void> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMsg(), null);
    }

    public static <T> ApiResponse<T> fail(ResultCode resultCode, T data) {
        return fail(resultCode.getCode(), resultCode.getMsg(), data);
    }

    public static <T> ApiResponse<T> fail(String code, String msg) {
        return fail(code, msg, null);
    }

    public static <T> ApiResponse<T> fail(String code, String msg, T data) {
        return new ApiResponse<>(false, code, msg, data);
    }
}
