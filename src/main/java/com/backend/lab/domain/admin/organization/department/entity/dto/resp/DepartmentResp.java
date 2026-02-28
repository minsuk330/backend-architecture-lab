package com.backend.lab.domain.admin.organization.department.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResp extends BaseResp {

  private Integer rank;
  private String name;
  private Boolean isActive;
  private Long migrationDepartmentId;
  @Setter
  private String migrationDepartmentName;
}
