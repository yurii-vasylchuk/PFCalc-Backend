package org.mvasylchuk.pfcc.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseResponse<T> {
    private boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, null, data);
    }

    public static <Void> BaseResponse<Void> fail(Object error) {
        return new BaseResponse<>(false, error, null);
    }
}
