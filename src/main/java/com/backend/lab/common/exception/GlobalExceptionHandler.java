package com.backend.lab.common.exception;

import com.backend.lab.common.exception.ErrorResponse.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      HttpServletRequest request, MethodArgumentNotValidException e
  ) {
    ErrorCode errorCode = ErrorCode.BAD_REQUEST;
    return ResponseEntity
        .badRequest()
        .body(
            ErrorResponse.builder()
                .path(request.getRequestURI())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .inputErrors(
                    e.getBindingResult().getFieldErrors().stream()
                        .map(FieldErrorResponse::of)
                        .toList()
                )
                .build()
        );
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      HttpServletRequest request, ConstraintViolationException e
  ) {
    ErrorCode errorCode = ErrorCode.BAD_REQUEST;
    return ResponseEntity
        .badRequest()
        .body(
            ErrorResponse.builder()
                .path(request.getRequestURI())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .inputErrors(
                    e.getConstraintViolations().stream()
                        .map(FieldErrorResponse::of)
                        .toList()
                )
                .build()
        );
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handleBindException(
      HttpServletRequest request, BindException e
  ) {
    ErrorCode errorCode = ErrorCode.BAD_REQUEST;
    return ResponseEntity
        .badRequest()
        .body(
            ErrorResponse.builder()
                .path(request.getRequestURI())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .inputErrors(
                    e.getBindingResult().getFieldErrors().stream()
                        .map(FieldErrorResponse::of)
                        .toList()
                )
                .build()
        );
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      HttpServletRequest request, MethodArgumentTypeMismatchException e
  ) {
    ErrorCode errorCode = ErrorCode.BAD_REQUEST;
    return ResponseEntity
        .badRequest()
        .body(
            ErrorResponse.builder()
                .path(request.getRequestURI())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .inputErrors(
                    List.of(
                        FieldErrorResponse.builder()
                            .field(e.getName())
                            .rejectedValue(Objects.requireNonNull(e.getValue()).toString())
                            .message(e.getParameter().getParameterName())
                            .build()
                    )
                )
                .build()
        );
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      HttpServletRequest request, BusinessException e
  ) {
    ErrorCode errorCode = e.getErrorCode();
    int status = errorCode.equals(ErrorCode.SESSION_EXPIRED) ? 401 : 400;
    return ResponseEntity
        .status(status)
        .body(ErrorResponse.builder()
            .path(request.getRequestURI())
            .code(errorCode.getCode())
            .message(e.getMessage())
            .build()
        );
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
      HttpServletRequest request, AuthenticationException e
  ) {
    ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
    return ResponseEntity
        .status(401)
        .body(ErrorResponse.builder()
            .path(request.getRequestURI())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build()
        );
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(HttpServletRequest request,
      AccessDeniedException e) {
    ErrorCode errorCode = ErrorCode.FORBIDDEN;
    return ResponseEntity
        .status(403)
        .body(ErrorResponse.builder()
            .path(request.getRequestURI())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build()
        );
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<ErrorResponse> handleRuntimeException(HttpServletRequest request,
      RuntimeException e) {

    if (e instanceof AccessDeniedException || e instanceof AuthenticationException) {
      throw e;
    }

    e.printStackTrace();

    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    return ResponseEntity
        .internalServerError()
        .body(ErrorResponse.builder()
            .path(request.getRequestURI())
            .code(errorCode.getCode())
            .message(e.getMessage())
            .build()
        );
  }


}
