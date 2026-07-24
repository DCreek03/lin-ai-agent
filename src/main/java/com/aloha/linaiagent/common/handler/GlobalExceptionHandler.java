package com.aloha.linaiagent.common.handler;

import com.aloha.linaiagent.common.exception.BizException;
import com.aloha.linaiagent.common.response.ApiResponse;
import com.aloha.linaiagent.common.response.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;

/**
 * Global exception translator.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Object>> handleBizException(BizException ex) {
        return ResponseEntity.ok(ApiResponse.fail(ex.getCode(), ex.getMessage(), ex.getData()));
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        BindException.class,
        ConstraintViolationException.class,
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(ResultCode.PARAM_ERROR.getCode(), resolveBadRequestMessage(ex)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail(ResultCode.SYSTEM_ERROR.getCode(), ResultCode.SYSTEM_ERROR.getMsg()));
    }

    private String resolveBadRequestMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException validException) {
            return firstFieldErrorMessage(validException.getBindingResult().getFieldErrors())
                .orElse(ResultCode.PARAM_ERROR.getMsg());
        }
        if (ex instanceof BindException bindException) {
            return firstFieldErrorMessage(bindException.getBindingResult().getFieldErrors())
                .orElse(ResultCode.PARAM_ERROR.getMsg());
        }
        if (ex instanceof ConstraintViolationException violationException) {
            return firstViolationMessage(violationException.getConstraintViolations())
                .orElse(ResultCode.PARAM_ERROR.getMsg());
        }
        if (ex instanceof MethodArgumentTypeMismatchException typeMismatchException) {
            String requiredType = Optional.ofNullable(typeMismatchException.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("target type");
            return "parameter " + typeMismatchException.getName() + " cannot be converted to " + requiredType;
        }
        if (ex instanceof MissingServletRequestParameterException parameterException) {
            return "missing required parameter " + parameterException.getParameterName();
        }
        if (ex instanceof HttpMessageNotReadableException) {
            return "request body is malformed";
        }
        return ResultCode.PARAM_ERROR.getMsg();
    }

    private Optional<String> firstFieldErrorMessage(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
            .findFirst()
            .map(FieldError::getDefaultMessage);
    }

    private Optional<String> firstViolationMessage(Set<ConstraintViolation<?>> violations) {
        return violations.stream()
            .min(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
            .map(ConstraintViolation::getMessage);
    }
}
