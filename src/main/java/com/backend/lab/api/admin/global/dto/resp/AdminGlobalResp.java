package com.backend.lab.api.admin.global.dto.resp;

import com.backend.lab.domain.admin.audit.accessLog.entity.dto.resp.AdminAccessLogResp;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.admin.organization.department.entity.dto.resp.DepartmentResp;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.resp.JobGradeResp;
import com.backend.lab.domain.admin.permission.entity.dto.resp.PermissionResp;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminGlobalResp {

  @JsonUnwrapped
  private AdminResp admin;
  private long propertyCount;
  private DepartmentResp department;
  private JobGradeResp jobGrade;
  private AdminAccessLogResp lastLog;
  private PermissionResp permission;
}
