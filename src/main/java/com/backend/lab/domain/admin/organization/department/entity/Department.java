package com.backend.lab.domain.admin.organization.department.entity;

import com.backend.lab.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "department")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class Department extends BaseEntity {

  private Integer rank;

  private String name;
  private Boolean isActive;
  private Long migrationDepartmentId; // 부서 삭제 시, 마이그레이션을 위한 ID
}
