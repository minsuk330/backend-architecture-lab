package com.backend.lab.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  ENTITY_NOT_FOUND("%s 엔티티를 찾을 수 없습니다."),
  DUPLICATED("%s 중복된 데이터입니다."),
  BAD_REQUEST("잘못된 입력 값입니다."),

  ACCOUNT_NOT_FOUND("해당 계정을 찾을 수 없습니다."),

  UNAUTHORIZED("인증되지 않은 사용자입니다."),
  FORBIDDEN("권한이 없는 사용자입니다."),
  BLOCKED("차단된 사용자입니다 관리자에게 문의하세요."),
  ACCESS_DENIED("사용자 인증에 실패하였습니다."),

  LOGIN_FAILED("로그인에 실패하였습니다."),
  EMAIL_DUPLICATED("이미 존재하는 이메일입니다."),
  PHONE_NUMBER_DUPLICATED("이미 가입된 회원입니다."),
  SESSION_EXPIRED("세션이 만료되었습니다. 다시 로그인해주세요."),

  DUPLICATED_ADMIN_EMAIL("이미 존재하는 관리자 이메일입니다."),

  LACK_OF_CONTACT_ITEM("매도자 정보 열람권이 없습니다"),
  LACK_OF_ADV_ITEM("매물 광고권이 없습니다"),

  MAIL_SEND_FAILED("메일 전송에 실패하였습니다."),

  INTERNAL_SERVER_ERROR("서버 에러입니다."),
  FILE_UPLOAD_FAILED("파일 업로드에 실패하였습니다."),
  PNU_INVALID_FILE("올바르지 않은 파일 형식입니다"),
  CANT_PROCESS_WITH_CONFLICT("충돌 매물이 존재합니다."),
  ERROR_WHITE_PROCESS_XLSX("엑셀 파일 처리 중 에러가 발생했습니다, %s");
  private final String message;

  public String getCode() {
    return name();
  }
}
