package com.backend.lab.domain.admin.organization.jobGrade.entity;

import com.backend.lab.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class JobGrade extends BaseEntity {

  private Integer rank;

  private String name; // 직급명
  private Boolean isActive;
  private Long migrationJobGradeId; // 직급 삭제 시, 마이그레이션을 위한 ID
}
