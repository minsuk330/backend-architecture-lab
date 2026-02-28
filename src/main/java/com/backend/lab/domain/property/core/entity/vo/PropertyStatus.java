package com.backend.lab.domain.property.core.entity.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PropertyStatus {
  READY("준비"),
  COMPLETE("완료"),
  PENDING("보류"),
  SOLD("매각"),
  ;

  private final String displayName;
}
