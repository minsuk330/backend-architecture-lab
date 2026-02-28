package com.backend.lab.domain.property.core.entity.information;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.property.core.entity.embedded.FloorProperties;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "floor_information")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FloorInformation extends BaseEntity {

  @Embedded
  private FloorProperties properties;

  private Long rank; //각 층 순서
  private Boolean isPublic; //공개 비공개
  private Long buildingOrder; //건물 순서
}
