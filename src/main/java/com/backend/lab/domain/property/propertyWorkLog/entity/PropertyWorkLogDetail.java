package com.backend.lab.domain.property.propertyWorkLog.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.property.propertyWorkLog.entity.vo.LogFieldType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PropertyWorkLogDetail extends BaseEntity {
  //이걸 enum으로 생성할까 고민중
  @Enumerated(EnumType.STRING)
  private LogFieldType logFieldType;
  private String beforeValue;
  private String afterValue;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "property_work_log_id")
  private PropertyWorkLog propertyWorkLog;
}
