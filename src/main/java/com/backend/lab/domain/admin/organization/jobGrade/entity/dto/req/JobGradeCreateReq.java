package com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class JobGradeCreateReq {

  @NotNull
  private Integer rank;
  @NotEmpty
  private String name; // 직급명
  @NotNull
  private Boolean isActive;
  private Long migrationJobGradeId; // 직급 삭제 시, 마이그레이션을 위한 ID
}
