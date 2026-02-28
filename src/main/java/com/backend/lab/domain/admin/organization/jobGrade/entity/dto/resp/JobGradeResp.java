package com.backend.lab.domain.admin.organization.jobGrade.entity.dto.resp;

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
public class JobGradeResp extends BaseResp {

  private Integer rank;
  private String name; // 직급명
  private Boolean isActive;
  private Long migrationJobGradeId;
  @Setter
  private String migrationJobGradeName;
}
