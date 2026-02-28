package com.backend.lab.common.exception;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.FieldError;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  private String code;
  private String message;
  private String path;
  private List<FieldErrorResponse> inputErrors;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FieldErrorResponse {
    private String field;
    private Object rejectedValue;
    private String message;

    public static FieldErrorResponse of(FieldError error) {
      return FieldErrorResponse.builder()
          .field(error.getField())
          .rejectedValue(error.getRejectedValue())
          .message(error.getDefaultMessage())
          .build();
    }

    public static FieldErrorResponse of(ConstraintViolation<?> error) {
      PathImpl propertyPath = (PathImpl) error.getPropertyPath();
      return FieldErrorResponse.builder()
          .field(propertyPath.getLeafNode().toString())
          .rejectedValue(error.getInvalidValue().toString())
          .message(error.getMessage())
          .build();
    }
  }
}
