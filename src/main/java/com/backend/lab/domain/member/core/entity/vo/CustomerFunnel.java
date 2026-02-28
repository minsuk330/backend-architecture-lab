package com.backend.lab.domain.member.core.entity.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerFunnel {
  NAVER("네이버"),
  SNS("SNS"),
  HOMEPAGE("홈페이지"),
  BLOG("블로그"),
  INTRODUCE("소개"),
  FRIENDS("지인"),
  WALK_IN("도보고객"),
  PREVIOUS("이전고객"),
  ;

  private final String displayName;

  @JsonCreator
  public static CustomerFunnel fromString(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return CustomerFunnel.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
