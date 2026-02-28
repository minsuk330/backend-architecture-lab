package com.backend.lab.domain.admin.organization.department.entity.dto.req;

import lombok.Getter;

@Getter
public class DepartmentUpdateReq {

  private Integer rank;
  private String name;
  private Boolean isActive;
  private Long migrationDepartmentId;
}
