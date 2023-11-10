package org.mvasylchuk.pfcc.platform;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {
    private final PfccAppConfigurationProperties conf;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected BaseResponse<Void> handle(Exception e) {
        log.error("Unhandled exception", e);
        String msg = conf.exposeException != null && conf.exposeException ? e.getMessage() : "Internal error";
        return BaseResponse.fail(msg);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected BaseResponse<Void> handle(AccessDeniedException e) {
        return BaseResponse.fail("Access denied");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected BaseResponse<Void> handle(MethodArgumentNotValidException e) {
        return BaseResponse.fail(new ValidationErrorsDescriptor(e.getFieldErrors().stream()
                .map(fe -> new ValidationErrorsDescriptor.FieldValidationErrorsDescriptor(
                        fe.getField(),
                        fe.getRejectedValue(),
                        fe.getDefaultMessage()
                ))
                .toList()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class ValidationErrorsDescriptor {
        private final String message = "Invalid parameters passed";
        private final List<FieldValidationErrorsDescriptor> fieldsErrors;

        public record FieldValidationErrorsDescriptor(String field, Object invalidValue, String message) {
        }
    }
}
