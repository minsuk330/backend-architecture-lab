package com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req;

import lombok.Getter;

@Getter
public class JobGradeUpdateReq {

  private Integer rank;
  private String name; // 직급명
  private Boolean isActive;
  private Long migrationJobGradeId; // 직급 삭제 시, 마이그레이션을 위한 ID
}
