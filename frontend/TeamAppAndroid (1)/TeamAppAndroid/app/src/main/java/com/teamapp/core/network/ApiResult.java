package com.teamapp.core.network;

import androidx.annotation.Nullable;

/**
 * Wrapper cho kết quả gọi API.
 * - Success<T> chứa data
 * - HttpError chứa code + message từ server
 * - NetworkError chứa exception (mất mạng, timeout...)
 */
public class ApiResult<T> {

    public enum Kind { SUCCESS, HTTP_ERROR, NETWORK_ERROR }

    public final Kind kind;
    @Nullable public final T data;
    public final int code;                 // HTTP status (nếu có)
    @Nullable public final String message; // thông điệp lỗi user-friendly
    @Nullable public final Throwable error;

    private ApiResult(Kind kind, @Nullable T data, int code, @Nullable String message, @Nullable Throwable error) {
        this.kind = kind;
        this.data = data;
        this.code = code;
        this.message = message;
        this.error = error;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(Kind.SUCCESS, data, 200, null, null);
    }

    public static <T> ApiResult<T> httpError(int code, @Nullable String message) {
        return new ApiResult<>(Kind.HTTP_ERROR, null, code, message, null);
    }

    public static <T> ApiResult<T> networkError(Throwable t) {
        return new ApiResult<>(Kind.NETWORK_ERROR, null, 0, t != null ? t.getMessage() : null, t);
    }

    public boolean isSuccess() { return kind == Kind.SUCCESS; }
}
