package org.mvasylchuk.pfcc.platform.error;

import lombok.Getter;

@Getter
public class PfccException extends RuntimeException {
    private final ApiErrorCode errorCode;

    public PfccException(ApiErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public PfccException(String message, ApiErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PfccException(String message, Throwable cause, ApiErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public PfccException(Throwable cause, ApiErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public PfccException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ApiErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}
