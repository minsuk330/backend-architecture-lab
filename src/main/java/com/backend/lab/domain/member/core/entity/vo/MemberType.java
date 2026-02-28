package com.backend.lab.domain.member.core.entity.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
  CUSTOMER("고객"),
  AGENT("공인중개사"),
  BUYER("매수자"),
  SELLER("매도자"),
  ;
  private final String displayName;


  public static MemberType fromName(String name) {

    for (MemberType type : MemberType.values()) {
      if (type.name().equals(name) || type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid member type: " + name);
  }

  public String[] getRoleNames() {
    return new String[]{"ROLE_" + this.name(), "ROLE_MEMBER"};
  }
}
