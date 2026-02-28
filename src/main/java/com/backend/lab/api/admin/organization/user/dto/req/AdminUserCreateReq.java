package com.backend.lab.api.admin.organization.user.dto.req;

import com.backend.lab.domain.admin.core.entity.dto.req.AdminCreateReq;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

@Getter
public class AdminUserCreateReq {
  @JsonUnwrapped
  private AdminCreateReq adminCreateReq;
  private Long departmentId;
  private Long permissionId;
  private Long jobGradeId;
}
