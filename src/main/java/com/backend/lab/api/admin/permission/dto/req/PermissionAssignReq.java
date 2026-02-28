package com.backend.lab.api.admin.permission.dto.req;

import lombok.Getter;

@Getter
public class PermissionAssignReq {

  private Long permissionId;
  private Long adminId;
}
