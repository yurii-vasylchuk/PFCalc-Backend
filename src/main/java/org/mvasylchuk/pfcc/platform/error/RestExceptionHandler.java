package org.mvasylchuk.pfcc.platform.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

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
        return BaseResponse.fail(msg, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    protected BaseResponse<Void> handle(AccessDeniedException ignored) {
        return BaseResponse.fail("Access denied", null);
    }

    @ExceptionHandler(PfccException.class)
    protected ResponseEntity<BaseResponse<Void>> handle(PfccException e) {
        log.info("Invalid request", e);
        BaseResponse<Void> body = BaseResponse.fail("Invalid request", e.getErrorCode());
        return ResponseEntity.status(this.translateToStatus(e))
                .body(body);
    }

    private HttpStatusCode translateToStatus(PfccException e) {
        return switch (e.getErrorCode()) {
            case SECURITY -> FORBIDDEN;
            default -> BAD_REQUEST;
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    protected BaseResponse<Void> handle(MethodArgumentNotValidException e) {
        return BaseResponse.fail(new ValidationErrorsDescriptor(e.getFieldErrors().stream()
                                                                 .map(fe -> new ValidationErrorsDescriptor.FieldValidationErrorsDescriptor(
                                                                         fe.getField(),
                                                                         fe.getRejectedValue(),
                                                                         fe.getDefaultMessage()
                                                                 ))
                                                                 .toList()), ApiErrorCode.VALIDATION);
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
