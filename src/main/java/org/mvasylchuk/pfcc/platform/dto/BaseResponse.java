package org.mvasylchuk.pfcc.platform.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseResponse<T> {
    private boolean success;
    private String error;
    private T data;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, null, data);
    }

    public static <Void> BaseResponse<Void> fail(String error) {
        return new BaseResponse<>(false, error, null);
    }
}
