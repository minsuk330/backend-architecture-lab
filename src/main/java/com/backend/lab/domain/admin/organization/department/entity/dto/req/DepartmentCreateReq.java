package com.backend.lab.domain.admin.organization.department.entity.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DepartmentCreateReq {

  @NotNull
  private Integer rank;
  @NotEmpty
  private String name;
  @NotNull
  private Boolean isActive;
  private Long migrationDepartmentId;
}
