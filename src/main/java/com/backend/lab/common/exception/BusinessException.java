package com.backend.lab.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, Object... args) {
    super(errorCode.getMessage().formatted(args));
    this.errorCode = errorCode;
  }


}
