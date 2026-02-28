package com.backend.lab.domain.admin.core.entity.vo;

public enum AdminRole {
  ADMIN,
  EMPLOYEE,
  ;

  public String[] getRoleName() {
    return new String[]{"ROLE_" + name()};
  }
}
