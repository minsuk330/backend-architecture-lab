package com.backend.lab.domain.secret.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.property.core.entity.Property;
import jakarta.persistence.Entity;
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
public class Secret extends BaseEntity {

  private String content;

  @JoinColumn(name = "property_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Property property;

  @JoinColumn(name = "created_by_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Admin createdBy;


}
