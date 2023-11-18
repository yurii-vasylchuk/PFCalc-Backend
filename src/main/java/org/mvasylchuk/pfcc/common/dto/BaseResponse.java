package org.mvasylchuk.pfcc.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseResponse<T> {
    private boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ApiErrorCode errorCode;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, null, data, null);
    }

    public static <Void> BaseResponse<Void> fail(Object error, ApiErrorCode errorCode) {
        return new BaseResponse<>(false, error, null, errorCode);
    }
}
