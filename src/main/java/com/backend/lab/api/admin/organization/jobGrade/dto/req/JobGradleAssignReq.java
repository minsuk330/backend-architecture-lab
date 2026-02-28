package com.backend.lab.api.admin.organization.jobGrade.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class JobGradleAssignReq {

  @NotNull
  private Long adminId;
  private Long jobGradeId;
}
