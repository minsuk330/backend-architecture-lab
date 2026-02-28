package com.backend.lab.api.admin.organization.user.dto.req;

import com.backend.lab.domain.admin.core.entity.dto.req.AdminUpdateReq;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

@Getter
public class AdminUserUpdateReq {

  @JsonUnwrapped
  private AdminUpdateReq adminUpdateReq;
  private Long departmentId;
  private Long jobGradeId;
  private Long permissionId;
}
