package com.backend.lab.api.admin.organization.department.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DepartmentAssignReq {

  @NotNull
  private Long adminId;
  private Long departmentId;
}
